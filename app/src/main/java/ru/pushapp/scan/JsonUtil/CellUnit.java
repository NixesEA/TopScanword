package ru.pushapp.scan.JsonUtil;

import com.google.gson.annotations.SerializedName;


public class CellUnit {

    @SerializedName("letter")
    private String letter;

    @SerializedName("question")
    private String question;

    @SerializedName("way")
    private int way;

    // Getter Methods
    public String getLetter() {
        return letter;
    }

    public String getQuestion() {
        return question;
    }

    public int getWay() {
        return way;
    }


    // Setter Methods
    public void setLetter(String letter) {
        this.letter = letter;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setWay(int way) {
        this.way = way;
    }

}
