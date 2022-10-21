package com.example.ghi_chu;

public class Note {
    int id, pinned, archive, trash;
    String title, note, label, dateCreated, dateRemind, location;

    public Note() {
    }

    public Note(int id,
                int pinned,
                int archive,
                int trash,
                String title,
                String note,
                String label,
                String dateCreated,
                String dateRemind,
                String location) {
        this.id = id;
        this.pinned = pinned;
        this.archive = archive;
        this.trash = trash;
        this.title = title;
        this.note = note;
        this.label = label;
        this.dateCreated = dateCreated;
        this.dateRemind = dateRemind;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPinned() {
        return pinned;
    }

    public void setPinned(int pinned) {
        this.pinned = pinned;
    }

    public int getArchive() {
        return archive;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    public int getTrash() {
        return trash;
    }

    public void setTrash(int trash) {
        this.trash = trash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreate(String dateTime) {
        this.dateCreated = dateCreated;
    }

    public String getDateRemind() {
        return dateRemind;
    }

    public void setDateRemind(String dateRemind) {
        this.dateRemind = dateRemind;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
