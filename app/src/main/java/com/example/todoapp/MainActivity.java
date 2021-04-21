package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.todoapp.activity.AddNoteActivity;
import com.example.todoapp.object.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerAdapter recyclerAdapter;

    private ImageButton floatingButton;
    private RecyclerView todoRecyclerView;
    private EditText searchEditText;


    private RequestQueue queue;

    private ArrayList<Note> todoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //views in the activity
        todoRecyclerView = findViewById(R.id.recycler_view);
        floatingButton = findViewById(R.id.floating_button);
        searchEditText = findViewById(R.id.search_edit_text);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        //initializes array list with response of GET api
        getAPI();

        //configure recycler view
        recyclerAdapter = new RecyclerAdapter(todoList,this);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        todoRecyclerView.setAdapter(recyclerAdapter);

        //configure add button
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int requestCode = 1;
                Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
                startActivityForResult(intent,requestCode);
            }
        });

        //configure search bar
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchFilter(s.toString());
            }
        });

    }

    //need to handle array list changes here, after added to do from the AddNoteActivity/EditNoteActivity
    //to refresh the recycler view within this activity
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //for adding todos
        if(requestCode == 1  && resultCode  == RESULT_OK){
            String title = data.getExtras().getString("title");
            int userId = 1;
            int id = todoList.size()+1;
            boolean completed = false;
            todoList.add(new Note(userId,id,title,completed));

            recyclerAdapter.notifyDataSetChanged();

        }
        //for editing todos
        else if(requestCode == 2  && resultCode  == RESULT_OK){
            String title = data.getExtras().getString("title");
            int id = data.getExtras().getInt("id");
            boolean completed = data.getExtras().getBoolean("completed");

            for(Note n:todoList){
                if(n.getId() == id){
                    n.setTitle(title);
                    n.setCompleted(completed);
                }
            }
            recyclerAdapter.notifyDataSetChanged();

            todoRecyclerView.smoothScrollToPosition(id-1);

        }
    }

    private void searchFilter(String searchText){
        ArrayList<Note> tempList = new ArrayList<>();
        for(Note n: todoList){
            if(n.getTitle().contains(searchText)){
                tempList.add(n);
            }
        }
        recyclerAdapter.updateList(tempList);
    }

    //sends GET request
    private void getAPI() {
        String url ="https://jsonplaceholder.typicode.com/todos";
        StringRequest request = new StringRequest (Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try { ;
                           JSONArray jsonArr= new JSONArray(response);
                           for(int i=0;i<jsonArr.length();i++) {
                              JSONObject jsonObj=jsonArr.getJSONObject(i);

                              int userId = jsonObj.getInt("userId");
                              int id = jsonObj.getInt("id");
                              String title = jsonObj.getString("title");
                              boolean completed = jsonObj.getBoolean("completed");

                              //saved as Note object
                              todoList.add(new Note(userId,id,title,completed));
                           }
                            recyclerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                           e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                Log.d("Test","Error=>"+error.toString());
            }
        });
        queue.add(request);
    }

}