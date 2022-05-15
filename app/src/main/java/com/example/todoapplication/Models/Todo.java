package com.example.todoapplication.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "todos")
public class Todo implements Serializable {

   @PrimaryKey(autoGenerate = true)
   public int id;

   @ColumnInfo(name="userId")
   public int userId;

   @ColumnInfo(name="collaboratorId")
   public int collaboratorId;

   @ColumnInfo(name = "title")
   public String title;

   @ColumnInfo(name = "description")
   public String description;

   @ColumnInfo(name = "createdAt")
   public String createdAt;

   @ColumnInfo(name="category")
   public String category;

   @ColumnInfo(name = "isCompleted")
   public boolean isCompleted;

   public Todo(){}

   public Todo(String title, String description, String createdAt, boolean isCompleted, int userId, String category, int collaboratorId) {
      this.title = title;
      this.description = description;
      this.createdAt = createdAt;
      this.isCompleted = isCompleted;
      this.userId = userId;
      this.category = category;
      this.collaboratorId = collaboratorId;
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

   public int getCollaboratorId(){
      return this.collaboratorId;
   }

   public void setCollaboratorId(int id){
      this.collaboratorId = id;
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

   public void setCategory(String category){
      this.category = category;
   }

   public String getCategory(){
      return this.category;
   }

   public void setCompleted(boolean completed) {
      this.isCompleted = completed;
   }
}
