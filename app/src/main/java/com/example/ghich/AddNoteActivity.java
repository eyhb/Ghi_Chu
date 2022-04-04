package com.example.ghich;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddNoteActivity extends AppCompatActivity {
    EditText edTitle, edNote;
    DBHelper db;
    List<Label> listLabel;
    List<String> stringList;
    Note note;
    Toolbar toolbar;
    String inputNote;
    Intent intent;
    int noteId;
    String labelSelected = "";
    int labelPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Thêm ghi chú");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);
        note = new Note();
        edTitle = findViewById(R.id.edTitle);
        edNote = findViewById(R.id.edNote);
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
                finish();
                return true;
            case R.id.addLabel:
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this, R.style.Theme_AppCompat);
                builder.setTitle("Chọn nhãn");
                builder.setSingleChoiceItems(stringList.toArray(new String[stringList.size()]), labelPosition, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        labelSelected = stringList.get(which);
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast toast = Toast.makeText(AddNoteActivity.this, "Đã chọn", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                        toast.show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void add(View view) {
        inputNote = edNote.getText().toString().trim();
        if (!inputNote.equals("")) {
            note.setTitle(edTitle.getText().toString().trim());
            note.setNote(inputNote);
            note.setLabel(labelSelected);
            if (noteId == -1) {
                if (db.insertNote(note)) {
                    Toast toast = Toast.makeText(this, "Đã thêm", Toast.LENGTH_SHORT);
                    toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                    toast.show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast toast = Toast.makeText(this, "Lỗi! Thử lại sau.", Toast.LENGTH_SHORT);
                    toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                    toast.show();
                }
            } else {
                if (db.updateNote(note)) {
                    Toast toast = Toast.makeText(this, "Đã sửa", Toast.LENGTH_SHORT);
                    toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                    toast.show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast toast = Toast.makeText(this, "Lỗi! Thử lại sau.", Toast.LENGTH_SHORT);
                    toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                    toast.show();
                }
            }
        } else {
            edTitle.setText(note.getTitle());
            edNote.setText(note.getNote());
            Toast toast = Toast.makeText(this, "Ghi chú trống!", Toast.LENGTH_SHORT);
            toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
            toast.show();
        }
    }
}
