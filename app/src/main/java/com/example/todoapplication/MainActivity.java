package com.example.todoapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapplication.Database.DB;
import com.example.todoapplication.Models.Todo;
import com.example.todoapplication.Models.TodoWithUser;
import com.example.todoapplication.Models.User;
import com.example.todoapplication.RecyclerView.GridCategoryAdapter;
import com.example.todoapplication.RecyclerView.TodoClickListener;
import com.example.todoapplication.RecyclerView.TodoListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    RecyclerView todoRecyclerView;
    TodoListAdapter todoListAdapter;
    SearchView searchView;
    TextView userGreeting;
    ImageButton logoutBtn;
    GridView categoryGrid;
    ImageView toggleCategories;
    ConstraintLayout categorySectionContainer;
    ConstraintLayout noDataContainer;

    List<Todo> todoItems = new ArrayList<Todo>();
    DB database;
    FloatingActionButton addTodoBtn;

    GridCategoryAdapter gridAdapter;

    int loggedInUserId;
    String searchQuery = "";

    public boolean categoryGridVisible = false;
    public boolean selectMode = false;
    public List<Todo> selectedTodos = new ArrayList<>();

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
        categoryGrid = findViewById(R.id.todo_category_grid);
        toggleCategories = findViewById(R.id.toggle_category_icon);
        categorySectionContainer = findViewById(R.id.category_section_container);
        noDataContainer = findViewById(R.id.no_data_container);

        // get data for logged in user
        loggedInUserId = fetchUserId();

        // pass this as context when initializing database object
        database = DB.getInstance(this);
        fetchUserDetails();
        fetchAllTodos();

        // fill grid view with categories
        populateCategoryGrid();

        // onclick listener for add buttons and views
        addTodoBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        toggleCategories.setOnClickListener(this);

        // attach the item swipe helper to recycler view
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallBack);
        itemTouchHelper.attachToRecyclerView(todoRecyclerView);

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

            case R.id.toggle_category_icon:
                categoryGridVisible = !categoryGridVisible;
                this.toggleCategories();
                break;

            default:
                return;
        }
    }

    public void toggleCategories(){

        int visibility;
        int iconSource;

        if(categoryGridVisible){
            visibility = View.VISIBLE;
            iconSource = R.drawable.ic_baseline_horizontal_rule_24;
        }else{
            visibility = View.GONE;
            iconSource = R.drawable.ic_baseline_add_24;
        }

        categoryGrid.setVisibility(visibility);
        toggleCategories.setImageResource(iconSource);
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

    // swipe listener to delete items
    ItemTouchHelper.SimpleCallback swipeCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        // swipe to delete
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Todo todo = todoItems.get(position);

            // check if todo was collaborated, if it was do not let user delete the todo
            if(todo.getCollaboratorId()==fetchUserId()){
                todoListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Cannot delete this todo.", Toast.LENGTH_SHORT).show();
            }else{
                deleteTodo(todoItems.get(position));
            }

        }

        // show "delete" background on swipe
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.red_primary));

            c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                    (float) itemView.getRight(), (float) itemView.getBottom(), paint);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void populateCategoryGrid() {
        List<GridCategory> gridCategories = new ArrayList<GridCategory>();
        HashMap<String, HashMap<String, Integer>> allCategories= new HashMap<>();

        for (Todo todo: todoItems){
            boolean completed = todo.getCompleted();

            // add category to key if it doesn't exit
            if(!allCategories.containsKey(todo.getCategory())){
                allCategories.put(todo.getCategory(), new HashMap<String, Integer>(){{
                    put("totalItems", 0);
                    put("completedItems", 0);
                }});
            }

            // add values to the hashmap key
            allCategories.get(todo.getCategory()).put("totalItems", allCategories.get(todo.getCategory()).get("totalItems")+1);
            if(completed){
                allCategories.get(todo.getCategory()).put("completedItems", allCategories.get(todo.getCategory()).get("completedItems")+1);
            }
        }

        // fill up the grid categories
        for(String category: TodoManagerActivity.TODO_CATEGORIES){
            if(allCategories.containsKey(category)){
                HashMap<String, Integer> categoryStats = allCategories.get(category);
                int totalItems = categoryStats.get("totalItems");
                int completedItems = categoryStats.get("completedItems");

                gridCategories.add(new GridCategory(category, totalItems, completedItems));
            }
        }

        gridAdapter = new GridCategoryAdapter(this, R.layout.category_grid_items, gridCategories);
        categoryGrid.setAdapter(gridAdapter);
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
            if(todo.getTitle().toLowerCase().contains(this.searchQuery.toLowerCase())){
                newList.add(todo);
            }
        }

        this.todoListAdapter.setFilteredItems(newList);
        this.todoListAdapter.notifyDataSetChanged();
    }

    public void fetchAllTodos(){
        // get all todo items and update the recycler view
        List<TodoWithUser> todosWithUsers = database.userDAO().getUserTodos(loggedInUserId);
        List<Todo> collaboratedTodos = database.todoDAO().getCollaboratedTodo(loggedInUserId);

        if(todosWithUsers.size()>0 || collaboratedTodos.size()>0){
            List<Todo> allTodos = new ArrayList<Todo>();

            // add collaborated and personal todos to the list
            allTodos.addAll(todosWithUsers.get(0).todoItems);
            allTodos.addAll(collaboratedTodos);

            this.todoItems = allTodos;
            renderView(this.todoItems);
        }

        this.populateCategoryGrid();

        // check if any todos exist, if not show the no data image and hide the category section
        if(this.todoItems.size()==0){
            categorySectionContainer.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);

            // display the no data image
            noDataContainer.setVisibility(View.VISIBLE);
        }
        else{
            categorySectionContainer.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            noDataContainer.setVisibility(View.GONE);
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
        database.todoDAO().updateTodo(updatedTodo.getId(), updatedTodo.getTitle(), updatedTodo.getDescription(), updatedTodo.getCategory(), updatedTodo.getCollaboratorId());
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
            // check if select mode is on, if it is do not go to the update todo intent
            if(MainActivity.this.selectMode){

                if(!MainActivity.this.selectedTodos.contains(todo)){
                    MainActivity.this.selectedTodos.add(todo);
                }else{
                    MainActivity.this.selectedTodos.remove(todo);
                }
                return;
            }

            // redirect to update todo
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
            MainActivity.this.selectMode = true;
        }
    };
}