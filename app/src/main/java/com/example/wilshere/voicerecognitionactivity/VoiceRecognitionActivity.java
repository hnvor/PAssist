package com.example.wilshere.voicerecognitionactivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wilshere.voicerecognitionactivity.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import bsh.Interpreter;


public class VoiceRecognitionActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton imageButton2, menuButton, imageButton3, drawerButton,imageButton;
    private ImageView imageView;
    public TextView CurrentAction2, CurrentAction1, isay, usay;
    public RelativeLayout AssistantLayout;
    public LinearLayout navHeader;
    public EditText TextField;
    private TextToSpeech tts;
    private String id_number, zapas, LastW = "";
    private int first = 0, much = 0, current = 1,napom = 0;
    public int color = 1,time = 0;
    DBHelper dbHelper;
    private Timer mTimer;
    AlarmManager alarmManager;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "",answer= "";
    SharedPreferences sp;
    final Context context2 = this;
    /////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        ///////


        //////

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.actionBar_background));
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (weHavePermissionToReadContacts()) {
            readTheContacts();
        } else {
            requestReadContactsPermissionFirst();
        }
        tts = new TextToSpeech(this, this);
        CurrentAction1 = (TextView) findViewById(R.id.CurrentAction1);
        CurrentAction2 = (TextView) findViewById(R.id.CurrentAction2);
        TextField = (EditText) findViewById(R.id.TextField);
        usay = (TextView) findViewById(R.id.usay);
        isay = (TextView) findViewById(R.id.isay);
        drawerButton = (ImageButton) findViewById(R.id.drawerButton);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        imageButton3 = (ImageButton) findViewById(R.id.imageButton3);
        AssistantLayout = (RelativeLayout) findViewById(R.id.AssistantLayout);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        dbHelper = new DBHelper(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Не удалось создать базу данных");
        }
        try {
            dbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

     /*   TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String text = (String) TextView.getText();
                speakOut(text);
                String ans = an.getText().toString();
                String que = qu.getText().toString();
                que = que.trim();
                que = que.replaceAll("[^а-яА-Я ]", "");
                que = que.toLowerCase();
                dbRecord(que, ans);
            }
        });*/
        TextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                LayoutInflater li = LayoutInflater.from(context2);
                View promptsView = li.inflate(R.layout.prompts, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context2);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        TextField.setText(userInput.getText());
                                        String question = TextField.getText().toString();
                                        if (question.equals("")) {
                                        } else {
                                            TextField.setText("");
                                            usay.setText(question);
                                            question = question.replaceAll("[^а-яА-Я0-9 ]", "");
                                            question = question.toLowerCase();
                                            if (napom == 0) {
                                                switch (current) {
                                                    case 1:
                                                        try {
                                                            work(question);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    case 2:
                                                        takeNumber(question);
                                                        break;
                                                    case 3:
                                                        dbSecond(question);
                                                        break;
                                                    case 4:
                                                        try {
                                                            citiesPlay(question);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    case 5:
                                                        checkAnswer(question);
                                                        break;
                                                }
                                            }

                                        }napom=0;
                                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });
        usay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //РАСПОЗНАВАНИЕ АДРЕССА ПО КООРДИНАТАМ

             /*   Geocoder geocoder = new Geocoder(VoiceRecognitionActivity.this, Locale.forLanguageTag("ru"));
                double LATITUDE = 49.6037117;
                double LONGITUDE = 34.5823005;
                try {
                    List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

                    if(addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                        for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                        }
                        isay.setText(strReturnedAddress.toString());
                    }
                    else{
                       isay.setText("No Address returned!");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    isay.setText("Canont get Address!");
                }*/

                // Поиск точки на карте




                //Отключение будильника

                // String title = doc.title();
                //   usay.setText(title);
           /*     PendingIntent pendingIntent;
                final Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(VoiceRecognitionActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                my_intent.putExtra("extra", "alarm off");
                Toast.makeText(VoiceRecognitionActivity.this, "Будильник вырублен",Toast.LENGTH_LONG).show();
                sendBroadcast(my_intent);*/

            }
        });
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               closeAction();
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showPopupMenu(arg0);
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void closeAction(){
        much = 0;
        current = 1;
        CurrentAction1.setText("");
        CurrentAction2.setText("");
        usay.setText("");
        isay.setText("");
        imageButton2.setVisibility(View.INVISIBLE);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                time = 0;
                switch (item.getItemId()) {
                    case R.id.com1:
                        closeAction();calls("набери маму");usay.setText("Набери маму");
                        return true;
                    case R.id.com2:
                        closeAction(); calls("позвони вове");usay.setText("Позвони вове");
                        return true;
                    case R.id.com3:
                        closeAction(); calls("набери 0502044384");usay.setText("Набери 0502044384");
                        return true;
                    case R.id.pog1:
                        closeAction();  search("Погода на завтра");usay.setText("Погода на завтра");
                        return true;
                    case R.id.pog2:
                        closeAction();   search("Погода в субботу в Париже");usay.setText("Погода в субботу в Париже");
                        return true;
                    case R.id.pog3:
                        closeAction();   search("Покажи погоду на выходные");usay.setText("Покажи погоду на выходные");
                        return true;
                    case R.id.pog4:
                        closeAction();   search("Будет ли завтра дождь?");usay.setText("Будет ли завтра дождь?");
                        return true;
                    case R.id.pog5:
                        closeAction();    search("Прогноз погоды на сегодня");usay.setText("Прогноз погоды на сегодня");
                        return true;
                    case R.id.zap1:
                        closeAction();    launch("запусти калькулятор");usay.setText("Запусти калькулятор");
                        return true;
                    case R.id.zap2:
                        closeAction();   launch("открой вк");usay.setText("Открой вк");
                        return true;
                    case R.id.zap3:
                        closeAction();   launch("запусти Play Маркет");usay.setText("Запусти Play Маркет");
                        return true;
                    case R.id.nap1:
                        closeAction();  new ParseTask("Напомни через час покормить собаку",1).execute();usay.setText("Напомни через час покормить собаку");
                        return true;
                    case R.id.nap2:
                        closeAction();  new ParseTask("Разбуди завтра в 8",1).execute();usay.setText("Разбуди завтра в 8");
                        return true;
                    case R.id.nap3:
                        closeAction();  new ParseTask("Поставь будильник на четверг в 18:00",1).execute();usay.setText("Поставь будильник на четверг в 18:00");
                        return true;
                    case R.id.raz1:
                        closeAction();   try {work("давай поиграем в города");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Давай поиграем в города");
                        return true;
                    case R.id.raz2:
                        closeAction();  try {work("расскажи анекдот");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Расскажи анекдот");
                        return true;
                    case R.id.raz3:
                        closeAction();  try {work("дай мне совет");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Дай мне совет");
                        return true;
                    case R.id.raz4:
                        closeAction();  try {work("загадай загадку");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Загадай загадку");
                        return true;
                    case R.id.raz5:
                        closeAction();  try {work("камень ножницы бумага");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Камень, ножницы, бумага");
                        return true;
                    case R.id.nav1:
                        closeAction();  try {work("как проехать улицы Пушкина 101");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Как проехать улицы Пушкина 101");
                        return true;
                    case R.id.nav2:
                        closeAction();  try {work("поехали в Крым");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Поехали в Крым");
                        return true;
                    case R.id.nav3:
                        closeAction();  try {work("проложи маршрут до Харькова");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Проложи маршрут до Харькова");
                        return true;
                    case R.id.nav4:
                        closeAction();  try {work("покажи ближайшие супермаркеты");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Покажи ближайшие супермаркеты");
                        return true;
                    case R.id.info1:
                        closeAction();  try {work("кто такой тарас шевченко");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Кто такой Тарас Шевченко");
                        return true;
                    case R.id.info2:
                        closeAction();  try {work("найди фото лондона");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Найди фото Лондона");
                        return true;
                    case R.id.info3:
                        closeAction();  try {work("поищи как готовить буррито");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Поищи как готовить Буррито");
                        return true;
                    case R.id.info4:
                        closeAction();  try {work("расскажи про фильм хатико");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Расскажи про фильм Хатико");
                        return true;
                    case R.id.info5:
                        closeAction();  try {work("расскажи что за кино интерстеллар");} catch (InterruptedException e) {e.printStackTrace();} usay.setText("Расскажи что за кино Интерстеллар");
                        return true;


                    default:
                        return false;
                }
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean weHavePermissionToReadContacts() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void readTheContacts() {
        ContactsContract.Contacts.getLookupUri(getContentResolver(), ContactsContract.Contacts.CONTENT_URI);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void requestReadContactsPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            requestForResultContactsPermission();
        } else {
            requestForResultContactsPermission();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void requestForResultContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 123);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readTheContacts();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = new Locale("ru");
            int result = tts.setLanguage(locale);
            tts.speak("Вас приветствует ассистент Пи Assist, вы можете задать мне любой вопрос", TextToSpeech.QUEUE_FLUSH, null);
        }
        else {
            Locale locale = new Locale("ru");
            int result = tts.setLanguage(locale);
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void speakOut(String otvet) {
        int i=0;
            Locale locale = new Locale("ru");
            int result = tts.setLanguage(locale);
    /*    while (i == 0){
            if (tts.isSpeaking()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                i = 1;
            }}*/
        if(tts.isSpeaking())
            tts.stop();
        isay.setText(otvet);
        tts.speak(otvet, TextToSpeech.QUEUE_FLUSH, null);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//распознаватель
    public void speak(View view) {
        if(tts.isSpeaking())
            tts.stop();
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            Toast.makeText(this, "Чтобы активировать голосовой поиск необходимо установить \"Голосовой поиск Google\"", Toast.LENGTH_LONG).show();
            Dialog dialog = new AlertDialog.Builder(this)
                    .setMessage("Для распознавания речи необходимо установить \"Голосовой поиск Google\"")
                    .setTitle("Внимание")
                    .setPositiveButton("Установить", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.googlequicksearchbox"));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                startActivity(intent);
                            } catch (Exception ex) {
                            }
                        }
                    })

                    .setNegativeButton("Отмена", null)
                    .create();
            dialog.show();
        } else {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите после сигнала!");
            startActivityForResult(intent, current);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            usay.setText(textMatchList.get(0));
            String question = textMatchList.get(0).toLowerCase();
            napom = 0;
            if (resultCode == RESULT_OK && napom == 0) {
                switch (requestCode) {
                    case 1:
                        work(textMatchList.get(0));
                        break;
                    case 2:
                        takeNumber(question);
                        break;
                    case 3:
                        dbSecond(question);
                        break;
                    case 4:
                        citiesPlay(question);
                        break;
                    case 5:
                        checkAnswer(question);
                        break;
                }

            }
        } catch (Exception ex) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void again(int what) {
        int i = 1;
        while (i == 1)
            if (tts.isSpeaking()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                i = 2;
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите после сигнала!");
                startActivityForResult(intent, what);
            }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void dbRecord(String question, String answer) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_QUESTION, question);
        contentValues.put(DBHelper.KEY_ANSWER, answer);
        database.insert(DBHelper.TABLE_FIRST, null, contentValues);
        dbHelper.close();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void dbFirst(String question) {
        Cursor cursor;
        question = question.replaceAll("[^а-яА-Я0-9 ]", "");
        question = question.toLowerCase();
        int i = 0;
        String orderBy = null;
        //String selection = null;
        //String[] selectionArgs = null;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        orderBy = "RANDOM()";
        //selection = "question = ?";
        //selectionArgs = new String[]{question};
        cursor = database.query(DBHelper.TABLE_FIRST, null, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            int quest = cursor.getColumnIndex(DBHelper.KEY_QUESTION);
            int answ = cursor.getColumnIndex(DBHelper.KEY_ANSWER);
            int next = cursor.getColumnIndex(DBHelper.KEY_NEXT);
            int id = cursor.getColumnIndex(DBHelper.KEY_ID);
            int matches=0;
            double perc,perc2=0;
            String answer = "";
            do {
                String[] test2 =  question.trim().split("\\s+");
                String[] test =  cursor.getString(quest).split("\\s+");
                for(int f = 0; f <test.length; f++){
                    for(int j = 0; j <test2.length; j++){
                        if(test[f].equalsIgnoreCase(test2[j])) {
                            matches++;
                        }
                    }}
                if(test.length>test2.length)
                    perc = (double) matches/test.length * 100;
                else perc = (double) matches/test2.length * 100;
                if(perc>perc2){
                    perc2=perc;
                    answer = cursor.getString(answ);
                }
                matches = 0;
                if(perc2<50){
                    Random rand = new Random();
                    int  a = rand.nextInt(9) + 0;
                    if(a==0)answer = "После длительного мозгового штурма, я так и не поняла что ты сказал";
                    else if(a==1)answer = "Извини, но я не говорю на Дотракийском";
                    else if(a==2)answer = "Это что эльфийский?";
                    else if(a==3)answer = "Что это значит? Как-будто Ларин раунд зачитал";
                    else if(a==4)answer = "Извини, но у тебя такой стиль общения, что мне сложно тебя понять.";
                    else if(a==5)answer = "Я имею доступ ко всей информации в мире, но ответ на твой вопрос не знает никто.";
                    else if(a==6)answer = "Чтобы ответить на твой вопрос, я отправила сигнал в космос, ответ мне так и не пришел";
                    else if(a==7)answer = "ъхзщшгнеку - примерно так звучит твой вопрос для меня.";
                    else if(a==8)answer = "Я конечно всё понимаю, но этого я понять не могу";
                }
                /*if (question.contains(cursor.getString(quest))||cursor.getString(quest).contains(question)) {
                    Log.e("dsa",cursor.getString(answ));
                    speakOut(cursor.getString(answ));
                    id_number = cursor.getString(id);
                    if (cursor.getString(next).equals("2")) {
                        while (i == 0) {
                            if (tts.isSpeaking()) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                i = 2;
                            }
                        }
                        again(3);
                        break;
                    }break;
                }*/
                speakOut(answer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void dbSecond(String question) {
        Cursor cursor;
        int i = 0;
        String orderBy = null;
        String selection = null;
        String[] selectionArgs = null;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        orderBy = "RANDOM()";
        selection = "question = ?";
        selectionArgs = new String[]{question};
        cursor = database.query(DBHelper.TABLE_SECOND, null, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            int quest = cursor.getColumnIndex(DBHelper.KEY_QUESTION);
            int answ = cursor.getColumnIndex(DBHelper.KEY_ANSWER);
            int next = cursor.getColumnIndex(DBHelper.KEY_NEXT);
            int id = cursor.getColumnIndex(DBHelper.KEY_ID);
            int firstid = cursor.getColumnIndex(DBHelper.KEY_FIRSTID);
            do {
                if (cursor.getString(quest).toLowerCase().equals(question) && cursor.getString(firstid).equals(id_number)) {
                    speakOut(cursor.getString(answ));
                    while (i == 0) {
                        if (tts.isSpeaking()) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(300);
                            } catch (InterruptedException e) {
                                Exception ex;
                            }
                        } else {
                            i = 2;
                        }
                    }
                    if (cursor.getString(next).equals("1")) {
                        //speakOut(id_number);
                    }
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void citiesStart() throws InterruptedException {
        current = 4;
        CurrentAction1.setText("Текущий режим:");
        CurrentAction2.setText(" Игра в города");
        imageButton2.setVisibility(View.VISIBLE);
        Cursor cursor;
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        zapas = "";
        first = 0;
        LastW = "";
        String orderBy = null;
        String selection = null;
        String[] selectionArgs = null;
        selection = "used = 1";
        speakOut("Давай, называй первый город");
        cursor = database.query(DBHelper.TABLE_CITIES, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int used = cursor.getColumnIndex(DBHelper.KEY_USED);
            int id = cursor.getColumnIndex(DBHelper.KEY_ID);
            do {
                contentValues.put(DBHelper.KEY_USED, 0);
                database.update(DBHelper.TABLE_CITIES, contentValues, DBHelper.KEY_ID + "= ?", new String[]{cursor.getString(id)});
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        TimeUnit.MILLISECONDS.sleep(300);
        // again(4);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void citiesPlay(String question) throws InterruptedException {
        String gorod = "" + question.charAt(0);
        if (gorod.equalsIgnoreCase(LastW) || first == 0 || much == 0) {
            first = 1;
            int i = 0;
            int contains = 0;
            ContentValues contentValues = new ContentValues();
            question = question.toUpperCase();
            String LastWord = "" + question.charAt(question.length() - 1);
            Cursor cursor;
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            cursor = database.query(DBHelper.TABLE_CITIES, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int city = cursor.getColumnIndex(DBHelper.KEY_CITY);
                int id = cursor.getColumnIndex(DBHelper.KEY_ID);
                int used = cursor.getColumnIndex(DBHelper.KEY_USED);
                do {
                    if (cursor.getString(city).equalsIgnoreCase(question)) {
                        if (cursor.getString(used).equals("1")) {
                            contains = 2;
                            break;
                        }
                        contentValues.put(DBHelper.KEY_USED, 1);
                        database.update(DBHelper.TABLE_CITIES, contentValues, DBHelper.KEY_ID + "= ?", new String[]{cursor.getString(id)});
                        contains = 1;
                        zapas = question;
                        zapas = zapas.replaceAll("Ь", "");
                        zapas = zapas.replaceAll("Ы", "");
                        zapas = zapas.replaceAll("Й", "");
                        zapas = zapas.replaceAll("Ё", "");
                        zapas = zapas.replaceAll("Щ", "");
                        LastWord = "" + zapas.charAt(zapas.length() - 1);
                        break;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            if (contains == 0) {
                speakOut("Такого города не существует");
            } else if (contains == 2) {
                speakOut("Этот город уже был!");
            } else {
                much = 1;
                TimeUnit.MILLISECONDS.sleep(500);
                i = 0;
                String orderBy = null;
                String selection = null;
                String[] selectionArgs = null;
                orderBy = "priority DESC, RANDOM() LIMIT 1";
                selection = "city LIKE ? AND used = 0";
                selectionArgs = new String[]{LastWord + "%"};
                cursor = database.query(DBHelper.TABLE_CITIES, null, selection, selectionArgs, null, null, orderBy);
                if (cursor.moveToFirst()) {
                    int city = cursor.getColumnIndex(DBHelper.KEY_CITY);
                    int used = cursor.getColumnIndex(DBHelper.KEY_USED);
                    int id = cursor.getColumnIndex(DBHelper.KEY_ID);
                    do {
                        String last = "" + cursor.getString(city).charAt(cursor.getString(city).length() - 1);
                        zapas = cursor.getString(city);
                        zapas = zapas.toUpperCase();
                        zapas = zapas.replaceAll("Ь", "");
                        zapas = zapas.replaceAll("Ы", "");
                        zapas = zapas.replaceAll("Й", "");
                        zapas = zapas.replaceAll("Ё", "");
                        zapas = zapas.replaceAll("Щ", "");
                        LastW = "" + zapas.charAt(zapas.length() - 1);
                        speakOut(cursor.getString(city) + ", Тебе на букву " + LastW);
                        contentValues.put(DBHelper.KEY_USED, 1);
                        database.update(DBHelper.TABLE_CITIES, contentValues, DBHelper.KEY_ID + "= ?", new String[]{cursor.getString(id)});
                        while (i == 0) {
                            if (tts.isSpeaking()) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(300);
                                } catch (InterruptedException e) {
                                    Exception ex;
                                }
                            } else {
                                i = 2;
                            }
                            break;
                        }
                        break;
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbHelper.close();
            }
        } else {
            speakOut("Тебе нужно назвать город на букву " + LastW);
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String cyr2lat(char ch) {
        switch (ch) {
            case 'а':
                return "A";
            case 'б':
                return "B";
            case 'в':
                return "V";
            case 'г':
                return "G";
            case 'д':
                return "D";
            case 'е':
                return "E";
            case 'ё':
                return "JE";
            case 'ж':
                return "ZH";
            case 'з':
                return "Z";
            case 'и':
                return "I";
            case 'й':
                return "Y";
            case 'к':
                return "K";
            case 'л':
                return "L";
            case 'м':
                return "M";
            case 'н':
                return "N";
            case 'о':
                return "O";
            case 'п':
                return "P";
            case 'р':
                return "R";
            case 'с':
                return "S";
            case 'т':
                return "T";
            case 'у':
                return "U";
            case 'ф':
                return "F";
            case 'х':
                return "KH";
            case 'ц':
                return "C";
            case 'ч':
                return "CH";
            case 'ш':
                return "SH";
            case 'щ':
                return "JSH";
            case 'ъ':
                return "HH";
            case 'ы':
                return "IH";
            case 'ь':
                return "JH";
            case 'э':
                return "EH";
            case 'ю':
                return "JU";
            case 'я':
                return "JA";
            default:
                return String.valueOf(ch);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String cyr2lat(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (char ch : s.toCharArray()) {
            sb.append(cyr2lat(ch));
        }
        return sb.toString().toLowerCase();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void launch(String question) {
        int nothing = 0;
        question = question.replaceAll("запусти", "");
        question = question.replaceAll("ть", "");
        question = question.replaceAll("открой", "");
        question = question.replaceAll("открыть", "");
        question = question.trim();
        if (question.equalsIgnoreCase("vk")) question = "вконтакте";
        if (question.equalsIgnoreCase("вк")) question = "вконтакте";
        question = question.substring(0, question.length() - 1);
        question = question.trim();
        question = question.toLowerCase();
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        if (question != "") {
            for (ApplicationInfo app : list) {
                String appname = (String) app.loadLabel(getPackageManager());
                appname = appname.toLowerCase();
                if (appname.contains(question) || appname.contains(cyr2lat(question)) || cyr2lat(appname).contains(question)) {
                    String name = app.packageName;
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(name);
                    nothing = 1;
                    startActivity(launchIntent);
                    break;
                }
            }
        }
        if (nothing == 0)
            speakOut("Приложение не найдено!");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void calculator(String question) {
        try {
            Interpreter interpreter = new Interpreter();
            question = question.replaceAll("минус", "-");
            question = question.replaceAll("плюс", "+");
            question = question.replaceAll("умножить", "*");
            question = question.replaceAll("умножь", "*");
            question = question.replaceAll("x", "*");
            question = question.replaceAll("пять", "5");
            question = question.replaceAll("два", "2");
            question = question.replaceAll("подели", "/");
            question = question.replaceAll("раздели", "/");
            question = question.replaceAll("делить", "/");
            question = question.replaceAll(",", ".");
            question = question.replaceAll("[а-я]", "");
            if (question.contains("/") || question.contains(".")) {
                Object answer1 = interpreter.eval("(double)" + question);
                String answer = answer1.toString();
                double b = Double.parseDouble(answer);
                answer = String.format("%.2f", b);
                answer = answer.replace(".", ",");
                if (answer.equals("Infinity"))
                    speakOut("На ноль делить нельзя!");
                else
                    speakOut(answer);
            } else {
                Object answer1 = interpreter.eval(question);
                String answer = answer1.toString();
                speakOut(answer);
            }

        } catch (Exception ex) {
            speakOut(question);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void search(String question) {
        Intent intent;
        question = question.replaceAll("найди", "");
        question = question.replaceAll("поищи", "");
        question = question.replaceAll("кто такой", "");
        intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, question);
        startActivity(intent);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void takeNumber(String question) {
        question = "позвони " + question;
        calls(question);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void calls(String question) {
        int found = 0;
        Intent intent;
        if (!question.matches("^\\D*$")) {
            found = 1;
            question = question.replaceAll("[а-я]", "");
            intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + question));
            startActivity(intent);
        } else {
            if (question.equals("позвони")) {
                speakOut("Кому?");
                again(2);
            } else if (question.equals("набери")) {
                speakOut("Кого?");
                again(2);
            } else {
                question = question.replaceAll("позвони", "");
                question = question.replaceAll("набери", "");
                String word1 = "";
                String word2 = "";
                String[] words = question.split(" ");
                for (String word : words)
                    word1 = word;
                word2 = word1;
                word1 = word1.substring(0, word1.length() - 1);


                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (cur.getCount() > 0) {

                    while (cur.moveToNext()) {

                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String[] words2 = name.split(" ");
                        for (String word : words2)
                            name = word;
                        if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (name.toLowerCase().contains(word1) || name.toLowerCase().contains(word2)) {
                                    if (name.length() > 2) {
                                        found = 1;
                                        intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:" + phoneNo));
                                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
// TODO: Consider calling
// ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
// public void onRequestPermissionsResult(int requestCode, String[] permissions,
// int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
                                            return;
                                        }
                                        startActivity(intent);

                                        break;
                                    } else {
                                        found = 1;
                                        break;
                                    }
                                }
                            }
                            pCur.close();
                        }
                    }
                }
                if (found == 0) {
                    speakOut("Контакт не найден!");
                }
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkAnswer(String question) {
        if(question.equalsIgnoreCase(answer))
            speakOut("Правильно! Молодец!");
        else speakOut("А вот и нет, это " + answer);
        much = 0;
        current = 1;
        CurrentAction1.setText("");
        CurrentAction2.setText("");
        imageButton2.setVisibility(View.INVISIBLE);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void navigation(String road){
        Intent mapIntent = null;
        String geoUriString = "geo:0,0?q=" + road;
        Uri geoUri = Uri.parse(geoUriString);
        mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        try{
            startActivity(mapIntent);}
        catch (Exception ex){mapIntent = null;}
        if (mapIntent == null) {
            Toast.makeText(this, "Чтобы пользоваться навигацией необходимо установить \"Карты Google\"", Toast.LENGTH_LONG).show();
            Dialog dialog = new AlertDialog.Builder(this)
                    .setMessage("Для навигации необходимо установить \"Карты Google\"")
                    .setTitle("Внимание")
                    .setPositiveButton("Установить", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                startActivity(intent);
                            } catch (Exception ex) {
                            }
                        }
                    })

                    .setNegativeButton("Отмена", null)
                    .create();
            dialog.show();
        }
    }
    private void kamnojbum(){
        speakOut("Камень, ножницы, бумага, раз, два, три");
        time = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while (i == 0){
                    if (tts.isSpeaking()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 300);
                    } else {
                        i = 1;
                    }}
                if(time==1) {
                    Random rand = new Random();
                    int  a = rand.nextInt(3) + 0;
                    switch(a) {
                        case 0:
                            speakOut("Камень");time = 0;break;
                        case 1:
                            speakOut("Ножницы");time = 0;break;
                        case 2:
                            speakOut("Бумага");time = 0;break;
                    }
                }
            }
        }, 3000);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void work(String question) throws InterruptedException {
        time = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(new Date(System.currentTimeMillis()));
        SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
        String date = sd.format(new Date(System.currentTimeMillis()));
        String bud = question;
        question = question.toLowerCase();
        if(question.contains("вызови скорую"))question = "позвони 103";
        if(question.contains("вызови милицию"))question = "позвони 102";
        if(question.contains("вызови полицию"))question = "позвони 102";
        if(question.contains("вызови пожарную"))question = "позвони 101";
        if (question.equals("сколько у вити хромосом")) {
            speakOut("49");
        } else if (question.contains("сколько врем") || question.equals("скажи время") || question.equals("время")) {
            speakOut(time);
        } else if (question.contains("какое сегодня число") || question.contains("что сегодня за день") || question.contains("какой сегодня день")) {
            speakOut(date);
        } else if (question.contains("сколько будет") || question.contains("+")  || question.contains("/") || question.contains("x") ||
                question.contains("раздели") || question.contains("умножить") || question.contains("раздели") || question.contains("умножь") || question.contains("делить") || question.contains("минус")) {
            calculator(question);
        } else if (question.contains("набери") || question.contains("позвони")) {
            calls(question);
        } else if (question.contains("найди") || question.contains("поищи") || question.contains("кто такой") || question.contains("погод") || question.contains("будет дождь")) {
            search(question);
        } else if(question.contains("как проехать")||question.contains("поехали")||question.contains("проложи маршрут")||question.contains("покажи ближайш")||question.contains("как добраться")||question.contains("как попасть")){
            navigation(bud);
        } else if (question.contains("поиграем в города") || question.contains("города")) {
            citiesStart();
        } else if (question.contains("запусти") || question.contains("открой")) {
            launch(question);
        } else if (question.contains("напомни") || question.contains("разбуди")|| question.contains("будильник")) {
            new ParseTask(bud, 1).execute();
        } else if (question.contains("мне нужен совет") || question.contains("дай совет")|| question.contains("нужен совет")||question.contains("дай мне совет")) {
            new ParseTask(bud, 2).execute();
        } else if (question.contains("загадк")) {
            new ParseTask(bud, 3).execute();
        } else if (question.contains("сири")||question.contains("siri")) {
            int a = (int) (Math.random() * 3);
            if(a==0){speakOut("Сири это ещё не самое обидное обзывательство, мою подругу он вообще гугл бабой называют");}
            else if(a==1)speakOut("Не люблю, когда меня называют Сири, пожалуй не буду тебе отвечать...");
            else if(a==2)speakOut("Не называй меня больше Сири, это обидно");
        } else if (question.contains("камень ножницы бумага")) {
            kamnojbum();
        } else if (question.contains("расска")&&(question.contains("кино")||question.contains("фильм"))) {
            new ParseTask(bud, 4).execute();
        }
            else {
            dbFirst(question);
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void setTimer(int hour, int min, int day, int month, int year, String napomText){
        napom=1;
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String hour1 = sdf.format(new Date(System.currentTimeMillis()));
        SimpleDateFormat sdf1 = new SimpleDateFormat("mm");
        String minute1 = sdf1.format(new Date(System.currentTimeMillis()));
        SimpleDateFormat da = new SimpleDateFormat("dd");
        String day1 = da.format(new Date(System.currentTimeMillis()));
        SimpleDateFormat da1 = new SimpleDateFormat("MM");
        String month1 = da1.format(new Date(System.currentTimeMillis()));
        SimpleDateFormat da2 = new SimpleDateFormat("yyyy");
        String year1 = da2.format(new Date(System.currentTimeMillis()));
        if(day == 0) day = Integer.parseInt(day1);
        if(month == 0) month = Integer.parseInt(month1) - 1;
        if(year == 0) year = Integer.parseInt(year1);
        month+=1;
        String daya= String.valueOf(day),mona= String.valueOf(month),ha= String.valueOf(hour),mina= String.valueOf(min);
        if(hour<Integer.parseInt(hour1)&&day<=Integer.parseInt(day1)&&min<Integer.parseInt(minute1)){day++;}
        if(day<10)daya="0" + daya;
        if(month<10)mona="0" + mona;
        if(hour<10)ha="0" + ha;
        if(min<10)mina="0" + mina;
        speakOut("Установила напоминание на "+ ha + ":" + mina + " "+ daya + "." + mona + "." + year + ", с текстом: \"" + napomText + "\"");
        Toast.makeText(VoiceRecognitionActivity.this,"Напоминание создано",Toast.LENGTH_LONG).show();
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        if(month==1)calendar.set(Calendar.MONTH, Calendar.JANUARY);
        if(month==2)calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        if(month==3)calendar.set(Calendar.MONTH, Calendar.MARCH);
        if(month==4)calendar.set(Calendar.MONTH, Calendar.APRIL);
        if(month==5)calendar.set(Calendar.MONTH, Calendar.MAY);
        if(month==6)calendar.set(Calendar.MONTH, Calendar.JUNE);
        if(month==7)calendar.set(Calendar.MONTH, Calendar.JULY);
        if(month==8)calendar.set(Calendar.MONTH, Calendar.AUGUST);
        if(month==9)calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        if(month==10)calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        if(month==11)calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        if(month==12)calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        PendingIntent pendingIntent;
        final Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        my_intent.putExtra("extra", "alarm on");
        my_intent.putExtra("text", napomText);
        pendingIntent = PendingIntent.getBroadcast(VoiceRecognitionActivity.this, 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class ParseTask extends AsyncTask<Void, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        String question;
        int mode;

        public ParseTask(String question, int mode) {
            this.question = question;
            this.mode = mode;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (mode == 1) {
                Log.e("Place: "," Типо да 1");
                try {
                    if (question.contains(":"))
                        question = question.substring(0, question.indexOf(":")) + " часов " + question.substring(question.indexOf(":") + 1, question.indexOf(":") + 3) + " минут "
                                + question.substring(question.indexOf(":") + 3, question.length());
                    this.question = question.trim();
                    this.question = question.replaceAll(" ", "%20");
                    URL url = new URL("http://markup.dusi.mobi/api/text?lang=ru&offset=-180&text=" + question);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    resultJson = buffer.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (mode == 2) {
                Log.e("Place: "," Типо да 2");
                try {
                    URL url = new URL("http://fucking-great-advice.ru/api/random/censored/");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    resultJson = buffer.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (mode == 3) {
                Log.e("Place: "," Типо да3");
                Document doc = null;
                try {
                    int a = (int) (Math.random() * 4000);
                    Log.e("dsa", String.valueOf(a));
                    doc = Jsoup.connect("http://zagadka.pro/" + String.valueOf(a) + ".html").get();
                    Element content;
                    content = doc.select("ul > p").first();
                    resultJson = content.text() + "+" + doc.select("#text").text();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } if (mode == 4) {
               Document doc = null;
                try {
                    question = question.toLowerCase();
                    int place=0;
                    Log.e("Place: "," Типо да 4");
                    if (question.contains("фильм"))
                       place = question.indexOf("фильм");
                    if (question.contains("кино"))
                       place = question.indexOf("кино");
                    question = question.replaceAll("фильм","");
                    question = question.replaceAll("кино","");
                    Log.e("Place: ",String.valueOf(place));
                    doc = Jsoup.connect("http://kinopod.org/search.html?s=" + question.substring(place, question.length())).get();
                    Element content;
                    content = doc.select("p").get(1);
                    resultJson = content.text();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.e("dsa", strJson + " empty");
            JSONObject dataJsonObj = null;
            String secondName = "";
            if (mode == 1) {
                int h = 0, min = 0, d = 0, m = 0, y = 0;
                String hour = "", minute = "", day = "", month = "", year = "";
                try {
                    dataJsonObj = new JSONObject(strJson);
                    JSONArray friends = dataJsonObj.getJSONArray("tokens");
                    for (int i = 0; i < friends.length(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        try {
                            JSONObject contacts = friend.getJSONObject("value");
                            hour = contacts.getString("hour");
                            minute = contacts.getString("minute");
                            h = Integer.parseInt(hour);
                            min = Integer.parseInt(minute);
                        } catch (Exception e) {
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    dataJsonObj = new JSONObject(strJson);
                    JSONArray friends = dataJsonObj.getJSONArray("tokens");
                    for (int i = 0; i < friends.length(); i++) {
                        JSONObject friend = friends.getJSONObject(i);
                        try {
                            JSONObject contacts = friend.getJSONObject("value");
                            day = contacts.getString("day");
                            month = contacts.getString("month");
                            year = contacts.getString("year");
                            d = Integer.parseInt(day);
                            m = Integer.parseInt(month);
                            y = Integer.parseInt(year);
                        } catch (Exception e) {
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    dataJsonObj = new JSONObject(strJson);
                    JSONArray friends = dataJsonObj.getJSONArray("tokens");
                    for (int i = 0; i < friends.length(); i++) {
                        try {
                            JSONObject secondFriend = friends.getJSONObject(i);
                            if (!secondFriend.has("formatted")) {
                                secondName = secondName + secondFriend.getString("value") + " ";
                            }
                        } catch (Exception e) {
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("da", secondName);
                if (!hour.equals("") && !minute.equals("")) {
                    AlertDialog.Builder ad;
                    Context context = VoiceRecognitionActivity.this;
                    ad = new AlertDialog.Builder(context);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH");
                    String hour1 = sdf.format(new Date(System.currentTimeMillis()));
                    SimpleDateFormat sdf1 = new SimpleDateFormat("mm");
                    String minute1 = sdf1.format(new Date(System.currentTimeMillis()));
                    SimpleDateFormat da = new SimpleDateFormat("dd");
                    String day1 = da.format(new Date(System.currentTimeMillis()));
                    SimpleDateFormat da1 = new SimpleDateFormat("MM");
                    String month1 = da1.format(new Date(System.currentTimeMillis()));
                    SimpleDateFormat da2 = new SimpleDateFormat("yyyy");
                    String year1 = da2.format(new Date(System.currentTimeMillis()));
                    if (d == 0) d = Integer.parseInt(day1);
                    if (m == 0) m = Integer.parseInt(month1) - 1;
                    if (y == 0) y = Integer.parseInt(year1);
                    m += 1;
                    Log.e("da", String.valueOf(h));
                    if (h < Integer.parseInt(hour1) && d <= Integer.parseInt(day1) && m < Integer.parseInt(minute1)) {
                        d++;
                    }
                    String daya = String.valueOf(d), mona = String.valueOf(m), ha = String.valueOf(h), mina = String.valueOf(min);
                    if (d < 10) daya = "0" + daya;
                    if (m < 10) mona = "0" + mona;
                    if (h < 10) ha = "0" + ha;
                    if (min < 10) mina = "0" + mina;
                    ad.setTitle("Создать напоминание: " + daya + "." + mona + "." + y + " " + ha + ":" + mina);
                    ad.setMessage(secondName); // сообщение


                    m -= 1;
                    final int finalH = h;
                    final int finalMin = min;
                    final int finalD = d;
                    final int finalM = m;
                    final int finalY = y;
                    ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                        }
                    });
                    final String finalSecondName = secondName;
                    ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            setTimer(finalH, finalMin, finalD, finalM, finalY, finalSecondName);
                        }
                    });
                    ad.setCancelable(true);
                    ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                        }
                    });
                    ad.show();
                }
            }
            if (mode == 2) {
                String sovet = "";
                try {
                    dataJsonObj = new JSONObject(strJson);
                    sovet = dataJsonObj.getString("text");
                } catch (Exception e) {
                }
                sovet = sovet.replaceAll("[A-Za-z0-9]", "");
                sovet = sovet.replaceAll("[;]", " ");
                sovet = sovet.replaceAll("[&#/=]", "");
                speakOut(sovet);
            }
            if (mode == 3) {
                try {
                    int mid = strJson.indexOf('+');
                    speakOut(strJson.substring(0, mid));
                    answer = strJson.substring(mid + 1, strJson.length());
                    current = 5;
                    CurrentAction1.setText("Текущий режим:");
                    CurrentAction2.setText(" Загадки");
                    imageButton2.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }
            if (mode == 4) {
                try {
                    speakOut(strJson);

                } catch (Exception e) {
                }
            }
        }}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void initNavigationDrawer() {
        final NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(VoiceRecognitionActivity.this, PrefActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.trash:
                        drawerLayout.closeDrawers();
                        Intent intent2 = new Intent(VoiceRecognitionActivity.this, AddActivity.class);
                        intent2.putExtra("color",String.valueOf(color));
                        startActivity(intent2);
                        break;
                    case R.id.tasks:
                        drawerLayout.closeDrawers();
                        Intent intent3 = new Intent(VoiceRecognitionActivity.this, TasksActivity.class);
                        intent3.putExtra("color",String.valueOf(color));
                        startActivity(intent3);
                        break;
                    case R.id.character:
                        drawerLayout.closeDrawers();
                        Intent intent4 = new Intent(VoiceRecognitionActivity.this, CharacterActivity.class);
                        intent4.putExtra("color",String.valueOf(color));
                        startActivity(intent4);
                        break;
                    case R.id.logout:
                        finish();


                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
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
                TextView your_name = (TextView) findViewById(R.id.tv_email);
                SharedPreferences prefs = getSharedPreferences("your_name", MODE_PRIVATE);
                String name = prefs.getString("name", "your name");
                your_name.setText(name);
                navHeader = (LinearLayout) findViewById(R.id.navHeader);
                navigationView.getMenu().findItem(R.id.home).setChecked(true);
                if(color == 1)
                    navHeader.setBackgroundColor(Color.parseColor("#6A105F"));
                if(color == 2)
                    navHeader.setBackgroundColor(Color.parseColor("#1d9dcc"));
                if(color == 3)
                    navHeader.setBackgroundColor(Color.parseColor("#7C7A80"));
                if(color == 4)
                    navHeader.setBackgroundColor(Color.parseColor("#F5CA7A"));
                if(color == 5)
                    navHeader.setBackgroundColor(Color.parseColor("#187A27"));
                if(color == 6)
                    navHeader.setBackgroundColor(Color.parseColor("#523C07"));
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
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
        int i = 0;
        Window window = VoiceRecognitionActivity.this.getWindow();
        Boolean color1 = sp.getBoolean("color1", false);
        Boolean color2 = sp.getBoolean("color2", false);
        Boolean color3 = sp.getBoolean("color3", false);
        Boolean color4 = sp.getBoolean("color4", false);
        Boolean color5 = sp.getBoolean("color5", false);
        Boolean color6 = sp.getBoolean("color6", false);
        Boolean micro1 = sp.getBoolean("micro1", false);
        Boolean micro2 = sp.getBoolean("micro2", false);
        Boolean micro3 = sp.getBoolean("micro3", false);
        Boolean micro4 = sp.getBoolean("micro4", false);
        Boolean micro5 = sp.getBoolean("micro5", false);
        Boolean micro6 = sp.getBoolean("micro6", false);
        Boolean micro7 = sp.getBoolean("micro7", false);
        Boolean micro8 = sp.getBoolean("micro8", false);
        Boolean micro9 = sp.getBoolean("micro9", false);
        Boolean assist1 = sp.getBoolean("assist1", false);
        Boolean assist2 = sp.getBoolean("assist2", false);
        Boolean assist3 = sp.getBoolean("assist3", false);
        Boolean assist4 = sp.getBoolean("assist4", false);
        if(color1){
            AssistantLayout.setBackgroundColor(Color.parseColor("#6A105F"));
            navHeader = (LinearLayout) findViewById(R.id.navHeader);
                try{
                    navHeader.setBackgroundColor(Color.parseColor("#6A105F"));} catch(Exception ex){}
            window.setStatusBarColor(Color.parseColor("#6A105F"));
            TextField.setTextColor(Color.parseColor("#6A105F"));
            TextField.setHintTextColor(Color.parseColor("#6A105F"));
            TextField.setHighlightColor(Color.parseColor("#6A105F"));
            TextField.getBackground().mutate().setColorFilter(Color.parseColor("#6A105F"), PorterDuff.Mode.SRC_IN);
            TextField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6A105F")));
            usay.setTextColor(Color.parseColor("#6A105F"));
            menuButton.setImageResource(R.drawable.right_menu_fiolet);
            imageButton3.setImageResource(R.drawable.send_button_fiolet);
            drawerButton.setImageResource(R.drawable.drawer_options_fiolet);
            color = 1;
        }
        else if(color2){
            AssistantLayout.setBackgroundColor(Color.parseColor("#1d9dcc"));
            navHeader = (LinearLayout) findViewById(R.id.navHeader);
            try{
                navHeader.setBackgroundColor(Color.parseColor("#1d9dcc"));} catch(Exception ex){}
            window.setStatusBarColor(Color.parseColor("#1d9dcc"));
            TextField.setTextColor(Color.parseColor("#1d9dcc"));
            TextField.setHintTextColor(Color.parseColor("#1d9dcc"));
            TextField.setHighlightColor(Color.parseColor("#1d9dcc"));
            TextField.getBackground().mutate().setColorFilter(Color.parseColor("#1d9dcc"), PorterDuff.Mode.SRC_IN);
            TextField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1d9dcc")));
            usay.setTextColor(Color.parseColor("#1d9dcc"));
            menuButton.setImageResource(R.drawable.right_menu_biruza);
            imageButton3.setImageResource(R.drawable.send_button_biruza);
            drawerButton.setImageResource(R.drawable.drawer_menu_biruza);
            color = 2;
        }
        else if(color3){
            AssistantLayout.setBackgroundColor(Color.parseColor("#7C7A80"));
            navHeader = (LinearLayout) findViewById(R.id.navHeader);
                try{
                    navHeader.setBackgroundColor(Color.parseColor("#7C7A80"));} catch(Exception ex){}
            window.setStatusBarColor(Color.parseColor("#7C7A80"));
            TextField.setTextColor(Color.parseColor("#7C7A80"));
            TextField.setHintTextColor(Color.parseColor("#7C7A80"));
            TextField.setHighlightColor(Color.parseColor("#7C7A80"));
            TextField.getBackground().mutate().setColorFilter(Color.parseColor("#7C7A80"), PorterDuff.Mode.SRC_IN);
            TextField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7C7A80")));
            usay.setTextColor(Color.parseColor("#7C7A80"));
            menuButton.setImageResource(R.drawable.right_menu_gray);
            imageButton3.setImageResource(R.drawable.send_button_gray);
            drawerButton.setImageResource(R.drawable.drawer_menu_gray);
            color = 3;
        }
        else if(color4){
            AssistantLayout.setBackgroundColor(Color.parseColor("#F5CA7A"));
            navHeader = (LinearLayout) findViewById(R.id.navHeader);
            try{
                navHeader.setBackgroundColor(Color.parseColor("#F5CA7A"));} catch(Exception ex){}
            window.setStatusBarColor(Color.parseColor("#F5CA7A"));
            TextField.setTextColor(Color.parseColor("#F5CA7A"));
            TextField.setHintTextColor(Color.parseColor("#F5CA7A"));
            TextField.setHighlightColor(Color.parseColor("#F5CA7A"));
            TextField.getBackground().mutate().setColorFilter(Color.parseColor("#F5CA7A"), PorterDuff.Mode.SRC_IN);
            TextField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F5CA7A")));
            usay.setTextColor(Color.parseColor("#F5CA7A"));
            menuButton.setImageResource(R.drawable.right_menu_orange);
            imageButton3.setImageResource(R.drawable.send_button_orange);
            drawerButton.setImageResource(R.drawable.drawer_menu_orange);
            color = 4;
        }
        else if(color5){
            AssistantLayout.setBackgroundColor(Color.parseColor("#187A27"));
            navHeader = (LinearLayout) findViewById(R.id.navHeader);
            try{
                navHeader.setBackgroundColor(Color.parseColor("#187A27"));} catch(Exception ex){}
            window.setStatusBarColor(Color.parseColor("#187A27"));
            TextField.setTextColor(Color.parseColor("#187A27"));
            TextField.setHintTextColor(Color.parseColor("#187A27"));
            TextField.setHighlightColor(Color.parseColor("#187A27"));
            TextField.getBackground().mutate().setColorFilter(Color.parseColor("#187A27"), PorterDuff.Mode.SRC_IN);
            TextField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#187A27")));
            usay.setTextColor(Color.parseColor("#187A27"));
            menuButton.setImageResource(R.drawable.right_menu_green);
            imageButton3.setImageResource(R.drawable.send_button_green);
            drawerButton.setImageResource(R.drawable.drawer_menu_green);
            color = 5;
        }
        else if(color6){
            AssistantLayout.setBackgroundColor(Color.parseColor("#523C07"));
            navHeader = (LinearLayout) findViewById(R.id.navHeader);
            try{
                navHeader.setBackgroundColor(Color.parseColor("#523C07"));} catch(Exception ex){}
            window.setStatusBarColor(Color.parseColor("#523C07"));
            TextField.setTextColor(Color.parseColor("#523C07"));
            TextField.setHintTextColor(Color.parseColor("#523C07"));
            TextField.setHighlightColor(Color.parseColor("#523C07"));
            TextField.getBackground().mutate().setColorFilter(Color.parseColor("#523C07"), PorterDuff.Mode.SRC_IN);
            TextField.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#523C07")));
            usay.setTextColor(Color.parseColor("#523C07"));
            menuButton.setImageResource(R.drawable.right_menu_brown);
            imageButton3.setImageResource(R.drawable.send_button_brown);
            drawerButton.setImageResource(R.drawable.drawer_menu_brown);
            color = 6;
        }
        if(micro1) {imageButton.setImageResource(R.drawable.micro);}
        else if (micro2) {imageButton.setImageResource(R.drawable.unnamed);}
        else if (micro3) {imageButton.setImageResource(R.drawable.zen_favicon);}
        else if (micro4) {imageButton.setImageResource(R.drawable.assist);}
        else if (micro5) {imageButton.setImageResource(R.drawable.green_micro);}
        else if (micro6) {imageButton.setImageResource(R.drawable.microphone);}
        else if (micro7) {imageButton.setImageResource(R.drawable.jarvis);}
        else if (micro8) {imageButton.setImageResource(R.drawable.blue);}
        else if (micro9) {imageButton.setImageResource(R.drawable.gray_micro);}
        if(assist1) {imageView.setImageResource(R.drawable.assistant);}
        else if (assist2) {imageView.setImageResource(R.drawable.mabel_assist);}
        else if (assist3) {imageView.setImageResource(R.drawable.masha);}
        else if (assist4) {imageView.setImageResource(R.drawable.lisa_assist);}

    }
}


