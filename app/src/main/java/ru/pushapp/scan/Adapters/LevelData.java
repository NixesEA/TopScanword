package ru.pushapp.scan.Adapters;

public class LevelData {

    String title;
    String description;
    int progress;
    boolean unblocked;

    String res;//content current level

    public LevelData(String title, String description, int progress, boolean unblocked, String res){
        this.title = title;
        this.description = description;
        this.progress = progress;
        this.unblocked = unblocked;

        this.res = res;
    }

    public boolean getUnblocked() {
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

    public String getRes() {
        return res;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnblocked(boolean unblocked) {
        this.unblocked = unblocked;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
