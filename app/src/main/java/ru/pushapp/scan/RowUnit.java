package ru.pushapp.scan;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RowUnit {
    @SerializedName("row")
    ArrayList<CellUnit> cellsInRow = new ArrayList<>();
}
