package com.sayantanbanerjee.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ToDoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        getSupportActionBar().hide();
    }
}
