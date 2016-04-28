package info.ruebenburrowsdavies.pricefindertwo;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;


import info.ruebenburrowsdavies.pricefindertwo.R;
import info.ruebenburrowsdavies.pricefindertwo.StartSearch;

public class SettingsNew extends AppCompatActivity {

    private Context context;
    private long timesSpan = 1800000;//default 30 min is user does not select

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SettingsNew.this.context = SettingsNew.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        details();

        RadioGroup group=(RadioGroup) findViewById(R.id.radioGroup4);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(R.id.radioButton);
                RadioButton rb2 = (RadioButton) findViewById(R.id.radioButton2);
                if (rb.isChecked()) {
                    timesSpan = 1800000;
                    Toast.makeText(getBaseContext(), "30 Minutes Set", Toast.LENGTH_LONG).show();//setting sync period

                } else if (rb2.isChecked()) {
                    Toast.makeText(getBaseContext(), "12 Hours Set", Toast.LENGTH_LONG).show();
                    timesSpan = 43200000;

                } else {
                    Toast.makeText(getBaseContext(), "1 Hour Set", Toast.LENGTH_LONG).show();
                    timesSpan = 3600000;
                }
            }
        });



        Button btt2 = (Button)findViewById(R.id.button2);
        btt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText ee = (EditText)findViewById(R.id.editText4);
                String ComparePrice = ee.getText().toString();

                savePricePoint();

                //following code is to start the background service, which in turn will run sync and notifcations
                Intent alarm = new Intent(SettingsNew.this.context, AlarmReceiver.class);
                boolean alarmRunning = (PendingIntent.getBroadcast(SettingsNew.this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
                if (alarmRunning == false) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsNew.this.context, 0, alarm, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), timesSpan, pendingIntent);
                }
                Intent alarm2 = new Intent(SettingsNew.this.context, MainActivity.class);
                startActivity(alarm2);
                //end of this

                long newTime = timesSpan/60000; //display time to user in minutes variable
                String timeText = (newTime + " Minutes"); //for main menu time
                SharedPreferences.Editor editor = getSharedPreferences(StartSearch.MY_PREFS_NAME, MODE_APPEND).edit();
                editor.remove("timespan");
                editor.putString("timespan", timeText);
                editor.commit();

                Toast.makeText(getBaseContext(), "Content Saved and Notifications are running every " + newTime +" minutes", Toast.LENGTH_LONG).show(); //display message

                try {
                    PrintWriter writer = new PrintWriter(StartSearch.FILENAME);
                    writer.print("");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed",
                            Toast.LENGTH_LONG).show();
                }


            }

        });
    }

    public  void details(){

        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_APPEND);
        String site = prefs.getString("Website", "No name defined");

        if (site.equals("yes")) { //place details of product at top of page so user can see

            String ItemName = prefs.getString("DabTitle", "No item Found");
            String ItemPrice = prefs.getString("DabPrice", "No Price Found");
            // globally
            TextView ItemNameField = (TextView)findViewById(R.id.watchItem);
            //in your OnCreate() method
            ItemNameField.setText(ItemName+" £" +ItemPrice);

        } else {

            String ItemName = prefs.getString("Title", "No item Found");
            String ItemPrice = prefs.getString("price", "No Price Found");
            // globally
            TextView ItemNameField = (TextView)findViewById(R.id.watchItem);
            //in your OnCreate() method
            ItemNameField.setText(ItemName+" £" +ItemPrice);

        }


    }



    public void savePricePoint(){
        EditText ee = (EditText)findViewById(R.id.editText4);
        String ComparePrice = ee.getText().toString();

        if (ComparePrice != null){
            ComparePrice = ee.getText().toString(); //place value of edittext field into string
        }


            SharedPreferences.Editor editor = getSharedPreferences(StartSearch.MY_PREFS_NAME, MODE_APPEND).edit();
            editor.remove("pricePoint");
            editor.putString("pricePoint", ComparePrice); //put string in sharedpref to save
            editor.commit();



    }


}
