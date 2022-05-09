package com.example.todoapplication.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todoapplication.GridCategory;
import com.example.todoapplication.R;

import java.util.ArrayList;
import java.util.List;

public class GridCategoryAdapter extends ArrayAdapter<GridCategory> {

    Context context;
    int resource;
    List<GridCategory> categories;

    public GridCategoryAdapter(@NonNull Context context, int resource, List<GridCategory> categories)  {
        super(context, resource, categories);
        this.context=context;
        this.resource=resource;
        this.categories=categories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       View view = convertView;
       ViewHolder holder = null;

       if(view==null){
           LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view = inflater.inflate(resource, parent, false);
           holder = new ViewHolder();
           holder.categoryName = view.findViewById(R.id.grid_category_name);
           holder.categoryStatus = view.findViewById(R.id.grid_category_status);
           holder.categoryIcon = view.findViewById(R.id.grid_category_icon);
           holder.progressBar = view.findViewById(R.id.progress_bar);
           view.setTag(holder);
       }else{
          holder = (ViewHolder) view.getTag();
       }

        // fill data items
        GridCategory category = getItem(position);
        holder.categoryName.setText(category.categoryName);
        if(category.totalItems == 0){
            holder.categoryStatus.setText("No Items");
        }else{
            holder.categoryStatus.setText(category.completedItems+" completed of "+category.totalItems);
        }
        holder.categoryIcon.setImageResource(getCategoryIcon(category.categoryName));

        // configure progress bar
        holder.progressBar.setMax(category.totalItems);
        holder.progressBar.setProgress(category.completedItems);

       return view;
    }

    public int getCategoryIcon(String name){
        int drawable = 0;

        switch(name){
            case "Shopping":
                drawable = R.drawable.ic_outline_shopping_cart_24;
                break;

            case "Home":
                drawable = R.drawable.ic_outline_home_24;
                break;

            case "Work":
                drawable = R.drawable.ic_outline_work_outline_24;
                break;

            case "School":
                drawable = R.drawable.ic_outline_school_24;
                break;
        }

        return drawable;
    }
}

class ViewHolder {
    public TextView categoryName, categoryStatus;
    public ImageView categoryIcon;
    public ProgressBar progressBar;
}