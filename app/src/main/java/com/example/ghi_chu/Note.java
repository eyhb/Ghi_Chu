package com.example.ghi_chu;

public class Note {
    Integer id;
    Boolean pinned, archive, trash;
    String title, note, label, dateCreated, dateRemind, location;

    public Note() {
    }

    public Note(Integer id,
                Boolean pinned,
                Boolean archive,
                Boolean trash,
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

    public boolean getPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean getArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public boolean getTrash() {
        return trash;
    }

    public void setTrash(boolean trash) {
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
