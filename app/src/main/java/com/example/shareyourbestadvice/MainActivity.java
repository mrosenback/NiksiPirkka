package com.example.shareyourbestadvice;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addAdviceButton;
    private ArrayList<Advice> adviceList = new ArrayList<>();
    MyViewModel model;
    private RecyclerView recyclerView;

    String category;
    String author;
    String advice;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 11) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            category = intent.getStringExtra("category");
                            author = intent.getStringExtra("author");
                            advice = intent.getStringExtra("advice");
                            model.addAdvices(new Advice(category, author, advice));
                            model.getAdvices().observe(MainActivity.this, advices ->
                                    adviceList = advices);
                            setAdapter();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);

        if (model == null) {
            model = new ViewModelProvider(this).get(MyViewModel.class);
        }

        setAdapter();

        addAdviceButton = findViewById(R.id.addAdviceButton);

        addAdviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAdviceActivity.class);
                activityLauncher.launch(intent);
            }
        });
    }

    private void setAdapter() {
        RecyclerAdapter adapter = new RecyclerAdapter(adviceList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}