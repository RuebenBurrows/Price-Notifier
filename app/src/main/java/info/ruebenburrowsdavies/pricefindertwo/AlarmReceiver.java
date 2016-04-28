package info.ruebenburrowsdavies.pricefindertwo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by rueben on 03/12/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent background = new Intent(context, BackgroundService.class);
        context.startService(background);
    }

}
