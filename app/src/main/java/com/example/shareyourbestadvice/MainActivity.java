package com.example.shareyourbestadvice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addAdviceButton;
    private ArrayList<Advice> adviceList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        adviceList = new ArrayList<>();
        String category = getIntent().getStringExtra("category");
        String author = getIntent().getStringExtra("author");
        String advice = getIntent().getStringExtra("advice");

        addAdviceButton = findViewById(R.id.addAdviceButton);

        addAdviceButton.setOnClickListener(view -> openAddAdviceActivity());
        if (advice != null) {
            adviceList.add(new Advice(advice, author, category));
        }
        setAdapter();

    }

    public void openAddAdviceActivity() {
        Intent intent = new Intent(this, AddAdviceActivity.class);
        startActivity(intent);
    }

    private void setAdapter() {
        RecyclerAdapter adapter = new RecyclerAdapter(adviceList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}