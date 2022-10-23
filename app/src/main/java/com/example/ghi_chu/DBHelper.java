package com.example.ghi_chu;

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
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "note TEXT," +
                "label TEXT," +
                "dateCreated TEXT," +
                "dateRemind TEXT," +
                "location TEXT," +
                "pinned BOOLEAN," +
                "archive BOOLEAN," +
                "trash BOOLEAN)");
        db.execSQL("CREATE TABLE labels (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "label TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        db.execSQL("DROP TABLE IF EXISTS lables");
        onCreate(db);
    }

    public boolean insertNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setContentValues(note);
        if (db.insert("notes", null, values) < 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public boolean updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setContentValues(note);
        if (db.update("notes", values, "id = ?", new String[]{String.valueOf(note.getId())}) < 0) {
            db.close();
            return false;
        } else {
            db.close();
            return true;
        }
    }

    private ContentValues setContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("note", note.getNote());
        values.put("label", note.getLabel());
        values.put("dateCreated", note.getDateCreated());
        values.put("dateRemind", note.getDateRemind());
        values.put("location", note.getLocation());
        values.put("pinned", note.getPinned());
        values.put("archive", note.getArchive());
        values.put("trash", note.getTrash());
        return values;
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

    public List<Note> getAllNotes(boolean trash) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "trash = ?", new String[]{String.valueOf(trash)}, null, null, null);
        return getList(cursor);
    }

    public List<Note> getAllNotesByLabel(String label) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "label = ?", new String[]{label}, null, null, null);
        return getList(cursor);
    }

    public Note getNotesById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        return getList(cursor).get(0);
    }

    private List<Note> getList(Cursor cursor) {
        List<Note> list = new ArrayList<Note>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setNote(cursor.getString(2));
            note.setLabel(cursor.getString(3));
            note.setDateCreate(cursor.getString(4));
            note.setDateRemind(cursor.getString(5));
            note.setLocation(cursor.getString(6));
            note.setPinned(Boolean.parseBoolean(String.valueOf(cursor.getInt(7))));
            note.setArchive(Boolean.parseBoolean(String.valueOf(cursor.getInt(8))));
            note.setTrash(Boolean.parseBoolean(String.valueOf(cursor.getInt(9))));
            list.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
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
}
