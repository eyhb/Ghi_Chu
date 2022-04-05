package com.example.ghi_chu;

public class Label {
    int id;
    String label;

    public Label() {
    }

    public Label(String label) {
        this.label = label;
    }

    public Label(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
