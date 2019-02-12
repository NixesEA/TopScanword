package ru.pushapp.scan;

public class GridItem {

    String question;
    char response;

    public GridItem(String question){
        this.question = question;
    }
    public GridItem(char response){
        this.response = response;
    }

    @Override
    public String toString() {
        return "Question = " + question + " Response = " + response;
    }
}
