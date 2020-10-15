package com.example.healthapp.data;

import java.util.Date;

public class Entry {

    //Fields
    private CategoryEntry[] categories;
    private Date entryDate;
    private String entryText;
    private int mood;

    public Entry() {
        categories = new CategoryEntry[3];
        entryDate = new Date();
        entryText = "";
    }

    //Getters
    public CategoryEntry[] getCategories() {
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

    //Setters
    public void setCategories(CategoryEntry[] categories) {
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

}
