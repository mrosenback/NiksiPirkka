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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FloatingActionButton addAdviceButton;
    private List<Advice> adviceList = new ArrayList<>();
    private List<Advice> adviceData = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    MyViewModel model;
    private RecyclerView recyclerView;

    EditText authorInput;
    Spinner categorySpinner;
    Button fetch;
    Spinner timeSpinner;
    Switch fetchSwitch;

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
                                getServerAdvices();
                            }).start();

                            /*model.getAdvices().observe(MainActivity.this, advices ->
                                    adviceList = advices);*/
                        }
                    }
                }
            });

    private static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if ( connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnected());
        }
        return isConnected;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        recyclerView = findViewById(R.id.recyclerView);
        authorInput = findViewById(R.id.authorInput);
        categorySpinner = findViewById(R.id.categorySpinner2);
        fetch = findViewById(R.id.fetchAdvices);
        timeSpinner = findViewById(R.id.timeSpinner);
        fetchSwitch = findViewById(R.id.fetchSwitch);

        dao = AdviceDatabase.getInstance(MainActivity.this).adviceDao();

        getServerCategories();

        if (isConnectedToNetwork(MainActivity.this)) {
            getServerAdvices();
        } else {
            dao.getAllAdvices().observe(this, advices -> {
                adviceList = advices;
                setAdapter();
            });
        }

        if (model == null) {
            model = new ViewModelProvider(this).get(MyViewModel.class);
        }

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.time, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter2);
        timeSpinner.setOnItemSelectedListener(this);

        addAdviceButton = findViewById(R.id.addAdviceButton);

        fetchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switchState) {
                if (switchState) {
                    int timeInput = Integer.parseInt(timeSpinner.getSelectedItem().toString());

                    PeriodicWorkRequest myPeriodicWorkRequest =
                            new PeriodicWorkRequest.Builder(MyWorker.class, timeInput, TimeUnit.MINUTES).build();
                    WorkManager.getInstance(getApplicationContext()).enqueue(myPeriodicWorkRequest);

                } else {
                    WorkManager.getInstance(getApplicationContext()).cancelAllWork();
                }
            }
        });

        addAdviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String authorInputString = authorInput.getText().toString();
                int categoryPos = categorySpinner.getSelectedItemPosition();
                Intent intent = new Intent(MainActivity.this, AddAdviceActivity.class);
                intent.putExtra("AUTHOR", authorInputString);
                intent.putExtra("CATEGORYPOS", categoryPos);
                Bundle bundle=new Bundle();
                bundle.putSerializable("CATEGORYLIST", (Serializable) categoryList);
                intent.putExtras(bundle);
                activityLauncher.launch(intent);
            }
        });

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnectedToNetwork(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "No network", Toast.LENGTH_SHORT).show();
                } else {
                    WorkManager workManager = WorkManager.getInstance(MainActivity.this);
                    OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MyWorker.class).build();
                    workManager.enqueue(request);
                    getServerAdvices();
                }
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

    static Call<List<Advice>> getRetrofitAdvicesCall() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://niksipirkka.cloud-ha.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DataService service = retrofit.create(DataService.class);
        return service.getAdviceData();
    }

    private void getServerAdvices() {
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
                adviceData = response.body();

                dao.getAllAdvices().observe(MainActivity.this, advices -> {
                    adviceList = adviceData;
                    setAdapter();
                });
            }

            @Override
            public void onFailure(Call<List<Advice>> call, Throwable t) {

            }
        });
    }

    private void getServerCategories() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://niksipirkka.cloud-ha.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DataService service = retrofit.create(DataService.class);
        Call<List<Category>> call = service.getCategoryData();
        call.enqueue(new Callback<List<Category>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categoryList = response.body();

                ArrayAdapter<Category> adapter1 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, categoryList);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter1);
                categorySpinner.setOnItemSelectedListener(MainActivity.this);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                System.out.println(t);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MY CHANNEL NAME";
            String description = "MY CHANNEL DESCRIPTION";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}