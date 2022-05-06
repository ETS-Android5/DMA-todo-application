package com.example.todoapplication.RecyclerView;

import androidx.cardview.widget.CardView;

import com.example.todoapplication.Models.Todo;

public interface TodoClickListener {
    void onClick(Todo todo);
    void onLongClick(Todo todo, CardView cardView);
}
