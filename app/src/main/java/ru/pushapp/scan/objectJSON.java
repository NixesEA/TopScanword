package ru.pushapp.scan;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class objectJSON {

    @SerializedName("questions")
    ArrayList<RowUnit> rows = new ArrayList<>();

}
