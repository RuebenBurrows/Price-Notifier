package info.ruebenburrowsdavies.pricefindertwo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView StartProcess = (CardView)findViewById(R.id.cvc1);

        StartProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+RuebenBurrows"));
                startActivity(browserIntent); //load webpage up in browser
            }
        });

        CardView HistProcess = (CardView)findViewById(R.id.cvc2);

        HistProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"ruebenburrows@gmail.com"}); //set email header etc then open email client
                i.putExtra(Intent.EXTRA_SUBJECT, "About Price Finder");

                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(contact.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        CardView SettProcess = (CardView)findViewById(R.id.cvc3);

        SettProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ruebenburrowsdavies.info/"));
                startActivity(browserIntent); //go to my website
            }
        });
    }

}
