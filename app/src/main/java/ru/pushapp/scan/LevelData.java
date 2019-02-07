package ru.pushapp.scan;

public class LevelData {

    String title;
    String description;
    int progress;
    boolean unblocked;

    LevelData(String title, String description, int progress, boolean unblocked){
        this.title = title;
        this.description = description;
        this.progress = progress;
        this.unblocked = unblocked;
    }

    public boolean isUnblocked() {
        return unblocked;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getProgress() {
        return progress;
    }
}
