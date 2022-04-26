package com.example.shareyourbestadvice;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "advices")
public class Advice implements Serializable {

    @PrimaryKey
    private int id;
    @NonNull
    private String category;
    @NonNull
    private String advice;
    @NonNull
    private String author;

    public Advice(int id, @NonNull String advice, @NonNull String author, @NonNull String category) {
        this.id = id;
        this.advice = advice;
        this.author = author;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getAdvice() {
        return advice;
    }

    public void setAdvice(@NonNull String advice) {
        this.advice = advice;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull String author) {
        this.author = author;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }
}
