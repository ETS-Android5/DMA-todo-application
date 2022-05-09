package com.example.todoapplication;

import android.widget.ImageView;

public class GridCategory {
    public String categoryName;
    public int totalItems;
    public int completedItems;

    public GridCategory(String categoryName, int totalItems, int completedItems) {
        this.categoryName = categoryName;
        this.totalItems = totalItems;
        this.completedItems = completedItems;
    }
}
