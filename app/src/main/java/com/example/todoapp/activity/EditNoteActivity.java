package com.example.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

public class EditNoteActivity extends AppCompatActivity {
    private Switch completionSwitch;
    private TextView completionTextView;
    private EditText titleEditText;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //set layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent = getIntent();

        titleEditText = findViewById(R.id.edit_title_edit_text);
        completionTextView = findViewById(R.id.completion_text);
        completionSwitch = findViewById(R.id.switch_completion);
        editButton = findViewById(R.id.edit_button);

        titleEditText.setText(intent.getStringExtra("title"));
        if(intent.getBooleanExtra("completed",false)) {
            completionSwitch.setChecked(true);
        } else {
            completionSwitch.setChecked(false);
        }

        int id = intent.getIntExtra("id",0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title",titleEditText.getText().toString());
                intent.putExtra("completed",completionSwitch.isChecked());
                intent.putExtra("id",id);

                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    //back button for action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
