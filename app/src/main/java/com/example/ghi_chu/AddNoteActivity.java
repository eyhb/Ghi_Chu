package com.example.ghi_chu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ghich.R;

import java.util.ArrayList;
import java.util.List;

public class AddNoteActivity extends AppCompatActivity {
    CustomEditText edTitle, edNote;
    DBHelper db;
    List<Label> listLabel;
    List<String> stringList;
    Note note;
    Toolbar topToolbar;
    String inputTitle, inputNote;
    int noteId;
    String labelSelected = "";
    int labelPosition = 0;
    LinearLayout tagField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setTopToolbar();
        noteId = getIntent().getIntExtra("noteId", -1);
        note = new Note();
        db = new DBHelper(this);
        if (noteId != -1) {
            note = db.getNotesById(noteId);
        }
        setEditText();
//        getListLabel();
    }

    public void setTopToolbar() {
        topToolbar = findViewById(R.id.topToolbar);
//        topToolbar.setTitleTextColor(Color.WHITE);
        topToolbar.setTitle("");
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setEditText() {
        edTitle = findViewById(R.id.edTitle);
        edNote = findViewById(R.id.edNote);
        if (noteId != -1) {
            edTitle.setText(note.getTitle());
            edNote.setText(note.getNote());
        }

        // Focus to Title EditText and show keyboard
        edTitle.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void getListLabel() {
        listLabel = new ArrayList<Label>();
        stringList = new ArrayList<String>();
        stringList.add("");
        db = new DBHelper(this);
        listLabel = db.getAllLabels();
        for (Label label : listLabel)
            stringList.add(label.getLabel());
        if (noteId != -1) {
            note = db.getNotesById(noteId);
            edTitle.setText(note.getTitle());
            edNote.setText(note.getNote());
            if (!note.getLabel().equals("") && note.getLabel() != null) {
                for (Label label : listLabel) {
                    labelPosition++;
                    if (labelSelected.equals(label.getLabel())) {
                        break;
                    }
                }
                if (labelPosition == listLabel.size())
                    labelPosition = 0;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        if (noteId != -1) {
            db = new DBHelper(this);
            note = db.getNotesById(noteId);
            if (note.getPinned() == 1) {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pinned));
            }
            if (note.getArchive() == 1) {
                menu.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unarchive));
            }
        } else {
            note.setPinned(0);
            note.setArchive(0);
            note.setTrash(0);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                add();
                finish();
                return true;
            case R.id.archive:
                    if (note.getArchive() == 0 || note.getArchive() == null) {
                        note.setArchive(1);
                        item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unarchive));
                        setToast(this, "Đã lưu").show();
                    } else {
                        note.setArchive(0);
                        item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_archive));
                        setToast(this, "Đã bỏ lưu").show();
                    }
                return true;
            case R.id.reminder:
                setToast(this, "Thông báo").show();
                return true;
            case R.id.pin:
                if (note.getPinned() == 0 || note.getPinned() == null) {
                    note.setPinned(1);
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pinned));
                    setToast(this, "Đã gim").show();
                } else {
                    note.setPinned(0);
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pin));
                    setToast(this, "Đã bỏ gim").show();
                }
                return true;
            case R.id.addLabel:
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this, R.style.Theme_AppCompat);
                builder.setTitle("Chọn nhãn");
                builder.setSingleChoiceItems(stringList.toArray(new String[stringList.size()]),
                        labelPosition,
                        (dialog, which) -> labelSelected = stringList.get(which));
                builder.setPositiveButton("OK", (dialog, which) -> {
                    setToast(AddNoteActivity.this, "Đã chọn").show();
                    dialog.dismiss();
                });
                builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Add note when press back button to close Activity
    @Override
    public void onBackPressed() {
        add();
        super.onBackPressed();
    }

    // Add note to database
    public void add() {
        inputTitle = edTitle.getText().toString().trim();
        inputNote = edNote.getText().toString().trim();

        // Check the note is empty or not
        if (!(inputTitle.equals("") && inputNote.equals(""))) {
            note.setTitle(inputTitle);
            note.setNote(inputNote);
            note.setLabel("");

            // Check the existence of note
            if (noteId == -1) {

                // Insert new note
                if (db.insertNote(note)) {
                    setToast(this, "Đã thêm").show();
                    setResult(Activity.RESULT_OK); // Send result to refresh List<Note> in NotesFragment
                } else {
                    setToast(this, "Lỗi! Thử lại sau.").show();
                }
            } else {

                // Update note
                if (db.updateNote(note)) {
                    setToast(this, "Đã sửa").show();
                    setResult(Activity.RESULT_OK); // Send result to refresh List<Note> in NotesFragment
                } else {
                    setToast(this, "Lỗi! Thử lại sau.").show();
                }
            }
        } else {
            edTitle.setText(note.getTitle());
            edNote.setText(note.getNote());
            setToast(this, "Ghi chú trống!").show();
        }
    }

    public Toast setToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setTextColor(Color.DKGRAY);
        return toast;
    }
}
