package com.example.todoapplication.Models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TodoWithUser {

    @Embedded
    public User user;

    @Relation(parentColumn = "id", entityColumn = "userId")
    public List<Todo> todoItems;

    public TodoWithUser(User user, List<Todo> todoItems){
        this.user = user;
        this.todoItems = todoItems;
    }

}
