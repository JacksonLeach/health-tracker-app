package com.example.healthapp.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Entry {

    //Fields
    private List<CategoryEntry> categories;
    private Date entryDate;
    private String entryText;
    private int mood;

    private String userId;

    public Entry() {
        categories = new ArrayList<>();
        entryDate = new Date();
        entryText = "";
    }

    //Getters
    public List<CategoryEntry> getCategories() {
        return categories;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public String getEntryText() {
        return entryText;
    }

    public int getMood() {
        return mood;
    }

    public String getUserId() {
        return userId;
    }

    //Setters
    public void setCategories(List<CategoryEntry> categories) {
        this.categories = categories;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
