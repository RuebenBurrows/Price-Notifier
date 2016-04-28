package info.ruebenburrowsdavies.pricefindertwo;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;


import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//set view to activty_main

        // configure Flurry
        FlurryAgent.setLogEnabled(false);

        // init Flurry
        FlurryAgent.init(this, "J46PTPWJT9RJ2CZ3CS9B");//use own key

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //sets up the navigation drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);


        new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US)); //set decinmal to local standard



        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_APPEND);
        String currentPrice = prefs.getString("pricePoint", "No Price Point Set");
        String itemName = prefs.getString("DabTitle", "No product title found");
        String time = prefs.getString("timespan", "No Sync time set");
        String site = prefs.getString("Website", "No name defined");
        String ebuyerTitle = prefs.getString("Title", "No product title found");


        if (currentPrice != null){

            if (site.equals("yes")){
                TextView watchprice =(TextView)findViewById(R.id.textView3); //apply product and price to main screen
                watchprice.setText(itemName + "\n"+" Set Watch Price: £"+currentPrice);
                TextView syncperiod =(TextView)findViewById(R.id.textView4);
                syncperiod.setText("Sync Period: " + time);
            }else {

                TextView watchprice =(TextView)findViewById(R.id.textView3); //apply product and price to main screen
                watchprice.setText(ebuyerTitle + "\n"+" Set Watch Price: £"+currentPrice);
                TextView syncperiod =(TextView)findViewById(R.id.textView4);
                syncperiod.setText("Sync Period: " + time);
            }


        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent5 = new Intent(MainActivity.this, SettingsNew.class);
            startActivity(intent5);
        } else if (id == R.id.action_contact){
            Intent intent5 = new Intent(MainActivity.this, contact.class);
            startActivity(intent5);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {
            Intent intent = new Intent(MainActivity.this, StartSearch.class);
            startActivity(intent);

        } else if (id == R.id.nav_history) {
            Intent intent1 = new Intent(MainActivity.this, History.class);
            startActivity(intent1);
        } else if (id == R.id.nav_settings) {
            Intent intent5 = new Intent(MainActivity.this, SettingsNew.class);
            startActivity(intent5);
        } else if (id == R.id.nav_home) {
            Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
