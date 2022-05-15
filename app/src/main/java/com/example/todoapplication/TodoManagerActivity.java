package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapplication.Database.DB;
import com.example.todoapplication.Models.Todo;
import com.example.todoapplication.Utils.DialogBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TodoManagerActivity extends AppCompatActivity implements  View.OnClickListener{

    EditText todoTitle, todoDescription, collaborationInput;
    FloatingActionButton addTodoBtn;
    Button deleteTodoBtn, removeCollaboratorBtn;
    Spinner todoCategoryDropdown;
    TextView actionBarTitle;
    ImageView goBackIcon;
    LinearLayout removeCollaboratorContainer;

    String title, description, collaboratorUsername = "";
    Todo todo;
    int collaboratorId;
    String todoCategory;
    boolean isOldTodo;

    Dialog dialog;

    DB database;

    public static final String NEW_TODO_EXTRA = "new_todo";
    public static final String UPDATE_TODO_EXTRA = "update_todo";
    public static final String DELETE_TODO_EXTRA = "delete_todo";

    public static final List<String> TODO_CATEGORIES = new ArrayList<String>(Arrays.asList("Home", "Work", "School", "Shopping"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_manager);
        getSupportActionBar().hide();

        todoTitle = findViewById(R.id.todo_title_input);
        todoDescription = findViewById(R.id.todo_description_input);
        collaborationInput = findViewById(R.id.todo_collaborator_input);
        addTodoBtn = findViewById(R.id.add_todo_btn);
        removeCollaboratorBtn = findViewById(R.id.remove_collaborator_btn);
        deleteTodoBtn = findViewById(R.id.delete_todo_btn);
        todoCategoryDropdown = findViewById(R.id.todo_category_dropdown);
        actionBarTitle = findViewById(R.id.action_bar_title);
        goBackIcon = findViewById(R.id.go_back_icon);
        removeCollaboratorContainer = findViewById(R.id.remove_collaborator_container);

        dialog = new Dialog(this);

        database = DB.getInstance(this);

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
            actionBarTitle.setText("Update Todo");
            setOldValues();

            // set dropdown value
            int dropdownItemPosition = TODO_CATEGORIES.indexOf(todo.getCategory());
            todoCategoryDropdown.setSelection(dropdownItemPosition);

            if(todo.getCollaboratorId()!=fetchUserId()){
                // set the delete button's visibility to visible
                deleteTodoBtn.setVisibility(View.VISIBLE);

                // show option to remove collaborator if one exists
                checkCollabortorStatus();
            }else{
                collaborationInput.setVisibility(View.GONE);
            }

            // change title to update todo
            this.setTitle("Update Todo");
        }

        // on click listeners for add/update and delete buttons
        removeCollaboratorBtn.setOnClickListener(this);
        deleteTodoBtn.setOnClickListener(this);
        addTodoBtn.setOnClickListener(this);
        goBackIcon.setOnClickListener(this);

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

                String collaboratorText = collaborationInput.getText().toString();
                this.collaboratorUsername = collaboratorText.isEmpty()?null:collaboratorText;

                // validate title
                if(title.isEmpty()){
                    Toast.makeText(TodoManagerActivity.this, "The title cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check collaborator
                if(this.collaboratorUsername!=null){
                    int id = database.userDAO().getIdByUsername(collaboratorUsername);
                    if(id==0){
                        Toast.makeText(this, "Invalid collaborator", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(this.todo!=null){
                        this.todo.setCollaboratorId(id);
                    }else{
                        this.collaboratorId = id;
                    }
                }

                intent.putExtra(isOldTodo?UPDATE_TODO_EXTRA:NEW_TODO_EXTRA, isOldTodo?updateTodo():createNewTodo());
                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.delete_todo_btn:
                showCustomDialog();
                break;

            case R.id.go_back_icon:
                finish();
                break;

            case R.id.remove_collaborator_btn:
                this.todo.setCollaboratorId(0);
                checkCollabortorStatus();
                break;

            default:
                return;
        }
    }

    public void checkCollabortorStatus(){
        if(this.todo.getCollaboratorId()!=0){
            removeCollaboratorContainer.setVisibility(View.VISIBLE);
            collaborationInput.setVisibility(View.GONE);

        }else{
            removeCollaboratorContainer.setVisibility(View.GONE);
            collaborationInput.setVisibility(View.VISIBLE);
        }
    }

    public void showCustomDialog(){
        DialogBox dialogBox = new DialogBox(this);
        dialogBox.show();

    }

    public void deleteTodo(){
        Intent intent = new Intent();
        intent.putExtra(DELETE_TODO_EXTRA, this.todo);
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
        Todo newTodo = new Todo(title, description, createdAt, false, userId, todoCategory, collaboratorId);

        return newTodo;
    }

    private int fetchUserId(){
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(LoginActivity.USER_ID, -1);
        return userId;
    }
}