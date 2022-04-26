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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FloatingActionButton addAdviceButton;
    private List<Advice> adviceList = new ArrayList<>();
    MyViewModel model;
    private RecyclerView recyclerView;

    EditText authorInput;
    Spinner categorySpinner;

    int id;
    String category;
    String author;
    String advice;

    AdviceDao dao;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 11) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            id += 1;
                            category = intent.getStringExtra("CATEGORY");
                            author = authorInput.getText().toString();
                            advice = intent.getStringExtra("ADVICE");
                            //model.addAdvices(new Advice(id, advice, author, category));

                            new Thread( () -> {
                                dao.insert(new Advice(id, advice, author, category));
                            }).start();

                            /*model.getAdvices().observe(MainActivity.this, advices ->
                                    adviceList = advices);*/
                            dao.getAllAdvices().observe(MainActivity.this, advices -> {
                                adviceList = advices;
                                setAdapter();
                            });
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        authorInput = findViewById(R.id.authorInput);
        categorySpinner = findViewById(R.id.categorySpinner2);

        if (model == null) {
            model = new ViewModelProvider(this).get(MyViewModel.class);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

        dao = AdviceDatabase.getInstance(MainActivity.this).adviceDao();

        new Thread( () -> {
            dao.deleteAllAdvices();
        }).start();

        dao.getAllAdvices().observe(MainActivity.this, advices -> {
            adviceList = advices;
            setAdapter();
        });

        addAdviceButton = findViewById(R.id.addAdviceButton);

        addAdviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String authorInputString = authorInput.getText().toString();
                String categoryInputString = categorySpinner.getSelectedItem().toString();
                Intent intent = new Intent(MainActivity.this, AddAdviceActivity.class);
                intent.putExtra("AUTHOR", authorInputString);
                intent.putExtra("CATEGORY", categoryInputString);
                activityLauncher.launch(intent);
            }
        });
    }

    private void setAdapter() {
        RecyclerAdapter adapter = new RecyclerAdapter(adviceList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}