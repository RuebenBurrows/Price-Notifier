package info.ruebenburrowsdavies.pricefindertwo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by rueben on 03/12/2015.
 */
public class BackgroundService extends Service {


    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    private String price, name,price3,Dabslink; //yes is a Amazon Search, no is an eBuyer search,test;
    public String  newPrice="0", test= "yes",AmazonName,WebsiteChoice;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_APPEND);
        AmazonName = prefs.getString("DabLink", "No Name");
        WebsiteChoice = prefs.getString("Website", "yes");
        name = prefs.getString("links", "No name defined"); //inilizle all vairables before assigning values to them first here
        Dabslink = prefs.getString("DabLink", "No name defined");


        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);


    }

    private Runnable myTask = new Runnable() {
        public void run() {

            mainMeth();// run method
            stopSelf(); //stop

        }
    };

    public void mainMeth(){

        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_APPEND);
        AmazonName = prefs.getString("DabLink", "No Name"); //get html link that has been saved


        if(AmazonName != null){ //make sure html string is no empty THIS IS FOR DABS NOT AMAZON BY THE WAY :)

            if (WebsiteChoice .equals(test)){ //if trun run following for dabs

                try
                {
                    Document doc = Jsoup.connect(AmazonName).timeout(10000).userAgent("Mozilla/5.0").get();
                    newPrice = doc.select("div.price-container span.lprice").text(); //get new product price from page in html link


                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String doublePrice = newPrice.substring(newPrice.indexOf("£") + 1); // turn to double to allow comparison in if statment
                    Double foo = Double.parseDouble(doublePrice);
                    String PricePoint = prefs.getString("pricePoint", "No name defined");
                    Double ComparePrice = Double.parseDouble(PricePoint);

                    if (foo <= ComparePrice){

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Dabslink));
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                context,
                                0,
                                browserIntent,
                                Intent.FLAG_ACTIVITY_NEW_TASK); //set up notifcation for dabs

                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(BackgroundService.this)
                                        .setSmallIcon(R.drawable.ic_stat_name2)
                                        .setContentTitle("Your Dabs item is on sale.")
                                        .setContentInfo("Click to buy")
                                        .setContentText("The Price is : £" + foo)
                                        .setContentIntent(pendingIntent)
                                        .setDefaults(Notification.DEFAULT_SOUND)
                                        .setAutoCancel(true)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000})
                                        .setLights(Color.RED, 3000, 3000);

                        int NOTIFICATION_ID = 12345;

                        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nManager.notify(NOTIFICATION_ID, builder.build());
                        try {

                            String TitleNew = prefs.getString("DabTitle", "No name defined"); //write content for history

                            String productSaved = TitleNew + " £" + doublePrice + "\r\n";


                            FileOutputStream fos = openFileOutput(StartSearch.FILENAME, Context.MODE_APPEND);
                            String curretnDate = DateFormat.getDateInstance().format(new Date());
                            OutputStreamWriter osw = new OutputStreamWriter(fos);


                            final SpannableStringBuilder sb = new SpannableStringBuilder(curretnDate);
                            final StyleSpan bss = new StyleSpan(Typeface.BOLD);
                            sb.setSpan(bss, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                            osw.write(sb + " --- " + productSaved);
                            osw.flush();
                            osw.close();


                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Failed",
                                    Toast.LENGTH_LONG).show();
                        }

                    }else {

                        return;

                    }

                }catch (Throwable e) {
                    e.printStackTrace();
                }

            }else{
                eBuyerStuff();
            }

        }


    }

    public  void eBuyerStuff(){

        SharedPreferences prefs = getSharedPreferences(StartSearch.MY_PREFS_NAME, MODE_APPEND);

        name = prefs.getString("links", "No name defined"); //get html link for ebyer

        try {

            Document doc = Jsoup.connect(name).timeout(10000).userAgent("Mozilla/5.0").get(); //run jsoup to get new price only
            Elements sections = doc.select("div.product-price");
            price = sections.select("p.price").text();


        } catch (IOException e) {
            e.printStackTrace();
        }

        price3 = price.substring(price.indexOf("£") + 1, price.lastIndexOf("inc.") - 1); //take away string in price

        try {

            String TitleNew = prefs.getString("Title", "No name defined");

            String productSaved = TitleNew + " £" + price3 + "\r\n"; //right new content gathered to a file to display in the histroy


            FileOutputStream fos = openFileOutput(StartSearch.FILENAME, Context.MODE_APPEND);
            String curretnDate = DateFormat.getDateInstance().format(new Date());
            OutputStreamWriter osw = new OutputStreamWriter(fos);


            final SpannableStringBuilder sb = new SpannableStringBuilder(curretnDate);
            final StyleSpan bss = new StyleSpan(Typeface.BOLD);
            sb.setSpan(bss, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            osw.write(sb + " --- " + productSaved);
            osw.flush();
            osw.close();


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed",
                    Toast.LENGTH_LONG).show();
        }

        try {
            Double foo = Double.parseDouble(price3); //turn string price into double to coompare
            String PricePoint = prefs.getString("pricePoint", "No name defined");
            Double ComparePrice = Double.parseDouble(PricePoint);

            if (foo <= ComparePrice){ //compare to user entered price point that has been saved

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(name)); //start and build the notifcation if true
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        browserIntent,
                        Intent.FLAG_ACTIVITY_NEW_TASK);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(BackgroundService.this)
                                .setSmallIcon(R.drawable.ic_stat_name2)
                                .setContentTitle("Your eBuyer item is on sale.")
                                .setContentInfo("Click to buy")
                                .setContentText("The Price is : £" + foo)
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setAutoCancel(true)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000}) //set virbrate & color of led
                                .setLights(Color.RED, 3000, 3000);

                int NOTIFICATION_ID = 12345;

                NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nManager.notify(NOTIFICATION_ID, builder.build());


            }else {

                return;

            }

        }catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

}
