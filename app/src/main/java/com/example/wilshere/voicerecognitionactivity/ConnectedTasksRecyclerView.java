package com.example.wilshere.voicerecognitionactivity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ConnectedTasksRecyclerView extends RecyclerView.Adapter<ConnectedTasksRecyclerView.ConnectedTasksViewHolder> {

    private ArrayList<Task> mTaskList;

    public static class ConnectedTasksViewHolder extends  RecyclerView.ViewHolder{
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_id;
        public ImageView img;
        public LinearLayout item_task;

        public ConnectedTasksViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.task_name);
            tv_time = (TextView) itemView.findViewById(R.id.task_time);
            item_task = (LinearLayout) itemView.findViewById(R.id.active_task_item_id);
            tv_id = (TextView) itemView.findViewById(R.id.task_name);
            img = (ImageView) itemView.findViewById(R.id.img_task);
        }
    }

    public ConnectedTasksRecyclerView(ArrayList<Task> taskList){
        mTaskList = taskList;
    }

    @Override
    public ConnectedTasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_task_item,parent,false);
        ConnectedTasksViewHolder vHolder = new ConnectedTasksViewHolder(v);
        return  vHolder;
    }

    @Override
    public void onBindViewHolder(ConnectedTasksViewHolder holder, int position) {
        Task taskItem = mTaskList.get(position);
        holder.tv_name.setText(taskItem.getName());
        holder.tv_time.setText(taskItem.getStart_time() + " - " + taskItem.getEnd_time());
        holder.img.setImageResource(taskItem.getImage());
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
}
