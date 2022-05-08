package com.example.todoapplication.Database;

import static androidx.room.OnConflictStrategy.REPLACE;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.todoapplication.Models.Todo;

import java.util.List;

@Dao
public interface TodoDAO {

   // replace with new item on conflict
   @Insert(onConflict = REPLACE)
   void insert(Todo todo);

   @Query("SELECT * FROM todos ORDER BY id DESC")
   List<Todo> fetchTodos();

   @Query("UPDATE todos SET title=:title, description=:description, category=:category WHERE id=:id")
   void updateTodo(int id, String title, String description, String category);

   @Query("UPDATE todos SET isCompleted=:newStatus WHERE id=:id")
   void toggleTodo(int id, boolean newStatus);

   @Delete
   void deleteTodo(Todo todo);
}
