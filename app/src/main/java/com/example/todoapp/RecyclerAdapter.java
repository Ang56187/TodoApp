package com.example.todoapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.object.Note;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Note> noteDataset;

    //constructor of the adapter
    public RecyclerAdapter(ArrayList<Note> dataset, Context context){
        noteDataset = dataset;
        this.mInflater = LayoutInflater.from(context);
    }

    //provide reference to type of view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView completedTextView;

        public ViewHolder(View v){
            super(v);
            titleTextView = (TextView)v.findViewById(R.id.row_text);
            completedTextView = v.findViewById(R.id.completed_text);
        }
    }

    //create new views in recycler view
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v = mInflater.inflate(R.layout.item_row,parent,false);
        return new RecyclerAdapter.ViewHolder(v);
    }

    //to adjust values of content of elements within recyler view
    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.titleTextView.setText(noteDataset.get(position).getTitle());

        if(noteDataset.get(position).isCompleted()){
            holder.completedTextView.setTextColor(Color.rgb(0,106,0));
            holder.completedTextView.setText("Completed");
        } else{
            holder.completedTextView.setTextColor(Color.RED);
            holder.completedTextView.setText("Yet completed");
        }
    }

    @Override
    public int getItemCount(){
        return noteDataset.size();
    }
}
