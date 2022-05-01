package com.example.shareyourbestadvice;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Category implements Serializable {

    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
