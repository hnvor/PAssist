package com.example.wilshere.voicerecognitionactivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CharacterRecyclerAdapter extends RecyclerView.Adapter<com.example.wilshere.voicerecognitionactivity.CharacterRecyclerAdapter.MyViewHolder> {

        Context mContext;
        List<Atribute> mData;
        Dialog myDialog;

        public CharacterRecyclerAdapter(Context mContext, List<Atribute> mData) {
            this.mContext = mContext;
            this.mData = mData;
        }

        @Override
        public com.example.wilshere.voicerecognitionactivity.CharacterRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            v = LayoutInflater.from(mContext).inflate(R.layout.atribute_item,parent,false);
            final com.example.wilshere.voicerecognitionactivity.CharacterRecyclerAdapter.MyViewHolder vHolder = new com.example.wilshere.voicerecognitionactivity.CharacterRecyclerAdapter.MyViewHolder(v);
            vHolder.item_atribute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2 = new Intent(mContext, AtributeActivity.class);
                    intent2.putExtra("atrib",String.valueOf( mData.get(vHolder.getAdapterPosition()).getName()));
                    intent2.putExtra("rating_color",String.valueOf( mData.get(vHolder.getAdapterPosition()).color));
                    mContext.startActivity(intent2);
                }
            });
            return vHolder;
        }

        @Override
        public void onBindViewHolder(com.example.wilshere.voicerecognitionactivity.CharacterRecyclerAdapter.MyViewHolder holder, int position) {
            holder.tv_name.setText(mData.get(position).getName());
            holder.tv_time.setText(mData.get(position).getValue());
            holder.img.setImageResource(mData.get(position).getImage());
            if(mData.get(position).color == 1){
                holder.tv_time.setTextColor(Color.parseColor("#B31B1B"));
            }
            if(mData.get(position).color == 2){
                holder.tv_time.setTextColor(Color.parseColor("#ffa500"));
            }
            if(mData.get(position).color == 3){
                holder.tv_time.setTextColor(Color.parseColor("#3BB143"));
            }
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
            private LinearLayout item_atribute;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_name = (TextView) itemView.findViewById(R.id.atribute_name);
                tv_time = (TextView) itemView.findViewById(R.id.atribute_stat);
                item_atribute = (LinearLayout) itemView.findViewById(R.id.character_item_id);
                img = (ImageView) itemView.findViewById(R.id.img_atribute);

            }
        }

    }
