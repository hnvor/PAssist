package com.example.wilshere.voicerecognitionactivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wilshere.voicerecognitionactivity.Utils.Utils;

import java.io.IOException;

public class CharacterActivity extends AppCompatActivity {
    public LinearLayout navHeader;
    SharedPreferences sp;
    private DrawerLayout drawerLayout;
   // private Toolbar toolbar2;
    int id,color;
    final Context context2 = this;
    Boolean color1,color2,color3,color4,color5,color6;
    RecyclerView rv;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    LinearLayout chBackground;
    DBHelper dbHelper;
    public static Activity char_act;
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
        dbHelper = new DBHelper(context2);
        try {
            dbHelper.createDataBase();}
        catch (IOException ioe) {
            throw new Error("Не удалось создать базу данных");}
        try {
            dbHelper.openDataBase();}
        catch (SQLException sqle) {
            throw sqle;}
        char_act = this;
        setContentView(R.layout.character_activity);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String color2 = getIntent().getStringExtra("color");
        color = Integer.parseInt(color2);
        navHeader = (LinearLayout) findViewById(R.id.navHeader);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);


        viewPager = (ViewPager) findViewById(R.id.viewpager_char_id);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new CharacterFragment(),"frag");
        viewPager.setAdapter(adapter);
        initNavigationDrawer();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView photo_profile = (ImageView) headerView.findViewById(R.id.photo_profile);
        byte[] data = dbHelper.getBitmapByName("profile_photo");
        if (data != null){
            Bitmap bitmap = Utils.getImage(data);
            //  profilePhoto.setImageBitmap(bitmap);
            Glide
                    .with(context2)
                    .load(bitmap)
                    .into(photo_profile);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        Window window = CharacterActivity.this.getWindow();
        CharacterFragment charac = new CharacterFragment();
        if(color1){
            window.setStatusBarColor(Color.parseColor("#6A105F"));
          //  charac.setChBackground("#6A105F");
        }
        else if(color2){
            window.setStatusBarColor(Color.parseColor("#1d9dcc"));
       //     charac.setChBackground("#1d9dcc");
        }
        else if(color3){
            window.setStatusBarColor(Color.parseColor("#7C7A80"));
      //      charac.setChBackground("#7C7A80");
        }
        else if(color4){
            window.setStatusBarColor(Color.parseColor("#F5CA7A"));
       //     charac.setChBackground("#F5CA7A");
        }
        else if(color5){
            window.setStatusBarColor(Color.parseColor("#187A27"));
         //   charac.setChBackground("#187A27");
        }
        else if(color6){
            window.setStatusBarColor(Color.parseColor("#523C07"));
         //   charac.setChBackground("#523C07");
        }
        else {
            window.setStatusBarColor(Color.parseColor("#6A105F"));
         //   charac.setChBackground("#6A105F");
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
            //  profilePhoto.setImageBitmap(bitmap);
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
                        break;
                    case R.id.settings:
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(CharacterActivity.this, PrefActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.trash:
                        drawerLayout.closeDrawers();
                        finish();
                        Intent intent2 = new Intent(CharacterActivity.this, AddActivity.class);
                        intent2.putExtra("color",String.valueOf(color));
                        startActivity(intent2);
                        break;
                    case R.id.tasks:
                        drawerLayout.closeDrawers();
                        finish();
                        Intent intent3 = new Intent(CharacterActivity.this, TasksActivity.class);
                        intent3.putExtra("color",String.valueOf(color));
                        startActivity(intent3);
                        break;
                    case R.id.character:
                        drawerLayout.closeDrawers();
                        break;
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
}
