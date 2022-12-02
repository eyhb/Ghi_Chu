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
                "pinned INTEGER," +
                "archive INTEGER," +
                "trash INTEGER)");
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

    public List<Note> getAllNotes(Boolean trash) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "trash = ?", new String[]{String.valueOf((trash) ? 1 : 0)}, null, null, null);
        return getListNote(cursor);
    }

    public List<Note> getAllNotesByLabel(String label) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "label = ?", new String[]{label}, null, null, null);
        return getListNote(cursor);
    }

    public Note getNotesById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("notes", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        return getListNote(cursor).get(0);
    }

    private List<Note> getListNote(Cursor cursor) {
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
            note.setPinned(cursor.getInt(7));
            note.setArchive(cursor.getInt(8));
            note.setTrash(cursor.getInt(9));
            list.add(note);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public boolean insertLabel(Label label) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Label> labelList = getAllLabels();
        if (checkLabel(label, labelList)) {
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
        if (checkLabel(label, labelList)) {
            ContentValues values = new ContentValues();
            List<Note> noteList = getAllNotesByLabel(oldLabel);
            values.put("id", label.getId());
            values.put("label", label.getLabel());
            if (db.update("labels", values, "id = ?", new String[]{String.valueOf(label.getId())}) < 0) {
                db.close();
                return false;
            } else {
                // Update all label exist in all note
                updateLabelInAllNote(noteList, label.getLabel());
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
            // Delete all label exist in all note
            updateLabelInAllNote(list, "");
            db.close();
            return true;
        }
    }

    private void updateLabelInAllNote(List<Note> noteList, String label) {
        for (Note note : noteList) {
            note.setLabel(label);
            updateNote(note);
        }
    }

    // Check if label exists or not
    private boolean checkLabel(Label label, List<Label> labelList) {
        String lb = label.getLabel();
        boolean check = true;
        for (Label labelLoop : labelList) {
            if (lb.equals(labelLoop.getLabel())) {
                check = false;
                break;
            }
        }
        return check;
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
