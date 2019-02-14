package ru.pushapp.scan.JsonUtil;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ObjectJSON {

    @SerializedName("questions")
    public ArrayList<RowUnit> rows = new ArrayList<>();

}
