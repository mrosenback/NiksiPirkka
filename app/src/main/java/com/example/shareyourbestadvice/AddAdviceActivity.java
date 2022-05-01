package com.example.shareyourbestadvice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddAdviceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FloatingActionButton sendAdviceButton;
    TextInputEditText adviceInput;
    Spinner categorySpinner;
    TextView selectedAuthor;
    List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advice);

        sendAdviceButton = findViewById(R.id.sendAdviceButton);
        selectedAuthor = findViewById(R.id.selectedAuthor);
        adviceInput = findViewById(R.id.adviceInput);
        categorySpinner = findViewById(R.id.categorySpinner);

        Intent intent = new Intent();
        Bundle extras = getIntent().getExtras();
        String author = extras.getString("AUTHOR");
        int categoryPos = extras.getInt("CATEGORYPOS");
        Bundle bundle = getIntent().getExtras();
        categoryList = (List<Category>) bundle.getSerializable("CATEGORYLIST");
        selectedAuthor.setText(author);

        ArrayAdapter<String> adapter = new ArrayAdapter(AddAdviceActivity.this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);
        categorySpinner.setSelection(categoryPos);

        sendAdviceButton.setOnClickListener(view -> {
            String categoryInputString = categorySpinner.getSelectedItem().toString();
            String adviceInputString = adviceInput.getText().toString();

            if (adviceInputString.length() == 0) {
                adviceInput.setError("Enter advice");
            } else {
                intent.putExtra("CATEGORY", categoryInputString);
                intent.putExtra("ADVICE", adviceInputString);
                setResult(11, intent);
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}