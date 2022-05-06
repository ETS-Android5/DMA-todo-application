package com.example.todoapplication.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "todos")
public class Todo implements Serializable {

   @PrimaryKey(autoGenerate = true)
   int id;

   @ColumnInfo(name = "title")
   String title;

   @ColumnInfo(name = "description")
   String description;

   @ColumnInfo(name = "createdAt")
   String createdAt;

   @ColumnInfo(name = "isCompleted")
   boolean isCompleted;

   public Todo(){}

   public Todo(String title, String description, String createdAt, boolean isCompleted) {
      this.title = title;
      this.description = description;
      this.createdAt = createdAt;
      this.isCompleted = isCompleted;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
   }

   public boolean getCompleted() {
      return isCompleted;
   }

   public void setCompleted(boolean completed) {
      this.isCompleted = completed;
   }
}
