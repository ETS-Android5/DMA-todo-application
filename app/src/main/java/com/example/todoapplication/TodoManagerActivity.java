package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.todoapplication.Models.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TodoManagerActivity extends AppCompatActivity implements  View.OnClickListener{

    EditText todoTitle, todoDescription;
    FloatingActionButton addTodoBtn;
    Button deleteTodoBtn;
    Spinner todoCategoryDropdown;

    String title, description = "";
    Todo todo;
    String todoCategory;
    boolean isOldTodo;

    public static final String NEW_TODO_EXTRA = "new_todo";
    public static final String UPDATE_TODO_EXTRA = "update_todo";
    public static final String DELETE_TODO_EXTRA = "delete_todo";

    public static final List<String> TODO_CATEGORIES = new ArrayList<String>(Arrays.asList("Home", "Work", "School", "Shopping"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_manager);

        todoTitle = findViewById(R.id.todo_title_input);
        todoDescription = findViewById(R.id.todo_description_input);
        addTodoBtn = findViewById(R.id.add_todo_btn);
        deleteTodoBtn = findViewById(R.id.delete_todo_btn);
        todoCategoryDropdown = findViewById(R.id.todo_category_dropdown);

        // populate dropdown with values from the TODO_CATEGORY list
        populateDropdown();

        // on item change listener for the category dropdown menu
        todoCategoryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TodoManagerActivity.this.todoCategory = TODO_CATEGORIES.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // check if the activity is launched for creating a todo or updating one
        if(getIntent().getSerializableExtra(UPDATE_TODO_EXTRA)!=null){
            todo = (Todo) getIntent().getSerializableExtra(UPDATE_TODO_EXTRA);
            isOldTodo = true;
            setOldValues();

            // set dropdown value
            int dropdownItemPosition = TODO_CATEGORIES.indexOf(todo.getCategory());
            todoCategoryDropdown.setSelection(dropdownItemPosition);

            // set the delete button's visibility to visible
            deleteTodoBtn.setVisibility(View.VISIBLE);

            // change title to update todo
            this.setTitle("Update Todo");
        }

        // on click listeners for add/update and delete buttons
        deleteTodoBtn.setOnClickListener(this);
        addTodoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        Intent intent = new Intent();

        switch (viewId){
            case R.id.add_todo_btn:
                this.title = todoTitle.getText().toString();

                String todoText = todoDescription.getText().toString();
                this.description = todoText.isEmpty()?"":todoText;

                // validate title
                if(title.isEmpty()){
                    Toast.makeText(TodoManagerActivity.this, "The title cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent.putExtra(isOldTodo?UPDATE_TODO_EXTRA:NEW_TODO_EXTRA, isOldTodo?updateTodo():createNewTodo());
                break;

            case R.id.delete_todo_btn:
                intent.putExtra(DELETE_TODO_EXTRA, this.todo);
                break;

            default:
                return;
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    public void populateDropdown(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.selected_dropdown_item, TODO_CATEGORIES);
        adapter.setDropDownViewResource(R.layout.dropdown_list_items);
        this.todoCategoryDropdown.setAdapter(adapter);

    }

    public void setOldValues(){
        this.todoTitle.setText(this.todo.getTitle());
        this.todoDescription.setText(this.todo.getDescription());
    }

    public Todo updateTodo(){
        this.todo.setTitle(this.title);
        this.todo.setDescription(this.description);
        this.todo.setCategory(this.todoCategory);

        return this.todo;
    }

    public Todo createNewTodo(){
        // parse the current date to string since room DB cannot store dates directly
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String createdAt = simpleDateFormat.format(new Date());

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(LoginActivity.USER_ID, -1);

        // create the new todo object
        Todo newTodo = new Todo(title, description, createdAt, false, userId, todoCategory);

        return newTodo;
    }
}