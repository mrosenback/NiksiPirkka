package com.example.shareyourbestadvice;

import java.io.Serializable;

public class Advice implements Serializable {

    private String advice;
    private String author;
    private String category;

    public Advice(String advice, String author, String category) {
        this.advice = advice;
        this.author = author;
        this.category = category;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
