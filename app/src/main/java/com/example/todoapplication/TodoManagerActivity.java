package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todoapplication.Models.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoManagerActivity extends AppCompatActivity {

    EditText todoTitle, todoDescription;
    FloatingActionButton addTodoBtn;

    String title, description;
    Todo todo;
    boolean isOldTodo;

    public static final String NEW_TODO_EXTRA = "new_todo";
    public static final String UPDATE_TODO_EXTRA = "update_todo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_manager);

        todoTitle = findViewById(R.id.todo_title_input);
        todoDescription = findViewById(R.id.todo_description_input);
        addTodoBtn = findViewById(R.id.add_todo_btn);

        // check if the activity is launched for creating a todo or updating one
        if(getIntent().getSerializableExtra(UPDATE_TODO_EXTRA)!=null){
            todo = (Todo) getIntent().getSerializableExtra(UPDATE_TODO_EXTRA);
            isOldTodo = true;
            setOldValues();
        }

        addTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TodoManagerActivity.this.title = todoTitle.getText().toString();
                TodoManagerActivity.this.description = todoDescription.getText().toString();

                // validate title
                if(title.isEmpty()){
                    Toast.makeText(TodoManagerActivity.this, "The title cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //result intent
                Intent intent = new Intent();
                intent.putExtra(isOldTodo?UPDATE_TODO_EXTRA:NEW_TODO_EXTRA, isOldTodo?updateTodo():createNewTodo());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    public void setOldValues(){
        this.todoTitle.setText(this.todo.getTitle());
        this.todoDescription.setText(this.todo.getDescription());
    }

    public Todo updateTodo(){
        this.todo.setTitle(this.title);
        this.todo.setDescription(this.description);

        return this.todo;
    }

    public Todo createNewTodo(){
        // parse the current date to string since room DB cannot store dates directly
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String createdAt = simpleDateFormat.format(new Date());

        // create the new todo object
        Todo newTodo = new Todo(title, description, createdAt, false);

        return newTodo;
    }
}