package cmu.xprize.rthomescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * RoboSuiteLauncher
 * <p>
 * Created by kevindeland on 3/16/18.
 */

public class ConfigureWifi extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.wtf("DEBUG_WIFI", "Received Broadcast");

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = context.getResources().getString(R.string.wifi_ssid);

        // update security
        String security = context.getResources().getString(R.string.wifi_security);

        switch(security) {
            case "WPA":
            case "WPA2":
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

                // only set password if security
                String password = context.getResources().getString(R.string.wifi_pw);

                if (password.matches("[0-9A-Fa-f]{64}")) {
                    config.preSharedKey = password;
                } else {
                    config.preSharedKey = getQuotedString(password);
                }

                break;

            case "":
            default:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
        }

        Log.w("DEBUG_WIFI", "SSID = " + config.SSID + ", Password = " + config.preSharedKey);

        boolean success = saveWifiConfiguration(context, config);
        Log.wtf("DEBUG_WIFI", "Changed config " + success);

    }


    private String getQuotedString(String string) {
        return "\"" + string + "\"";
    }

    public static boolean saveWifiConfiguration(Context context, WifiConfiguration
            wifiConfiguration) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final int networkId;

        if (wifiConfiguration.networkId == -1) {
            // new wifi configuration, add it and then save it.
            networkId = wifiManager.addNetwork(wifiConfiguration);
        } else {
            // existing wifi configuration, update it and then save it.
            networkId = wifiManager.updateNetwork(wifiConfiguration);
        }

        if (networkId == -1) {
            return false;
        }

        // Added successfully, try to save it now.
        wifiManager.enableNetwork(networkId, /* disableOthers */ false);
        if (wifiManager.saveConfiguration()) {
            return true;
        } else {
            // Remove the added network that fail to save.
            wifiManager.removeNetwork(networkId);
            return false;
        }
    }
}
