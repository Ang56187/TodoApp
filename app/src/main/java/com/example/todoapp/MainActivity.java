package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private RequestQueue queue;

    private ArrayList<Note> todoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //views in the activity
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        floatingButton = findViewById(R.id.floating_button);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        getAPI();

        //configure recycler view
        recyclerAdapter = new RecyclerAdapter(todoList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(recyclerAdapter);

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
                startActivity(intent);
            }
        });

    }



    private void getAPI() {

        String url ="https://jsonplaceholder.typicode.com/users/1/todos";
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

                              todoList.add(new Note(userId,id,title,completed));
                               Log.d("ddd","Size:"+todoList.size());
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