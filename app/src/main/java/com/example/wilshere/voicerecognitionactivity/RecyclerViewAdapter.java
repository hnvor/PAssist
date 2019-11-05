package com.example.wilshere.voicerecognitionactivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<Task> mData;
    Dialog myDialog;

    public RecyclerViewAdapter(Context mContext, List<Task> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.active_task_item,parent,false);
        final MyViewHolder vHolder = new MyViewHolder(v);


        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.dialog_task);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vHolder.item_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dialog_name_tv = (TextView) myDialog.findViewById(R.id.dialog_name_id);
                TextView dialog_time_tv = (TextView) myDialog.findViewById(R.id.dialog_time_id);
                ImageView dialog_task_img = (ImageView) myDialog.findViewById(R.id.dialog_img);
                dialog_name_tv.setText(mData.get(vHolder.getAdapterPosition()).getName());
                dialog_time_tv.setText(mData.get(vHolder.getAdapterPosition()).getStart_time()+" - "+mData.get(vHolder.getAdapterPosition()).getEnd_time());
                dialog_task_img.setImageResource(mData.get(vHolder.getAdapterPosition()).getImage());
                myDialog.show();
            }
        });

        return vHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_name.setText(mData.get(position).getName());
        holder.tv_time.setText(mData.get(position).getStart_time() + " - " + mData.get(position).getEnd_time());
        holder.img.setImageResource(mData.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_id;
        private ImageView img;
        private LinearLayout item_task;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.task_name);
            tv_time = (TextView) itemView.findViewById(R.id.task_time);
            item_task = (LinearLayout) itemView.findViewById(R.id.active_task_item_id);
        //    tv_end_time = (TextView) itemView.findViewById(R.id.task_name);
            tv_id = (TextView) itemView.findViewById(R.id.task_name);
            img = (ImageView) itemView.findViewById(R.id.img_task);

        }
    }
}
