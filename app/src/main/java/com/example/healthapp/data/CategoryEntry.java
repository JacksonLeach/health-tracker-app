package com.example.healthapp.data;

public class CategoryEntry {

    private String entryText;
    private int mood;
    private String categoryTitle;

    public CategoryEntry() {
        entryText = "";
        mood = -1;
        categoryTitle = "";
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }


}
