package com.example.todoapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapplication.Database.DB;
import com.example.todoapplication.Models.Todo;
import com.example.todoapplication.Models.TodoWithUser;
import com.example.todoapplication.Models.User;
import com.example.todoapplication.RecyclerView.TodoClickListener;
import com.example.todoapplication.RecyclerView.TodoListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    RecyclerView todoRecyclerView;
    TodoListAdapter todoListAdapter;
    SearchView searchView;
    TextView userGreeting;
    ImageButton logoutBtn;

    List<Todo> todoItems = new ArrayList<Todo>();
    DB database;
    FloatingActionButton addTodoBtn;

    int loggedInUserId;

    String searchQuery = "";

    public static final int NEW_TODO_REQUEST_CODE = 201;
    public static final int UPDATE_TODO_REQUEST_CODE = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // hide the title bar
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoRecyclerView = findViewById(R.id.recycler_view);
        addTodoBtn = findViewById(R.id.todo_manager_intent_btn);
        searchView = findViewById(R.id.search_todo);
        userGreeting = findViewById(R.id.user_greeting);
        logoutBtn = findViewById(R.id.log_out_btn);

        // get intent details sent from login/splash screen
        Intent initialIntent = getIntent();
        loggedInUserId = fetchUserId();

        // pass this as context when initializing database object
        database = DB.getInstance(this);
        fetchUserDetails();
        fetchAllTodos();

        // onclick listener for add buttons
        addTodoBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        // listeners for search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MainActivity.this.searchQuery = newText;
                filterItems();
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId){
            case R.id.todo_manager_intent_btn:
                Intent intent = new Intent(MainActivity.this, TodoManagerActivity.class);
                startActivityForResult(intent, NEW_TODO_REQUEST_CODE);
                break;

            case R.id.log_out_btn:
                // delete the user id key in shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF, MODE_PRIVATE);
                sharedPreferences.edit().remove(LoginActivity.USER_ID).commit();

                // redirect to login screen
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();

                break;

            default:
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAllTodos();
    }

    // Result from TodoManagerActivity activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case NEW_TODO_REQUEST_CODE:
                    Todo newTodo = (Todo) data.getSerializableExtra(TodoManagerActivity.NEW_TODO_EXTRA);
                    saveTodo(newTodo);
                    break;

                case UPDATE_TODO_REQUEST_CODE:
                    try {
                        Todo updatedTodo = (Todo) data.getSerializableExtra(TodoManagerActivity.UPDATE_TODO_EXTRA);
                        updateTodo(updatedTodo);
                    }catch (Exception e){
                        Todo todoToDelete = (Todo) data.getSerializableExtra(TodoManagerActivity.DELETE_TODO_EXTRA);
                        deleteTodo(todoToDelete);
                    }
                    finally {
                        break;
                    }
            }
        }
    }

    private int fetchUserId(){
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(LoginActivity.USER_ID, -1);
        return userId;
    }

    private void fetchUserDetails() {
        User user = database.userDAO().getUserDetails(loggedInUserId);
        userGreeting.setText(userGreeting.getText().toString()+" "+user.getFullName().split(" ")[0]);
    }

    public void filterItems(){
        List<Todo> newList = new ArrayList<>();

        // loop through the todo items to compare their contents with the query
        for(Todo todo : todoItems){
            if(todo.getTitle().toLowerCase().contains(this.searchQuery)){
                newList.add(todo);
            }
        }

        this.todoListAdapter.setFilteredItems(newList);
        this.todoListAdapter.notifyDataSetChanged();
    }

    public void fetchAllTodos(){
        // get all todo items and update the recycler view
        List<Todo> userTodos= new ArrayList<Todo>();
        List<TodoWithUser> todosWithUsers = database.userDAO().getUserTodos(loggedInUserId);

        if(todosWithUsers.size()>0){
            this.todoItems = todosWithUsers.get(0).todoItems;
            renderView(this.todoItems);
        }
    }

    public void reRenderList(){
        // refetch the todo items from the database
        this.todoItems.clear();
        fetchAllTodos();

        // re-render recycler view for the saved data
        todoListAdapter.notifyDataSetChanged();
    }

    public void updateTodo(Todo updatedTodo){
        database.todoDAO().updateTodo(updatedTodo.getId(), updatedTodo.getTitle(), updatedTodo.getDescription());
        reRenderList();
        Toast.makeText(this, "Todo Updated!", Toast.LENGTH_SHORT).show();
    }

    public void deleteTodo(Todo todoToDelete){
        database.todoDAO().deleteTodo(todoToDelete);
        reRenderList();
        Toast.makeText(this, "Todo Deleted!", Toast.LENGTH_SHORT).show();
    }

    public void toggleTodo(Todo todo){
        database.todoDAO().toggleTodo(todo.getId(), !todo.getCompleted());
        reRenderList();

        // re-filter items in case items were searched for before toggling
        this.filterItems();
    }

    public void saveTodo(Todo newTodo){
        database.todoDAO().insert(newTodo);
        reRenderList();
        Toast.makeText(this, "New todo created!", Toast.LENGTH_SHORT).show();
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
        public void onClick(Todo todo, String action) {
            // update todo
            if(action==TodoListAdapter.UPDATE_TODO_ACTION){
                Intent intent = new Intent(MainActivity.this, TodoManagerActivity.class);
                intent.putExtra(TodoManagerActivity.UPDATE_TODO_EXTRA, todo);
                startActivityForResult(intent, UPDATE_TODO_REQUEST_CODE);
            }
            else{
                // toggle todo completion status
                MainActivity.this.toggleTodo(todo);
            }

        }

        @Override
        public void onLongClick(Todo todo, CardView cardView) {
        }
    };
}