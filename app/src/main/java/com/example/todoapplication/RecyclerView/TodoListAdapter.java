package com.example.todoapplication.RecyclerView;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapplication.Database.DB;
import com.example.todoapplication.Models.Todo;
import com.example.todoapplication.R;

import java.util.List;

// get data for logged in user
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

   public static final String UPDATE_TODO_ACTION = "update_todo_action";
   public static final String TOGGLE_TODO_ACTION = "toggle_todo_action";

   Context context;
   List<Todo> todoItems;

   TodoClickListener todoClickListener;

   public void setFilteredItems(List<Todo> todoItems){
      this.todoItems = todoItems;
   }

   public TodoListAdapter(Context context, List<Todo> todoItems, TodoClickListener listener) {
      this.context = context;
      this.todoItems = todoItems;
      this.todoClickListener = listener;
   }

   @NonNull
   @Override
   public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater layoutInflater = LayoutInflater.from(context);
      View view = layoutInflater.inflate(R.layout.todo_item, parent, false);

      return new TodoViewHolder(view);
   }

   private String trimDescription(String description){
      final int maxLength = 40;
      if(description.length()<maxLength) return description;

      return description.substring(0, maxLength-3) + "...";
   }

   @Override
   public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
      // set values for each todo item
      Todo currentTodo = todoItems.get(position);

      holder.todoTitle.setText(currentTodo.getTitle());
      holder.todoCategory.setText(currentTodo.getCategory());
      holder.todoStatus.setChecked(currentTodo.getCompleted());

      // strike through
      if(currentTodo.getCompleted()){
         holder.todoTitle.setPaintFlags(holder.todoTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      }

      // check if description is empty of too long for the preview
      if(!currentTodo.getDescription().isEmpty()){
         holder.tododescription.setVisibility(View.VISIBLE);
         holder.tododescription.setText(trimDescription(currentTodo.getDescription()));
      }

      // set on click listener to todo items
      holder.todoListContainer.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            todoClickListener.onClick(todoItems.get(holder.getAdapterPosition()), UPDATE_TODO_ACTION);
         }
      });

      holder.todoListContainer.setOnLongClickListener(new View.OnLongClickListener(){
         @Override
         public boolean onLongClick(View view) {
            todoClickListener.onLongClick(todoItems.get(holder.getAdapterPosition()), holder.todoListContainer);
            return true;
         }
      });

      holder.todoStatus.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            todoClickListener.onClick(todoItems.get(holder.getAdapterPosition()), TOGGLE_TODO_ACTION);
         }
      });
   }

   @Override
   public int getItemCount() {
      return todoItems.size();
   }

   public class TodoViewHolder extends  RecyclerView.ViewHolder{

      CardView todoListContainer;
      TextView todoTitle, tododescription, todoCategory;
      CheckBox todoStatus;

      public TodoViewHolder(@NonNull View itemView) {
         super(itemView);

         todoListContainer = itemView.findViewById(R.id.todo_item_container);
         todoTitle = itemView.findViewById(R.id.todo_title);
         todoStatus = itemView.findViewById(R.id.todo_checkbox);
         tododescription = itemView.findViewById(R.id.todo_description);
         todoCategory = itemView.findViewById(R.id.todo_category);
      }
   }

}