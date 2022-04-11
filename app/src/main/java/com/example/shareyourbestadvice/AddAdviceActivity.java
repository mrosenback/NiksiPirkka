package com.example.shareyourbestadvice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AddAdviceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FloatingActionButton sendAdviceButton;
    EditText authorInput;
    TextInputEditText adviceInput;
    Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advice);

        sendAdviceButton = findViewById(R.id.sendAdviceButton);
        authorInput = findViewById(R.id.authorInput);
        adviceInput = findViewById(R.id.adviceInput);
        categorySpinner = findViewById(R.id.categorySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

        sendAdviceButton.setOnClickListener(view -> {
            String categoryInputString = categorySpinner.getSelectedItem().toString();
            String authorInputString = authorInput.getText().toString();
            String adviceInputString = adviceInput.getText().toString();

            if (authorInputString.length() == 0) {
                authorInput.setError("Enter author");
            }
            if (adviceInputString.length() == 0) {
                adviceInput.setError("Enter advice");
            } else {
                openMainActivity(categoryInputString, authorInputString, adviceInputString);
            }
        });
    }

    public void openMainActivity(String categoryInput, String authorInput, String adviceInput) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("category", categoryInput);
        intent.putExtra("author", authorInput);
        intent.putExtra("advice", adviceInput);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}