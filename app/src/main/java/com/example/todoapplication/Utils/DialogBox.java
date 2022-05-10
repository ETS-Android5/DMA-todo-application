package com.example.todoapplication.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.todoapplication.R;
import com.example.todoapplication.TodoManagerActivity;

public class DialogBox extends Dialog implements View.OnClickListener {

    public Context context;
    public Button btnYes, btnNo;

    public DialogBox(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // layout file
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box);
        btnYes = findViewById(R.id.dialog_btn_yes);
        btnNo = findViewById(R.id.dialog_btn_no);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId){
            case R.id.dialog_btn_yes:
                if(this.context instanceof TodoManagerActivity){
                    ((TodoManagerActivity) this.context).deleteTodo();
                }
                break;

            case R.id.dialog_btn_no:
                dismiss();
                break;

            default:
                break;
        }

        dismiss();
    }
}
