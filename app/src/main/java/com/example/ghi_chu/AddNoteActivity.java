package com.example.ghi_chu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ghich.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        // Get the note's value
        noteId = getIntent().getIntExtra("noteId", -1);
        note = new Note();
        db = new DBHelper(this);
        edTitle = findViewById(R.id.edTitle);
        edNote = findViewById(R.id.edNote);
        if (noteId != -1) {
            note = db.getNotesById(noteId);
            edTitle.setText(note.getTitle());
            edNote.setText(note.getNote());
        }
        focusToEditText();
//        getListLabel();
    }

    public void setTopToolbar() {
        topToolbar = findViewById(R.id.topToolbar);
//        topToolbar.setTitleTextColor(Color.WHITE);
        topToolbar.setTitle("");
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void focusToEditText() {
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

        // Check the existence of the note
        if (noteId != -1) {
            db = new DBHelper(this);
            note = db.getNotesById(noteId);

            // Set the pin icon to pinned icon if note already pinned
            if (note.getPinned() == 1) {
                // position of pin icon in add_note_menu is 0
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pinned));
            }

            // Set the archive icon to un archive icon if note already archived
            if (note.getArchive() == 1) {
                // position of archive icon in add_note_menu is 2
                menu.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unarchive));
            }
        } else {

            // Set the pin, archive, trash value of note to false (0) if it didn't exist in database
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
                    if (note.getArchive() == 0) {
                        note.setArchive(1);
                        item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unarchive));
                        showToast(this, "Đã lưu");
                    } else {
                        note.setArchive(0);
                        item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_archive));
                        showToast(this, "Đã bỏ lưu");
                    }
                return true;
            case R.id.reminder:
                Date currentTime = Calendar.getInstance().getTime();
                showToast(this, String.valueOf(currentTime));
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return true;
            case R.id.pin:
                if (note.getPinned() == 0) {
                    note.setPinned(1);
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pinned));
                    showToast(this, "Đã ghim");
                } else {
                    note.setPinned(0);
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_pin));
                    showToast(this, "Đã bỏ ghim");
                }
                return true;
            case R.id.addLabel:
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this, R.style.Theme_AppCompat);
                builder.setTitle("Chọn nhãn");
                builder.setSingleChoiceItems(stringList.toArray(new String[stringList.size()]),
                        labelPosition,
                        (dialog, which) -> labelSelected = stringList.get(which));
                builder.setPositiveButton("OK", (dialog, which) -> {
                    showToast(AddNoteActivity.this, "Đã chọn");
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

                // Insert the new note if it didn't exist
                if (db.insertNote(note)) {
                    showToast(this, "Đã thêm");
                    setResult(Activity.RESULT_OK); // Send result to refresh List<Note> in NotesFragment
                } else {
                    showToast(this, "Lỗi! Thử lại sau.");
                }
            } else {

                // Update note if it already exists
                if (db.updateNote(note)) {
                    showToast(this, "Đã sửa");
                    setResult(Activity.RESULT_OK); // Send result to refresh List<Note> in NotesFragment
                } else {
                    showToast(this, "Lỗi! Thử lại sau.");
                }
            }
        } else {
            edTitle.setText(note.getTitle());
            edNote.setText(note.getNote());
            showToast(this, "Ghi chú trống!");
        }
    }

    private void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setTextColor(Color.DKGRAY);
        toast.show();
    }

    // Test DatePicker, but it actually works. So bo^'i ro^'i!
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Toast toast = Toast.makeText(getContext(), month + 1 + "/" + dayOfMonth + "/" + year, Toast.LENGTH_SHORT);
            View v = toast.getView();
            TextView textView = v.findViewById(android.R.id.message);
            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setTextColor(Color.DKGRAY);
            toast.show();
        }
    }
}
