package ru.pushapp.scan;

public class LevelData {

    String title;
    String description;
    int progress;
    boolean unblocked;

    int res;//content current level

    LevelData(String title, String description, int progress, boolean unblocked, int res){
        this.title = title;
        this.description = description;
        this.progress = progress;
        this.unblocked = unblocked;

        this.res = res;
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

    public int getScene() {
        return res;
    }
}
