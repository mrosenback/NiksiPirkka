package com.example.shareyourbestadvice;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FloatingActionButton addAdviceButton;
    private List<Advice> adviceList = new ArrayList<>();
    private List<Advice> data = new ArrayList<>();
    MyViewModel model;
    private RecyclerView recyclerView;

    EditText authorInput;
    Spinner categorySpinner;
    Button fetch;

    int id;
    String category;
    String author;
    String advice;

    AdviceDao dao;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
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
                                addToWebList(id, advice, author, category);
                                getWebList();
                            }).start();

                            /*model.getAdvices().observe(MainActivity.this, advices ->
                                    adviceList = advices);*/
                        }
                    }
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        authorInput = findViewById(R.id.authorInput);
        categorySpinner = findViewById(R.id.categorySpinner2);
        fetch = findViewById(R.id.fetchAdvices);

        dao = AdviceDatabase.getInstance(MainActivity.this).adviceDao();

        new Thread( () -> {
            dao.deleteAllAdvices();
        }).start();

        getWebList();

        if (model == null) {
            model = new ViewModelProvider(this).get(MyViewModel.class);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

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

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebList();
                Toast toast = Toast.makeText(MainActivity.this, "Advices fetched from server", Toast.LENGTH_SHORT);
                toast.show();
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

    private void getWebList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://niksipirkka.cloud-ha.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DataService service = retrofit.create(DataService.class);
        Call<List<Advice>> call = service.getAdviceData();
        call.enqueue(new Callback<List<Advice>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Advice>> call, Response<List<Advice>> response) {
                data = response.body();

                dao.getAllAdvices().observe(MainActivity.this, advices -> {
                    adviceList = data;
                    setAdapter();
                });
            }

            @Override
            public void onFailure(Call<List<Advice>> call, Throwable t) {

            }
        });
    }

    private void addToWebList(int id, String advice, String author, String category) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://niksipirkka.cloud-ha.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DataService service = retrofit.create(DataService.class);
        Call<List<Advice>> call = service.addAdviceData(id, advice, author, category);
        call.enqueue(new Callback<List<Advice>>() {
            @Override
            public void onResponse(Call<List<Advice>> call, Response<List<Advice>> response) {

            }

            @Override
            public void onFailure(Call<List<Advice>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}