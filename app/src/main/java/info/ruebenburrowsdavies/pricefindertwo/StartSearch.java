package info.ruebenburrowsdavies.pricefindertwo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StartSearch extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static String search,website;
    private ListView lv;
    public List<String> myList = new ArrayList<String>();
    static List<String> myList2 = new ArrayList<String>();
    static List<String> myList3 = new ArrayList<String>();
    static List<String> myList22 = new ArrayList<String>();
    public RadioButton rGroup,rGroup1;
    static String desFinal, htmlLink,th;
    static final String MY_PREFS_NAME = "MyPrefsFile";
    static String FILENAME = "hello_file.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_search);

        NavSetUp(); //set up navigation menu
        amazonRadioButton();
        ebuyerRadioButton();

        findViewById(R.id.progressBar).setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //listen for fab button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtDescription = (EditText) findViewById(R.id.editText2); //take value from edittext field and assign to search
                search = txtDescription.getText().toString();//get search item and pass to string to add to the html in dobackground method


                rGroup = (RadioButton) findViewById(R.id.radio1);

                Toast.makeText(getApplicationContext(), "Results coming please be patient",
                        Toast.LENGTH_LONG).show();

                if (rGroup.isChecked()) {
                    website = "yes"; //for dabs assign yes for later use
                } else {
                    website = "no"; //for ebuyer assign no for later use
                }

                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                new Title().execute();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtDescription.getWindowToken(), 0);


            }
        });

        ListView lv2 = (ListView)findViewById(R.id.listView22);
            lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                    stopService(new Intent(StartSearch.this, BackgroundService.class));

                    if (website == "yes") {
                        final String selected = (String) arg0.getItemAtPosition(position); //GET VALUE AT POSITION
                        final String htmllink2 = myList3.get(position); //get item html link

                        AlertDialog.Builder builder = new AlertDialog.Builder(StartSearch.this); //BUILD A CONFIRMATION DIALOGUE

                        builder.setTitle("Are you sure you want to select this?");
                        builder.setMessage("The previous item will be cleared");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                String th2 = selected.split("\\£")[0]; //get Product Title
                                String FinalDabsLink = "http://www.dabs.com" + htmllink2; //get link of product for future notifcations
                                String selected2 = selected.substring(selected.lastIndexOf("£") + 1); //get price of product, numbers only

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_APPEND).edit(); //open sharedpref

                                editor.clear();
                                editor.putString("DabLink", FinalDabsLink); //apply values to sharedpref
                                editor.putString("DabTitle", th2); //product title for history later
                                editor.putString("DabPrice", selected2);
                                editor.putString("Website", website); //find out which website has been selected either yes or no

                                editor.commit();
                                myList.clear(); //clear view
                                myList2.clear();
                                myList3.clear();

                                Intent intent5 = new Intent(StartSearch.this, SettingsNew.class); //open setting area
                                startActivity(intent5);
                            }

                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();


                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StartSearch.this); //BUILD A CONFIRMATION DIALOGUE

                        desFinal = (String) arg0.getItemAtPosition(position);
                        htmlLink = myList2.get(position); //get html link for later

                        builder.setTitle("Are you sure you want to select this?");//start building a user confmation menu box
                        builder.setMessage("The previous item will be cleared");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                                th = desFinal.split("\\£")[0]; //get title of product
                                desFinal = desFinal.substring(desFinal.lastIndexOf("£") + 1, desFinal.lastIndexOf("inc."));

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_APPEND).edit();
                                editor.clear();
                                editor.putString("links", htmlLink);
                                editor.putString("Title", th); //apply values
                                editor.putString("price", desFinal);
                                editor.putString("Website", website);
                                editor.commit();
                                myList.clear();
                                myList2.clear();


                                Intent intent5 = new Intent(StartSearch.this, SettingsNew.class); //open setting area
                                startActivity(intent5);
                            }

                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                }
            });


        EditText clear = (EditText)findViewById(R.id.editText2); //listen for touch on edit text field
        clear.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                myList.clear();
                myList2.clear();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//bring keyboard up
                return false;

            }
        });

    }


    public void fillListView(){
        ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>( //fill listview contents with array
                this,
                android.R.layout.simple_list_item_1,
                myList);//fill adapter with mylist items

        lv = (ListView)findViewById(R.id.listView22);
        lv.setAdapter(arrayAdapter); //fill with adapter
    }
    public void amazonRadioButton(){//notify user
        rGroup = (RadioButton)findViewById(R.id.radio1);
        rGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lv == null) {
                    myList.clear();
                    Snackbar.make(view, "Dabs selected", Snackbar.LENGTH_LONG) //tell user information
                            .setAction("Action", null).show();

                } else {
                    myList.clear();
                    lv.setAdapter(null);
                    Snackbar.make(view, "Dabs selected & Content Cleared", Snackbar.LENGTH_LONG) //tell user information
                            .setAction("Action", null).show();

                }
            }
        });
    }
    public void ebuyerRadioButton(){ //notify user
        rGroup1 = (RadioButton)findViewById(R.id.radio2);
        rGroup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lv == null) {
                    myList.clear();
                    Snackbar.make(view, "eBuyer selected", Snackbar.LENGTH_LONG) //tell user information
                            .setAction("Action", null).show();
                } else {
                    myList.clear();
                    lv.setAdapter(null);
                    Snackbar.make(view, "eByer selected & Content Cleared", Snackbar.LENGTH_LONG) //tell user information
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private class Title extends AsyncTask<Void, Void, Void> { //method for background data

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (website == "yes"){ //one selected website do this

                    try{
                        Document doc = Jsoup.connect("http://www.dabs.com/search?q=" + search).timeout(10000).userAgent("Mozilla/5.0").get(); //get the document from link/user input
                        Elements sections = doc.select("tr");//get table items
                        for (Element section : sections) {

                            //get all data from search and put into elements then go through each one on page,
                            //selcting and finding the title, value and html link  for that product then fill an areraylist for later use.


                            section.select("td.description span.mfr-no").remove() ;
                            section.select("td.description span.line-alerts").remove();
                            String title = section.select("td.description").text();
                            String price = section.select("td.price").text();

                            Elements mm = section.select("a[href]");
                            String item = mm.attr("href"); //get article link

                            myList.add(title + "\n" + price); //add title and price to array
                            myList3.add(item); //add product link to array

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else{
                    // Connect to the web site
                    Document doc = Jsoup.connect("http://www.ebuyer.com/search?q=" + search).timeout(10000).userAgent("Mozilla/5.0").get(); //get website data
                    Elements sections = doc.select("div.listing-product"); //get all data from search and put into elements then go through each one on page,
                    //selcting and finding the title, value and html link  for that product then fill an areraylist for later use.

                    for (Element section : sections) {

                        section.select("span.saving").remove();
                        section.select("span.was").remove();
                        Elements mm = section.select("a[href]");
                        String item = mm.attr("href");
                        String finalLink = ("http://www.ebuyer.com"+item);

                        String title = section.select("h3.listing-product-title").text();
                        String price = section.select("div.inc-vat").text();

                        myList.add(title + " --- " + price);//add title and price to array
                        myList2.add(finalLink);//add product link to array

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            if (website == "yes"){

               if (myList==null){
                   Toast.makeText(getBaseContext(), "No Items found please try again", Toast.LENGTH_LONG).show();

               }else {
                   findViewById(R.id.progressBar).setVisibility(View.GONE); //get rid of loading circle
                   fillListView();

               }

            }else {
                if (myList==null){
                    Toast.makeText(getBaseContext(), "No Items found please try again", Toast.LENGTH_LONG).show();

                }else {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);//get rid of loading circle
                    fillListView();

                }

            }
        }
    }



    public void NavSetUp() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.start_search, menu);
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
            Intent intent5 = new Intent(StartSearch.this, SettingsNew.class);
            startActivity(intent5);
        } else if (id == R.id.action_contact){
            Intent intent5 = new Intent(StartSearch.this, contact.class);
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
            Intent intent = new Intent(StartSearch.this, StartSearch.class);
            startActivity(intent);

        } else if (id == R.id.nav_history) {
            Intent intent1 = new Intent(StartSearch.this, History.class);
            startActivity(intent1);
        } else if (id == R.id.nav_settings) {
            Intent intent5 = new Intent(StartSearch.this, SettingsNew.class);
            startActivity(intent5);
        } else if (id == R.id.nav_home) {
            Intent intent2 = new Intent(StartSearch.this, MainActivity.class);
            startActivity(intent2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
