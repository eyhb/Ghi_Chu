package com.example.ghi_chu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.EditText;
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
    Toolbar topToolbar, bottomToolbar;
    String inputNote;
    int noteId;
    String labelSelected = "";
    int labelPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setTopToolbar();
        setBottomToolbar();
        setEditText();
        noteId = getIntent().getIntExtra("noteId", -1);
        note = new Note();
        getListLabel();
    }

    public void setTopToolbar() {
        topToolbar = findViewById(R.id.topToolbar);
//        topToolbar.setTitleTextColor(Color.WHITE);
        topToolbar.setTitle("");
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setBottomToolbar() {
        bottomToolbar = findViewById(R.id.bottomToolbar);
    }

    public void setEditText() {
        edTitle = findViewById(R.id.edTitle);
        edNote = findViewById(R.id.edNote);
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
            labelSelected = note.getLabel();
            edTitle.setText(note.getTitle());
            edNote.setText(note.getNote());
            if (!labelSelected.equals("")) {
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
                setToast(this, "Đã lưu").show();
                return true;
            case R.id.reminder:
                setToast(this, "Thông báo").show();
                return true;
            case R.id.pin:
                setToast(this, "Đã gim").show();
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

    // Add note
    public void add() {
        inputNote = edNote.getText().toString().trim();
        if (!inputNote.equals("")) {
            note.setTitle(edTitle.getText().toString().trim());
            note.setNote(inputNote);
            note.setLabel(labelSelected);
            if (noteId == -1) {
                if (db.insertNote(note)) {
                    setToast(this, "Đã thêm").show();
                    setResult(Activity.RESULT_OK); // Send result to refresh List<Note> in NotesFragment
                } else {
                    setToast(this, "Lỗi! Thử lại sau.").show();
                }
            } else {
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
