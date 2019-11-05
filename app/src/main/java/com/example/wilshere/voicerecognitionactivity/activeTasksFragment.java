package com.example.wilshere.voicerecognitionactivity;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class activeTasksFragment extends Fragment {
    View v;
    private RecyclerView myrecyclerview;
    private List<Task> lstTask;
    DBHelper dbHelper;

    public activeTasksFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.active_tasks_fragment,container,false);
        myrecyclerview = (RecyclerView) v.findViewById(R.id.activeTasks_recyclerview);
        RecyclerViewAdapter recyclerAdapter = new RecyclerViewAdapter(getContext(),lstTask);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(getContext());
        try {
            dbHelper.createDataBase();}
        catch (IOException ioe) {
            throw new Error("Не удалось создать базу данных");}
        try {
            dbHelper.openDataBase();}
        catch (SQLException sqle) {
            throw sqle;}

        lstTask = new ArrayList<>();
        dbGet();
    }

    public void dbGet(){
        lstTask.clear();

        String selection = "date = ?";
        String selectionArgs[] = new String[] { "19.10.2019" };
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor            = db.query(DBHelper.TABLE_TASKS, null, null, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TASK_NAME));
                String start_time = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TASK_START_TIME));
                String end_time = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TASK_END_TIME));
                Boolean status = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TASK_STATUS)));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.TASK_ID));
                lstTask.add(new Task(name + " ", start_time , end_time,
                        R.drawable.ic_plus,id));
                //   System.out.println(name);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }
}
