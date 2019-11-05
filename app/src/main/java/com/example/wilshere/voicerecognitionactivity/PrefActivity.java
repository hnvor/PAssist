package com.example.wilshere.voicerecognitionactivity;

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class PrefActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    ArrayList<CheckBoxPreference> cbp_list = new ArrayList<CheckBoxPreference>();
    boolean color,micro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("color1"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("color2"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("color3"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("color4"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("color5"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("color6"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro1"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro2"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro3"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro4"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro5"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro6"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro7"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro8"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("micro9"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("assist1"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("assist2"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("assist3"));
        cbp_list.add((CheckBoxPreference) getPreferenceManager()
                .findPreference("assist4"));
        for (CheckBoxPreference cbp : cbp_list) {
            Log.e("dsa", "1");
            cbp.setOnPreferenceClickListener(this);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onPreferenceClick(Preference arg0) {
        if(arg0.getKey().contains("color")){
        for (CheckBoxPreference cbp : cbp_list) {
            if(cbp.getKey().contains("color")) {
                if (!cbp.getKey().equals(arg0.getKey()) && cbp.isChecked()) {
                    cbp.setChecked(false);
                } else if (cbp.getKey().equals(arg0.getKey()) && !cbp.isChecked()) {
                    cbp.setChecked(true);
                }
            }
        }}
        else if(arg0.getKey().contains("micro")){
        for (CheckBoxPreference cbp : cbp_list) {
            if(cbp.getKey().contains("micro")){
            if (!cbp.getKey().equals(arg0.getKey()) && cbp.isChecked()) {
                cbp.setChecked(false);
            } else if (cbp.getKey().equals(arg0.getKey()) && !cbp.isChecked()) {
                cbp.setChecked(true);
            }}
        }}
        else if(arg0.getKey().contains("assist")){
            for (CheckBoxPreference cbp : cbp_list) {
                if(cbp.getKey().contains("assist")){
                    if (!cbp.getKey().equals(arg0.getKey()) && cbp.isChecked()) {
                        cbp.setChecked(false);
                    } else if (cbp.getKey().equals(arg0.getKey()) && !cbp.isChecked()) {
                        cbp.setChecked(true);
                    }}
            }}
        return false;
    }
}