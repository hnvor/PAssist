package com.example.wilshere.voicerecognitionactivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wilshere.voicerecognitionactivity.Utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterFragment extends Fragment {
    View v;
    private RecyclerView myrecyclerview;
    private List<Atribute> lstAtribute;
    DBHelper dbHelper;
    private LinearLayout chBackground;
    private TextView rating,tasks_completed;
    SharedPreferences sp;
    int id,color;
    ImageView changeName,changePhoto,profilePhoto;
    Boolean color1,color2,color3,color4,color5,color6;
    private static final int SELECT_PHOTO = 7777;
    TextView user_name;

    public CharacterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.character_fragment,container,false);
        myrecyclerview = (RecyclerView) v.findViewById(R.id.character_recyclerview);
        CharacterRecyclerAdapter recyclerAdapter = new CharacterRecyclerAdapter(getContext(),lstAtribute);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setAdapter(recyclerAdapter);
        user_name = (TextView) v.findViewById(R.id.user_name) ;
        changeName = (ImageView) v.findViewById(R.id.changeName);
        changePhoto = (ImageView) v.findViewById(R.id.changePhoto);
        profilePhoto = (ImageView) v.findViewById(R.id.profile_photo);
        changeName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editName();
            }
        });
        changePhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,SELECT_PHOTO);
            }
        });
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
        lstAtribute = new ArrayList<>();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbGet();


    }

    public void dbGet(){
        lstAtribute.clear();
        long count=0;
        int cou = 0;
        List<String> names = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        List<String> images = new ArrayList<String>();

        String selection = "date = ?";
        String selectionArgs[] = new String[] { "19.10.2019" };
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            count = DatabaseUtils.queryNumEntries(db, DBHelper.TABLE_ATRIBUTES);
            cursor            = db.query(DBHelper.TABLE_ATRIBUTES, null, null, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_NAME));
                String value = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_VALUE));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_IMAGE));
                names.add(name);
                values.add(value);
                images.add(image);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        double[] array = new double[(int) count];
        for(int i=0;i<count;i++){
            array[i]=Double.parseDouble(values.get(i));
        }
        System.out.println(Arrays.toString(array));
        boolean needIteration = true;
        while (needIteration) {
            needIteration = false;
            for (int i = 1; i < array.length; i++) {
                if (array[i] < array[i - 1]) {
                    swap(array, i, i-1);
                    needIteration = true;
                }
            }
        }

        for(int i = 0;i<count;i++){
            Resources res = getResources();
            int resID = res.getIdentifier(images.get(i) , "drawable", getActivity().getPackageName());
            if(Double.parseDouble(values.get(i))<array[(int) count/3])
                cou=1;
            if(Double.parseDouble(values.get(i))>array[array.length -1 - (int) count/3])
                cou=3;
            if(Double.parseDouble(values.get(i))<=array[array.length -1 - (int) count/3]&&Double.parseDouble(values.get(i))>=array[(int) count/3])
                cou=2;
            lstAtribute.add(new Atribute(names.get(i) + " ", String.format("%.2f", Double.parseDouble(values.get(i))),
                    resID,cou));
        }
        System.out.println(Arrays.toString(array));
        System.out.println(array.length);
    }

    public void editName(){
                final InputMethodManager imm =
                        (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.prompts, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        imm.restartInput(userInput);
                                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("your_name", getContext().MODE_PRIVATE).edit();
                                        editor.putString("name", userInput.getText().toString());
                                        editor.apply();
                                        user_name.setText(userInput.getText());
                                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("your_name", getContext().MODE_PRIVATE).edit();
                                        editor.putString("name", user_name.getText().toString());
                                        editor.apply();
                                        dialog.cancel();
                                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void swap(double[] array, int ind1, int ind2) {
        double tmp = array[ind1];
        array[ind1] = array[ind2];
        array[ind2] = tmp;
    }

    public void setChBackground(String color){
        chBackground = (LinearLayout) v.findViewById(R.id.char_background);
        rating = (TextView) v.findViewById(R.id.rating);
        tasks_completed = (TextView) v.findViewById(R.id.tasks_completed);
        chBackground.setBackgroundColor(Color.parseColor(color));
        rating.setTextColor(Color.parseColor(color));
        tasks_completed.setTextColor(Color.parseColor(color));
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences("your_name", getContext().MODE_PRIVATE);
        String name = prefs.getString("name", "your name");
        user_name.setText(name);
        color1 = sp.getBoolean("color1", false);
        color2 = sp.getBoolean("color2", false);
        color3 = sp.getBoolean("color3", false);
        color4 = sp.getBoolean("color4", false);
        color5 = sp.getBoolean("color5", false);
        color6 = sp.getBoolean("color6", false);
        if(color1){
            setChBackground("#6A105F");
        }
        else if(color2){
            setChBackground("#1d9dcc");
        }
        else if(color3){
            setChBackground("#7C7A80");
        }
        else if(color4){
            setChBackground("#F5CA7A");
        }
        else if(color5){
            setChBackground("#187A27");
        }
        else if(color6){
            setChBackground("#523C07");
        }
        else {
            setChBackground("#6A105F");
        }

        byte[] data = dbHelper.getBitmapByName("profile_photo");
        if (data != null){
            Bitmap bitmap = Utils.getImage(data);
            //  profilePhoto.setImageBitmap(bitmap);
            Glide
                    .with(this)
                    .load(bitmap)
                    .into(profilePhoto);
        }

        rating.setText(String.format("%.2f", Double.parseDouble(String.valueOf(getRating()))));
    }

    public double getRating(){
        double rating = 0;
        double countAtributes = 0;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor            = db.query(DBHelper.TABLE_ATRIBUTES, null, null, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                String value = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_VALUE));
                rating = rating + Double.parseDouble(value);
                countAtributes++;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        rating = rating/countAtributes;
        return rating;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK && data!=null){
            Uri pickedImage = data.getData();
            Glide
                    .with(this)
                    .load(pickedImage)
                            .into(profilePhoto);

            profilePhoto.setImageURI(pickedImage);
            Bitmap bitmap = ((BitmapDrawable)profilePhoto.getDrawable()).getBitmap();

            dbHelper.addBitmap("profile_photo", Utils.getBytes(bitmap));

        }
    }
}
