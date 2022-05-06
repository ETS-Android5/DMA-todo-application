package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.todoapplication.Database.DB;
import com.example.todoapplication.Models.Todo;
import com.example.todoapplication.RecyclerView.TodoListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView todoRecyclerView;
    TodoListAdapter todoListAdapter;

    List<Todo> todoItems = new ArrayList<Todo>();
    DB database;
    FloatingActionButton addTodoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoRecyclerView = findViewById(R.id.recycler_view);
        addTodoBtn = findViewById(R.id.add_todo_btn);

        // pass this as context
        database = DB.getInstance(this);

        // get all todo items and update the recycler view
        this.todoItems = database.todoDAO().fetchTodos();

        renderView(todoItems);
    }

    private void renderView(List<Todo> todoItems){
        todoRecyclerView.setHasFixedSize(false);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // instantiate and set the adapter for recycler view
        todoListAdapter = new TodoListAdapter(MainActivity.this, todoItems);
        todoRecyclerView.setAdapter(todoListAdapter);
    }
}