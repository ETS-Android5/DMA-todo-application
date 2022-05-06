package com.example.todoapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.todoapplication.Database.DB;
import com.example.todoapplication.Models.Todo;
import com.example.todoapplication.RecyclerView.TodoClickListener;
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

    public static final int NEW_TODO_REQUEST_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // hide the title bar
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoRecyclerView = findViewById(R.id.recycler_view);
        addTodoBtn = findViewById(R.id.todo_manager_intent_btn);

        // pass this as context
        database = DB.getInstance(this);

        fetchAllTodos();

        // onclick listener for add todo button
        addTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TodoManagerActivity.class);
                startActivityForResult(intent, NEW_TODO_REQUEST_CODE);
            }
        });
    }

    // Result from TodoManagerActivity activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==NEW_TODO_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                Todo newTodo = (Todo) data.getSerializableExtra(TodoManagerActivity.NEW_TODO_EXTRA);
                saveTodo(newTodo);
            }
        }
    }

    @Override
    // onResume is used so that all the list is updated when navigating back from todo manager activity
    protected void onResume(){
        super.onResume();
        fetchAllTodos();
    }

    public void fetchAllTodos(){
        // get all todo items and update the recycler view
        this.todoItems = database.todoDAO().fetchTodos();
        renderView(this.todoItems);

        // refetch the todo items from the database
        this.todoItems.clear();
        this.todoItems.addAll(database.todoDAO().fetchTodos());

        // re-render recycler view for the saved data
        todoListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "New todo created!", Toast.LENGTH_SHORT).show();
    }

    public void saveTodo(Todo newTodo){
        database.todoDAO().insert(newTodo);
    }

    private void renderView(List<Todo> todoItems){
        todoRecyclerView.setHasFixedSize(false);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // instantiate and set the adapter for recycler view
        todoListAdapter = new TodoListAdapter(MainActivity.this, todoItems, todoClickListener);
        todoRecyclerView.setAdapter(todoListAdapter);
    }

    private final TodoClickListener todoClickListener = new TodoClickListener() {
        @Override
        public void onClick(Todo todo) {
        }

        @Override
        public void onLongClick(Todo todo, CardView cardView) {
        }
    };
}