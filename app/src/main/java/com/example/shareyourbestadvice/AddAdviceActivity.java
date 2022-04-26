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

import java.util.Objects;

public class AddAdviceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FloatingActionButton sendAdviceButton;
    TextInputEditText adviceInput;
    Spinner categorySpinner;
    TextView selectedAuthor;

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
        String category = extras.getString("CATEGORY");
        selectedAuthor.setText(author);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);
        categorySpinner.setSelection(adapter.getPosition(category));

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