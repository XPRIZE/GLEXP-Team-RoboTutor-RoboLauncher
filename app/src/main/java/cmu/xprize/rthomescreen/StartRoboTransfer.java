package cmu.xprize.rthomescreen;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * RoboSuiteLauncher
 * <p>
 * Created by kevindeland on 3/16/18.
 */

public class StartRoboTransfer extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.wtf("DEBUG_BROADCAST", "TestReceiver");

        Intent brIntent = new Intent();
        brIntent.setComponent(
                new ComponentName("cmu.xprize.service_ftp", "cmu.xprize.service_ftp.RoboTransferReceiver")
        );

        brIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);


        context.sendBroadcast(brIntent);

    }
}
