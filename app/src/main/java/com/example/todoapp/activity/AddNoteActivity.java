package com.example.todoapp.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todoapp.R;

public class AddNoteActivity extends AppCompatActivity {
    private Button addButton;
    private EditText titleEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        addButton =  findViewById(R.id.add_button);
        titleEditTxt = findViewById(R.id.title_edit_text);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title",titleEditTxt.getText().toString());

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

    // when on screen back button tapped
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
