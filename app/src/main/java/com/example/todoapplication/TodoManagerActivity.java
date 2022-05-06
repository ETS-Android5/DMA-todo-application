package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    public static final String NEW_TODO_EXTRA = "new_todo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_manager);

        todoTitle = findViewById(R.id.todo_title_input);
        todoDescription = findViewById(R.id.todo_description_input);
        addTodoBtn = findViewById(R.id.add_todo_btn);

        addTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = todoTitle.getText().toString();
                String description = todoDescription.getText().toString();

                // validate title
                if(title.isEmpty()){
                    Toast.makeText(TodoManagerActivity.this, "The title cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // parse the current date to string since room DB cannot store dates directly
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String createdAt = simpleDateFormat.format(new Date());

                // create the new todo object
                Todo newTodo = new Todo(title, description, createdAt, false);

                //result intent
                Intent intent = new Intent();
                intent.putExtra(NEW_TODO_EXTRA, newTodo);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}