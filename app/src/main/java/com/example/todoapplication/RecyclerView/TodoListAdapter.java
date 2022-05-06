package com.example.todoapplication.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapplication.Models.Todo;
import com.example.todoapplication.R;

import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

   Context context;
   List<Todo> todoItems;

   public TodoListAdapter(Context context, List<Todo> todoItems) {
      this.context = context;
      this.todoItems = todoItems;
   }

   @NonNull
   @Override
   public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater layoutInflater = LayoutInflater.from(context);
      View view = layoutInflater.inflate(R.layout.todo_item, parent, false);

      return new TodoViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
      // set values for each todo item
      Todo currentTodo = todoItems.get(position);

      holder.todoTitle.setText(currentTodo.getTitle());
      holder.todoStatus.setChecked(currentTodo.getCompleted());

      // set on click listener to todo items
      holder.todoListContainer.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         }
      });

      holder.todoListContainer.setOnLongClickListener(new View.OnLongClickListener(){
         @Override
         public boolean onLongClick(View view) {
            return true;
         }
      });
   }

   @Override
   public int getItemCount() {
      return todoItems.size();
   }

   public class TodoViewHolder extends  RecyclerView.ViewHolder{

      CardView todoListContainer;
      TextView todoTitle;
      CheckBox todoStatus;

      public TodoViewHolder(@NonNull View itemView) {
         super(itemView);

         todoListContainer = itemView.findViewById(R.id.todo_item_container);
         todoTitle = itemView.findViewById(R.id.todo_title);
         todoStatus = itemView.findViewById(R.id.todo_checkbox);
      }
   }

}