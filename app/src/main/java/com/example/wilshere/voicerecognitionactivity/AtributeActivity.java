package com.example.wilshere.voicerecognitionactivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wilshere.voicerecognitionactivity.Utils.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AtributeActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

        public LinearLayout navHeader;
        SharedPreferences sp;
        private DrawerLayout drawerLayout;
        int id,color;
        final Context context2 = this;
        Boolean color1,color2,color3,color4,color5,color6;
        DBHelper dbHelper;
        String atrib;
        TextView atributeName;
        ImageView atributeIcon;
        LinearLayout atr_info_layout;
        TextView worseAtribute,atributeRating,seekBarValue,justTextView,expectedValue;
        SeekBar sbar;
        View horizontalLine;
        private RecyclerView mRecyclerView;
        private  RecyclerView.Adapter mAdapter;
        private  RecyclerView.LayoutManager mLayoutManager;
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (!isTaskRoot()) {
                final Intent intent = getIntent();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                    finish();
                    return;
                }}
            setContentView(R.layout.atribute_activity);
            dbHelper = new DBHelper(context2);
            try {
                dbHelper.createDataBase();}
            catch (IOException ioe) {
                throw new Error("Не удалось создать базу данных");}
            try {
                dbHelper.openDataBase();}
            catch (SQLException sqle) {
                throw sqle;}
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            atrib = getIntent().getStringExtra("atrib");
            String ratingColor = getIntent().getStringExtra("rating_color");

            atributeName = (TextView) findViewById(R.id.atr_name);
            atributeRatingz = (TextView) findViewById(R.id.atr_rati);
            worseAtribute = (TextView) findViewById(R.id.worse_atr);
            seekBarValue = (TextView) findViewById(R.id.seekBarValue);
            justTextView = (TextView) findViewById(R.id.just_text_view);
            expectedValue = (TextView) findViewById(R.id.expected_value);
            atributeIcon = (ImageView) findViewById(R.id.atr_icon);
            atr_info_layout = (LinearLayout) findViewById(R.id.atr_info_layout);
            sbar = (SeekBar) findViewById(R.id.seekBar);
            horizontalLine = (View) findViewById(R.id.horizontal_line);
            addConnectedTasks();

            sbar.setOnSeekBarChangeListener(this);

            if(ratingColor.equals("1"))
                atributeRating.setTextColor(Color.parseColor("#B31B1B"));
            if(ratingColor.equals("2"))
                atributeRating.setTextColor(Color.parseColor("#ffa500"));
            if(ratingColor.equals("3"))
                atributeRating.setTextColor(Color.parseColor("#3BB143"));


            String selection = "atribute=?";
            atrib = atrib.trim();
            String[] selectionArgs = new String[]{atrib};
            String image="not found";
            String rating="";
            String multiplier="";
            String next_worsening="";
            String worsening_frequency="";
            Cursor cursor = null;
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
               // cursor =  db.rawQuery("select * from " + DBHelper.TABLE_ATRIBUTES + " where " + DBHelper.ATRIBUTE_NAME + "='" + atrib + "'" , null);
                cursor            = db.query(DBHelper.TABLE_ATRIBUTES, null, selection, selectionArgs, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    image = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_IMAGE));
                    rating = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_VALUE));
                    multiplier = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_MULTIPLIER));
                    next_worsening = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_NEXT_WORSENING));
                    worsening_frequency = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_WORSENING_FREQUENCY));
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }

            sbar.setProgress(Integer.parseInt(multiplier));
            atributeName.setText(atrib);
            if(worsening_frequency.equals("3"))
                worseAtribute.setText("Каждый день");
            if(worsening_frequency.equals("2"))
                worseAtribute.setText("Каждые 7 дней");
            if(worsening_frequency.equals("1"))
                worseAtribute.setText("Каждые 30 дней");
            if(worsening_frequency.equals("0"))
                worseAtribute.setText("Никогда");
            atributeRating.setText(String.format("%.2f", Double.parseDouble(rating)));
            Resources res = getResources();
            int resID = res.getIdentifier(image , "drawable", getPackageName());
            Drawable drawable = res.getDrawable(resID );
            atributeIcon.setImageDrawable(drawable );


            if(Double.parseDouble(atributeRating.getText().toString()) > Double.parseDouble(expectedValue.getText().toString()))
                expectedValue.setTextColor(Color.parseColor("#B31B1B"));
            if(Double.parseDouble(atributeRating.getText().toString()) < Double.parseDouble(expectedValue.getText().toString()))
                expectedValue.setTextColor(Color.parseColor("#3BB143"));
            if(Double.parseDouble(atributeRating.getText().toString()) == Double.parseDouble(expectedValue.getText().toString()))
                expectedValue.setTextColor(Color.parseColor("#ffa500"));


            drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
            initNavigationDrawer();

            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            View headerView = navigationView.getHeaderView(0);
            ImageView photo_profile = (ImageView) headerView.findViewById(R.id.photo_profile);
            byte[] data = dbHelper.getBitmapByName("profile_photo");
            if (data != null){
                Bitmap bitmap = Utils.getImage(data);
                Glide
                        .with(context2)
                        .load(bitmap)
                        .into(photo_profile);
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showDial(View v){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AtributeActivity.this);


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AtributeActivity.this, android.R.layout.select_dialog_item);
        arrayAdapter.add("Каждый день");
        arrayAdapter.add("Каждые 7 дней");
        arrayAdapter.add("Каждые 30 дней");
        arrayAdapter.add("Никогда");

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                worseAtribute.setText(strName);
                if (strName.equals("Никогда")){
                    seekBarValue.setEnabled(false);
                    sbar.setEnabled(false);
                    justTextView.setEnabled(false);
                    expectedValue.setEnabled(false);
                    expectedValue.setTextColor(603979776);
                    ContentValues cv = new ContentValues();
                        cv.put("worsening_frequency", 0);
                        cv.put("next_worsening", "12.12.2112");
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    db.update(DBHelper.TABLE_ATRIBUTES, cv, "atribute= \""+atrib.trim()+"\"", null);
                    dbHelper.close();
                }
                else{
                    seekBarValue.setEnabled(true);
                    sbar.setEnabled(true);
                    justTextView.setEnabled(true);
                    expectedValue.setEnabled(true);
                    if(Double.parseDouble(atributeRating.getText().toString()) > Double.parseDouble(expectedValue.getText().toString()))
                        expectedValue.setTextColor(Color.parseColor("#B31B1B"));
                    if(Double.parseDouble(atributeRating.getText().toString()) < Double.parseDouble(expectedValue.getText().toString()))
                        expectedValue.setTextColor(Color.parseColor("#3BB143"));
                    if(Double.parseDouble(atributeRating.getText().toString()) == Double.parseDouble(expectedValue.getText().toString()))
                        expectedValue.setTextColor(Color.parseColor("#ffa500"));

                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime( formatter.parse( formatter.format(date) ) );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    String next_worsening="";
                    String last_worsening="";
                    String selection = "atribute=?";
                    atrib = atrib.trim();
                    String[] selectionArgs = new String[]{atrib};
                    Cursor cursor = null;
                    try {
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        cursor            = db.query(DBHelper.TABLE_ATRIBUTES, null, selection, selectionArgs, null, null, null);
                        while (cursor != null && cursor.moveToNext()) {
                            next_worsening = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_NEXT_WORSENING));
                            last_worsening = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ATRIBUTE_LAST_WORSENING));
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                    Date d1 = null;
                    Date lwd3 = null;
                    try {
                        d1 = new SimpleDateFormat("dd.MM.yyyy").parse((String) next_worsening);
                        lwd3 = new SimpleDateFormat("dd.MM.yyyy").parse((String) last_worsening);
                    } catch (ParseException e) {
                        e.printStackTrace(); }

                    date = cal.getTime();
                    Date td2 = null;
                    try {
                        td2 = formatter.parse(formatter.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace(); }


                    ContentValues cv = new ContentValues();
                    if(strName.equals("Каждый день")) {
                        cv.put("worsening_frequency", 3);
                     if(lwd3.compareTo(td2) < 0) {
                            cal.add( Calendar.DATE, 1 );
                            date = cal.getTime();
                        } else if(lwd3.compareTo(td2) == 0) {
                            cal.add( Calendar.DATE, 1 );
                            date = cal.getTime();
                        }
                        cv.put("next_worsening", formatter.format(date));
                    }
                    if(strName.equals("Каждые 7 дней")) {
                        cv.put("worsening_frequency", 2);
                        if(lwd3.compareTo(td2) < 0) {
                            long diff = td2.getTime() - lwd3.getTime();
                            diff = (diff / (1000 * 60 * 60 * 24));
                            diff = 7-diff;
                            if(diff>0)
                                cal.add(Calendar.DATE,(int) diff);
                            else
                                cal.add(Calendar.DATE,1);
                            date = cal.getTime();
                        } else if(lwd3.compareTo(td2) == 0) {
                            cal.add( Calendar.DATE, 7 );
                            date = cal.getTime();
                        }
                        cv.put("next_worsening", formatter.format(date));
                    }
                    if(strName.equals("Каждые 30 дней")) {
                        cv.put("worsening_frequency", 1);
                        if(lwd3.compareTo(td2) < 0) {
                            long diff = td2.getTime() - lwd3.getTime();
                            diff = (diff / (1000 * 60 * 60 * 24));
                            diff = 30-diff;
                            if(diff>0)
                                cal.add(Calendar.DATE,(int) diff);
                            else
                                cal.add(Calendar.DATE,1);
                            date = cal.getTime();
                        } else if(lwd3.compareTo(td2) == 0) {
                            cal.add( Calendar.DATE, 30 );
                            date = cal.getTime();
                        }
                        cv.put("next_worsening", formatter.format(date));
                    }
                    System.out.println("DBLAST: " + formatter.format(lwd3) + "DBNEXT: " + formatter.format(d1) + "CURRENT: " + formatter.format(td2)+" FINAL:" +formatter.format(date));
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    db.update(DBHelper.TABLE_ATRIBUTES, cv, "atribute= \""+atrib.trim()+"\"", null);
                    dbHelper.close();
                }
            }
        });
        builderSingle.show();
    }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onResume() {
            super.onResume();
            color1 = sp.getBoolean("color1", false);
            color2 = sp.getBoolean("color2", false);
            color3 = sp.getBoolean("color3", false);
            color4 = sp.getBoolean("color4", false);
            color5 = sp.getBoolean("color5", false);
            color6 = sp.getBoolean("color6", false);
            Window window = AtributeActivity.this.getWindow();
            CharacterFragment charac = new CharacterFragment();
            if(color1){
                atr_info_layout.setBackgroundColor(Color.parseColor("#6A105F"));
                window.setStatusBarColor(Color.parseColor("#6A105F"));
                sbar.getProgressDrawable().setColorFilter(Color.parseColor("#6A105F"), PorterDuff.Mode.MULTIPLY);
                sbar.getThumb().setColorFilter(Color.parseColor("#6A105F"), PorterDuff.Mode.SRC_ATOP);
                horizontalLine.setBackgroundColor(Color.parseColor("#6A105F"));
            }
            else if(color2){
                atr_info_layout.setBackgroundColor(Color.parseColor("#1d9dcc"));
                window.setStatusBarColor(Color.parseColor("#1d9dcc"));
                sbar.getProgressDrawable().setColorFilter(Color.parseColor("#1d9dcc"), PorterDuff.Mode.MULTIPLY);
                sbar.getThumb().setColorFilter(Color.parseColor("#1d9dcc"), PorterDuff.Mode.SRC_ATOP);
                horizontalLine.setBackgroundColor(Color.parseColor("#1d9dcc"));
            }
            else if(color3){
                atr_info_layout.setBackgroundColor(Color.parseColor("#7C7A80"));
                window.setStatusBarColor(Color.parseColor("#7C7A80"));
                sbar.getProgressDrawable().setColorFilter(Color.parseColor("#7C7A80"), PorterDuff.Mode.MULTIPLY);
                sbar.getThumb().setColorFilter(Color.parseColor("#7C7A80"), PorterDuff.Mode.SRC_ATOP);
                horizontalLine.setBackgroundColor(Color.parseColor("#7C7A80"));
            }
            else if(color4){
                atr_info_layout.setBackgroundColor(Color.parseColor("#F5CA7A"));
                window.setStatusBarColor(Color.parseColor("#F5CA7A"));
                sbar.getProgressDrawable().setColorFilter(Color.parseColor("#F5CA7A"), PorterDuff.Mode.MULTIPLY);
                sbar.getThumb().setColorFilter(Color.parseColor("#F5CA7A"), PorterDuff.Mode.SRC_ATOP);
                horizontalLine.setBackgroundColor(Color.parseColor("#F5CA7A"));
            }
            else if(color5){
                atr_info_layout.setBackgroundColor(Color.parseColor("#187A27"));
                window.setStatusBarColor(Color.parseColor("#187A27"));
                sbar.getProgressDrawable().setColorFilter(Color.parseColor("#187A27"), PorterDuff.Mode.MULTIPLY);
                sbar.getThumb().setColorFilter(Color.parseColor("#187A27"), PorterDuff.Mode.SRC_ATOP);
                horizontalLine.setBackgroundColor(Color.parseColor("#187A27"));
            }
            else if(color6){
                atr_info_layout.setBackgroundColor(Color.parseColor("#523C07"));
                window.setStatusBarColor(Color.parseColor("#523C07"));
                sbar.getProgressDrawable().setColorFilter(Color.parseColor("#523C07"), PorterDuff.Mode.MULTIPLY);
                sbar.getThumb().setColorFilter(Color.parseColor("#523C07"), PorterDuff.Mode.SRC_ATOP);
                horizontalLine.setBackgroundColor(Color.parseColor("#523C07"));
            }
            else {
                atr_info_layout.setBackgroundColor(Color.parseColor("#6A105F"));
                window.setStatusBarColor(Color.parseColor("#6A105F"));
                sbar.getProgressDrawable().setColorFilter(Color.parseColor("#6A105F"), PorterDuff.Mode.MULTIPLY);
                sbar.getThumb().setColorFilter(Color.parseColor("#6A105F"), PorterDuff.Mode.SRC_ATOP);
                horizontalLine.setBackgroundColor(Color.parseColor("#6A105F"));
            }
        }

        @Override
        protected void onPostCreate(@Nullable Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
        }

        @Override
        protected void onResumeFragments() {
            super.onResumeFragments();
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            View headerView = navigationView.getHeaderView(0);
            ImageView photo_profile = (ImageView) headerView.findViewById(R.id.photo_profile);
            byte[] data = dbHelper.getBitmapByName("profile_photo");
            if (data != null){
                Bitmap bitmap = Utils.getImage(data);
                Glide
                        .with(context2)
                        .load(bitmap)
                        .into(photo_profile);
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public void initNavigationDrawer() {
            final NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    id = menuItem.getItemId();

                    switch (id){
                        case R.id.home:
                            drawerLayout.closeDrawers();
                            finish();
                            CharacterActivity.char_act.finish();
                            break;
                        case R.id.settings:
                            drawerLayout.closeDrawers();
                            Intent intent = new Intent(AtributeActivity.this, PrefActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.trash:
                            drawerLayout.closeDrawers();
                            finish();
                            CharacterActivity.char_act.finish();
                            Intent intent2 = new Intent(AtributeActivity.this, AddActivity.class);
                            intent2.putExtra("color",String.valueOf(color));
                            startActivity(intent2);
                            break;
                        case R.id.tasks:
                            drawerLayout.closeDrawers();
                            finish();
                            CharacterActivity.char_act.finish();
                            Intent intent3 = new Intent(AtributeActivity.this, TasksActivity.class);
                            intent3.putExtra("color",String.valueOf(color));
                            startActivity(intent3);
                            break;
                        case R.id.character:
                            drawerLayout.closeDrawers();
                            finish();
                        case R.id.logout:
                            finish();

                    }
                    return true;
                }
            });

            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){
                @Override
                public void onDrawerClosed(View v){
                    super.onDrawerClosed(v);
                }

                @Override
                public void onDrawerOpened(View v) {
                    super.onDrawerOpened(v);
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    SharedPreferences prefs = getSharedPreferences("your_name", MODE_PRIVATE);
                    String name = prefs.getString("name", "your name");

                    TextView your_name = (TextView) findViewById(R.id.tv_email);
                    your_name.setText(name);
                    navHeader = (LinearLayout) findViewById(R.id.navHeader);
                    navigationView.getMenu().findItem(R.id.character).setChecked(true);
                    if(color1)
                        navHeader.setBackgroundColor(Color.parseColor("#6A105F"));
                    if(color2)
                        navHeader.setBackgroundColor(Color.parseColor("#1d9dcc"));
                    if(color3)
                        navHeader.setBackgroundColor(Color.parseColor("#7C7A80"));
                    if(color4)
                        navHeader.setBackgroundColor(Color.parseColor("#F5CA7A"));
                    if(color5)
                        navHeader.setBackgroundColor(Color.parseColor("#187A27"));
                    if(color6)
                        navHeader.setBackgroundColor(Color.parseColor("#523C07"));
                }
            };
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
        }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarValue.setText(String.valueOf(seekBar.getProgress()+1));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ContentValues cv = new ContentValues();
        cv.put("multiplier",sbar.getProgress()); //These Fields should be your String values of actual column names
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.update(DBHelper.TABLE_ATRIBUTES, cv, "atribute= \""+atrib.trim()+"\"", null);
        dbHelper.close();

    }

    public void addConnectedTasks(){
        ArrayList<Task> tasklist = new ArrayList<>();

        tasklist.clear();

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
                tasklist.add(new Task(name + " ", start_time , end_time,
                        R.drawable.ic_plus,id));
                //   System.out.println(name);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        mRecyclerView = findViewById(R.id.connected_tasks_recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ConnectedTasksRecyclerView(tasklist);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

}
