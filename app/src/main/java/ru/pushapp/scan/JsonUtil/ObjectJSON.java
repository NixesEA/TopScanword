package ru.pushapp.scan.JsonUtil;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ObjectJSON {
    @SerializedName("number")
    public int number;
    @SerializedName("theme")
    public String theme;
    @SerializedName("unblocked")
    public boolean unblocked;
    @SerializedName("progress")
    public int progress;

    @SerializedName("questions")
    public ArrayList<RowUnit> rows = new ArrayList<>();

}
