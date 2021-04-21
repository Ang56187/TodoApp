package com.example.todoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.activity.AddNoteActivity;
import com.example.todoapp.activity.EditNoteActivity;
import com.example.todoapp.object.Note;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Note> noteDataset;
    private Context context;
    private Activity activity;

    //constructor of the adapter
    public RecyclerAdapter(ArrayList<Note> dataset, Context context){
        noteDataset = dataset;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = (Activity)context;
    }

    //provide reference to type of view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView completedTextView;

        private final LinearLayout rowLayout;

        public ViewHolder(View v){
            super(v);
            titleTextView = (TextView)v.findViewById(R.id.row_text);
            completedTextView = v.findViewById(R.id.completed_text);
            rowLayout = v.findViewById(R.id.item_row_layout);
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


        //when long press on to do note
        holder.rowLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle("Select Action")
                        .setNegativeButton("Cancel", null)
                        .setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dlg, int dialogPos)
                            {
                                //edit
                                if ( dialogPos == 0 ) {
                                    int requestCode = 2;
                                    Intent intent = new Intent(context, EditNoteActivity.class);
                                    intent.putExtra("title",noteDataset.get(position).getTitle());
                                    intent.putExtra("completed",noteDataset.get(position).isCompleted());
                                    intent.putExtra("id",noteDataset.get(position).getId());
                                    intent.putExtra("userid",noteDataset.get(position).getUserId());
                                    activity.startActivityForResult(intent,requestCode);
                                }
                                //delete
                                else if(dialogPos == 1){
                                    noteDataset.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, noteDataset.size());
                                    holder.rowLayout.setVisibility(View.GONE);
                                }
                            }
                        })
                        .create();
                dialog.show();
                return false;
            }
        });


    }

    public void updateList(ArrayList<Note> list){
        noteDataset = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return noteDataset.size();
    }
}
