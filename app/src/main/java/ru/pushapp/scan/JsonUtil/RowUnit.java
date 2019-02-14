package ru.pushapp.scan.JsonUtil;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RowUnit {
    @SerializedName("row")
    public ArrayList<CellUnit> cellsInRow = new ArrayList<>();
}
