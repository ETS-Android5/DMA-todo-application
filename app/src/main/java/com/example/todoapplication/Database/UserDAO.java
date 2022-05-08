package com.example.todoapplication.Database;

import static androidx.room.OnConflictStrategy.REPLACE;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.todoapplication.Models.TodoWithUser;
import com.example.todoapplication.Models.User;

import java.util.List;

@Dao
public interface UserDAO {

   @Insert(onConflict = REPLACE)
   void insert(User user);

   @Query("SELECT EXISTS(SELECT username FROM users WHERE username=:username)")
   boolean usernameIsTaken(String username);

   @Query("SELECT * FROM users WHERE password=:password AND username=:username")
   User getUserAccount(String username, String password);

   @Query("SELECT * FROM users WHERE id=:id")
   User getUserDetails(int id);

   @Transaction
   @Query("SELECT * FROM users WHERE id=:id")
   List<TodoWithUser> getUserTodos(int id);
}
