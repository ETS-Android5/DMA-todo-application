package com.example.todoapplication.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todoapplication.Models.Todo;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TodoDAO {

   // replace with new item on conflict
   @Insert(onConflict = REPLACE)
   void insert(Todo todo);

   @Query("SELECT * FROM todos ORDER BY id DESC")
   List<Todo> fetchTodos();

   @Update
   void updateTodo(Todo todo);

   @Delete
   void deleteTodo(Todo todo);
}
