package com.hcmus.easywork.models;

public class ItemHomeSummary {
    private int background;
    private String title;
    private int count;

    public ItemHomeSummary(int background, String title, int count) {
        this.background = background;
        this.title = title;
        this.count = count;
    }

    public int getBackground() {
        return background;
    }

    public String getTitle() {
        return title;
    }

    public String getCount() {
        return Integer.toString(count);
    }
}
