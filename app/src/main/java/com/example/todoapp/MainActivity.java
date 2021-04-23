package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.todoapp.activity.AddNoteActivity;
import com.example.todoapp.listener.OnLoadMoreListener;
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
    private ProgressBar progressBar;
    private RadioGroup filterRadioGroup;
    private RadioButton bothRadioBtn;
    private RadioButton completedRadioBtn;
    private RadioButton yetCompletedRadioBtn;

    private RequestQueue queue;

    //to keep all the to-dos sent from the GET api
    private ArrayList<Note> todoList = new ArrayList<>();

    protected Handler handler;

    //for pagination
    //to select which range of elements to show
    private int startIndex;
    private int endIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        //views in the activity
        todoRecyclerView = findViewById(R.id.recycler_view);
        floatingButton = findViewById(R.id.floating_button);
        searchEditText = findViewById(R.id.search_edit_text);
        progressBar = findViewById(R.id.progress_bar);
        filterRadioGroup = findViewById(R.id.radio_group_filter);
        bothRadioBtn = findViewById(R.id.radio_both);
        completedRadioBtn = findViewById(R.id.radio_completed);
        yetCompletedRadioBtn = findViewById(R.id.radio_yet_completed);

        // Instantiate the RequestQueue for API.
        queue = Volley.newRequestQueue(this);

        //initializes array list with response of GET api
        getAPI();

        //configure recycler view
        recyclerAdapter = new RecyclerAdapter(todoList,this,todoRecyclerView,progressBar,todoList);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        todoRecyclerView.setAdapter(recyclerAdapter);


        //when scroll, checks if needed to load more todos/items
        todoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
             @Override
             public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                 if(recyclerAdapter.getItemCount() <= todoList.size() -1 &&
                         searchEditText.getText().length() == 0 &&
                         bothRadioBtn.isChecked()
                 ) {
                     recyclerAdapter.scrollAction();
                 }
             }
         });

        recyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                todoList.add(null);
                recyclerAdapter.notifyItemInserted(todoList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        todoList.remove(todoList.size() - 1);
                        recyclerAdapter.notifyItemRemoved(todoList.size());

                        startIndex = recyclerAdapter.getItemCount();
                        // how many items added for each load
                        endIndex = startIndex + 20;

                        for(int i = startIndex ; i < endIndex ; i++){
                            //ensures array index does not go out of bounds
                            if(i <= todoList.size()){
                                recyclerAdapter.updateList(new ArrayList<>(todoList.subList(0,i)));
                                recyclerAdapter.notifyDataSetChanged();
                            } else {
                                break;
                            }
                        }

                        recyclerAdapter.setLoaded();
                    }
                }, 800);
            }
        });

        //configure radio buttons
        filterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_both:
                        searchFilter(searchEditText.getText().toString());
                        break;
                    case R.id.radio_completed:
                        searchFilter(searchEditText.getText().toString());
                        break;
                    case R.id.radio_yet_completed:
                        searchFilter(searchEditText.getText().toString());
                        break;
                }
            }//end onCheckedChanged
        });

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                searchFilter(s.toString());
            }
        });

    }//end onCreate

    //need to handle array list changes here, after added to do from the AddNoteActivity/EditNoteActivity
    //to refresh the recycler view within this activity
    //refreshing the recycler view of this activity from another activity is not possible
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //for adding todos
        if(requestCode == 1  && resultCode  == RESULT_OK){
            String title = data.getExtras().getString("title");
            int userId = 1;
            int id = todoList.get(todoList.size()-1).getId()+1;
            boolean completed = false;
            todoList.add(new Note(userId,id,title,completed));

            searchFilter(searchEditText.getText().toString());

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

            searchFilter(searchEditText.getText().toString());

            recyclerAdapter.notifyDataSetChanged();

            todoRecyclerView.smoothScrollToPosition(id-1);
        }
    }

    //for search bar
    private void searchFilter(String searchText){
        ArrayList<Note> tempList = new ArrayList<>();

        //when search for both completed and yet completed tasks without search term
        //(only parts that allows pagination)
        if(searchText.isEmpty() && bothRadioBtn.isChecked()){
            if(endIndex == 0){
                tempList = new ArrayList<>(todoList.subList(0,19));
            } else if(endIndex <= todoList.size()){
                tempList = new ArrayList<>(todoList.subList(0,endIndex));
            } else {
                tempList = new ArrayList<>(todoList.subList(0,todoList.size()));
            }
        }
        //if search through text, or/and filter for only completed or yet completed tasks
        //(loads every elements,no pagination)
        else {
            for(Note n: todoList){
                if(!todoList.isEmpty() && n.getTitle().contains(searchText)){
                    if(completedRadioBtn.isChecked() && n.isCompleted()){
                        tempList.add(n);
                    }
                    else if(yetCompletedRadioBtn.isChecked() && !n.isCompleted()){
                        tempList.add(n);
                    }
                    else if(bothRadioBtn.isChecked()){
                        tempList.add(n);

                    }
                }
            }
        }

        recyclerAdapter.updateList(tempList);
    }

    //sends GET request
    private void getAPI() {
        //it will get all of the todos as pagination doesnt work properly with jsonplaceholder
        //https://jsonplaceholder.typicode.com/todos?_limit=20 does not work as intended
        //therefore, all todos will be kept in 'todoList' arraylist variable
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
                            //to only show first 20 elements in recycler view,
                            //when user scroll down to last element, it will show more todos from arraylist
                            recyclerAdapter.updateList(new ArrayList<>(todoList.subList(0,19)));
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