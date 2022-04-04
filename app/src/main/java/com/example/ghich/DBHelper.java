package com.example.ghich;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    public DBHelper(Context context) {
        super(context, "eyhb", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, note TEXT, label TEXT, dateTime TEXT, trash INTEGER)");
        db.execSQL("CREATE TABLE labels (id INTEGER PRIMARY KEY AUTOINCREMENT, label TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        db.execSQL("DROP TABLE IF EXISTS lables");
        onCreate(db);
    }

    public boolean insertLable(Label label) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Label> labelList = getAllLabels();
        String lb = label.getLabel();
        boolean check = true;
        for (Label labelLoop : labelList) {
            if (lb.equals(labelLoop.getLabel())) {
                check = false;
                break;
            }
        }
        if (check) {
            ContentValues values = new ContentValues();
            values.put("label", label.getLabel());
            if (db.insert("labels", null, values) < 0) {
                db.close();
                return false;
            } else {
                db.close();
                return true;
            }
        } else {
            db.close();
            return false;
        }
    }

    public boolean insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("note", note.getNote());
        values.put("label", note.getLabel());
        values.put("dateTime", note.getDateTime());
        values.put("trash", 0);
        if (db.insert("notes", null, values) < 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public boolean updateLabel(Label label, String oldLabel) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Label> labelList = getAllLabels();
        String lb = label.getLabel();
        boolean check = true;
        for (Label labelLoop : labelList) {
            if (lb.equals(labelLoop.getLabel())) {
                check = false;
                break;
            }
        }
        if (check) {
            ContentValues values = new ContentValues();
            List<Note> noteList = getAllNotesByLabel(oldLabel);
            values.put("id", label.getId());
            values.put("label", label.getLabel());
            if (db.update("labels", values, "id = ?", new String[]{String.valueOf(label.getId())}) < 0) {
                db.close();
                return false;
            } else {
                for (Note note : noteList) {
                    note.setLabel(label.getLabel());
                    updateNote(note);
                }
                db.close();
                return true;
            }
        } else {
            db.close();
            return false;
        }
    }

    public boolean updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("note", note.getNote());
        values.put("label", note.getLabel());
        values.put("dateTime", note.getDateTime());
        values.put("trash", note.getTrash());
        if (db.update("notes", values, "id = ?", new String[]{String.valueOf(note.getId())}) < 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public boolean deleteLabel(String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Note> list = getAllNotesByLabel(label);
        if (db.delete("labels", "label = ?", new String[]{label}) < 0) {
            db.close();
            return false;
        } else {
            for (Note note : list) {
                note.setLabel("");
                updateNote(note);
            }
            db.close();
            return true;
        }
    }

    public boolean deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db.delete("notes", "id = ?", new String[]{String.valueOf(id)}) < 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public List<Label> getAllLabels() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Label> list = new ArrayList<Label>();
        Cursor cursor = db.rawQuery("SELECT * FROM labels ORDER BY label", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Label label = new Label();
            label.setId(cursor.getInt(0));
            label.setLabel(cursor.getString(1));
            list.add(label);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<Note> getAllNotes(int trash) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Note> list = new ArrayList<Note>();
        Cursor cursor = db.query("notes", null, "trash = ?", new String[]{String.valueOf(trash)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setNote(cursor.getString(2));
            note.setLabel(cursor.getString(3));
            note.setDateTime(cursor.getString(4));
            note.setTrash(trash);
            list.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<Note> getAllNotesByLabel(String label) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Note> list = new ArrayList<Note>();
        Cursor cursor = db.query("notes", null, "label = ?", new String[]{label}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setNote(cursor.getString(2));
            note.setLabel(cursor.getString(3));
            note.setDateTime(cursor.getString(4));
            note.setTrash(cursor.getInt(5));
            list.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public Note getNotesById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Note note = new Note();
        Cursor cursor = db.query("notes", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            note.setId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setNote(cursor.getString(2));
            note.setLabel(cursor.getString(3));
            note.setDateTime(cursor.getString(4));
            note.setTrash(cursor.getInt(5));
            cursor.moveToNext();
        }
        cursor.close();
        return note;
    }
}
