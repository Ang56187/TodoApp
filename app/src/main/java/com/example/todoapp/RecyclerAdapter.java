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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.activity.EditNoteActivity;
import com.example.todoapp.listener.OnLoadMoreListener;
import com.example.todoapp.object.Note;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Note> noteDataset;
    private ArrayList<Note> todoList;
    private Context context;
    private Activity activity;
    private RecyclerView recyclerView;
    private OnLoadMoreListener onLoadMoreListener;
    private ProgressBar progressBar;

    private int lastVisibleItem, totalVisibleItem;

    //checks how much items before the end to start loading
    private int visibleThreshold = 1;
    //indicate if currently loading
    private boolean loading;

    //constructor of the adapter
    public RecyclerAdapter(ArrayList<Note> dataset, Context context,
                           RecyclerView recyclerView,ProgressBar progressBar,ArrayList<Note> todoList){
        noteDataset = dataset;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = (Activity)context;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.todoList = todoList;
    }

    public void scrollAction() {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            totalVisibleItem = linearLayoutManager.getItemCount();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

            if (!loading && totalVisibleItem <= (lastVisibleItem + visibleThreshold)) {
                // End has been reached
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.loadMore();
                }
                loading = true;
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //to indicate when loading of pagination is completed
    public void setLoaded() {
        loading = false;
        progressBar.setVisibility(View.GONE);
    }

    //for only showing filtered todos
    public void updateList(ArrayList<Note> list){
        noteDataset = list;
        notifyDataSetChanged();
    }

    //provide reference to type of view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView completedTextView;

        private final LinearLayout rowLayout;

        public ViewHolder(View v){
            super(v);
            titleTextView = v.findViewById(R.id.row_text);
            completedTextView = v.findViewById(R.id.completed_text);
            rowLayout = v.findViewById(R.id.item_row_layout);
        }
    }

    //create new views in recycler view
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        RecyclerAdapter.ViewHolder vh;

        View v = mInflater.inflate(R.layout.item_row,parent,false);
        vh = new RecyclerAdapter.ViewHolder(v);

        return vh;
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

        //when long press on to-do note
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
                                    for(Note n: noteDataset){
                                        if(n.getId() == noteDataset.get(position).getId()){
                                            noteDataset.remove(n);
                                            todoList.remove(n);
                                            break;
                                        }
                                    }
                                    notifyDataSetChanged();
//                                    notifyItemRemoved(position);
//                                    notifyItemRangeChanged(position, noteDataset.size());
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

    @Override
    public int getItemCount(){
        return noteDataset.size();
    }

}
