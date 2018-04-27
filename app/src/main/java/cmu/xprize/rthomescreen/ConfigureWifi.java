package cmu.xprize.rthomescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * RoboSuiteLauncher
 * <p>
 * Created by kevindeland on 3/16/18.
 */

public class ConfigureWifi extends BroadcastReceiver {

    private static final String DEBUG_TAG = "DEBUG_LAUNCH:WIFI";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.wtf(DEBUG_TAG, "Received Broadcast");

        WifiConfiguration config = new WifiConfiguration();

        // connect to WiFi based on build and config
        int ssidConfig, securityConfig, passwordConfig;
        switch(BuildConfig.WIFI_CONFIG) {

            case "LOCAL":

                ssidConfig = R.string.wifi_ssid_local;
                securityConfig = R.string.wifi_security_local;
                passwordConfig = R.string.wifi_pw_local;
                break;

            case "VMC":
                ssidConfig = R.string.wifi_ssid_vmc;
                securityConfig = R.string.wifi_security_vmc;
                passwordConfig = R.string.wifi_pw_vmc;
                break;

            case "XPRIZE":
            default:
                ssidConfig = R.string.wifi_ssid_xprize;
                securityConfig = R.string.wifi_security_xprize;
                passwordConfig = R.string.wifi_pw_xprize;

        }

        config.SSID = context.getResources().getString(ssidConfig);

        // update security
        String security = context.getResources().getString(securityConfig);

        switch(security) {
            case "WPA":
            case "WPA2":
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

                Log.w(DEBUG_TAG, "WPA detected!");

                String password = context.getResources().getString(passwordConfig);
                Log.w(DEBUG_TAG, "Setting password=" + password);

                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                Log.w(DEBUG_TAG, "Setting a bunch of config settings...");
                Log.w(DEBUG_TAG, config.toString());

                config.preSharedKey = "\"".concat(password).concat("\"");
                /*if (password.matches("[0-9A-Fa-f]{64}")) {
                    config.preSharedKey = password;
                } else {
                    config.preSharedKey = getQuotedString(password);
                }*/

                break;

            case "":
            default:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
        }

        Log.w(DEBUG_TAG, "SSID = " + config.SSID + ", Password = " + config.preSharedKey);

        boolean success = saveWifiConfiguration(context, config);
        Log.wtf(DEBUG_TAG, "Changed wifi config " + success);

    }


    /**
     * Why did I do this???
     *
     * @param string
     * @return
     */
    private String getQuotedString(String string) {
        return "\"" + string + "\"";
    }

    public static boolean saveWifiConfiguration(Context context, WifiConfiguration
            wifiConfiguration) {

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final int networkId;

        Log.w(DEBUG_TAG, "saving WifiConfiguration " + wifiConfiguration.toString());
        Log.w(DEBUG_TAG, "saving WifiConfiguration " + wifiConfiguration.SSID + " " + wifiConfiguration.preSharedKey);


        findExistingNetwork(wifiManager, wifiConfiguration.SSID);


        if (wifiConfiguration.networkId == -1) {
            // new wifi configuration, add it and then save it.
            Log.w(DEBUG_TAG, "adding Network " + wifiConfiguration.SSID + " " + wifiConfiguration.preSharedKey);
            networkId = wifiManager.addNetwork(wifiConfiguration);
            Log.w(DEBUG_TAG, "networkId = " + networkId);
        } else {
            // existing wifi configuration, update it and then save it.
            Log.w(DEBUG_TAG, "updating Network " + wifiConfiguration.SSID + " " + wifiConfiguration.preSharedKey);
            networkId = wifiManager.updateNetwork(wifiConfiguration);
        }

        if (networkId == -1) {
            Log.w(DEBUG_TAG, "networkId == -1, returning false");
            return false;
        }

        // Added successfully, try to save it now.
        Log.w(DEBUG_TAG, "enabling network");
        wifiManager.enableNetwork(networkId, /* disableOthers */ false);

        Log.w(DEBUG_TAG, "saving configuration");
        if (wifiManager.saveConfiguration()) {
            return true;
        } else {
            // Remove the added network that fail to save.
            wifiManager.removeNetwork(networkId);
            return false;
        }
    }


    /**
     * Looks for an existing WiFi configuration
     *
     * @param wifiManager
     * @param ssid
     * @return
     */
    private static Integer findExistingNetwork(WifiManager wifiManager, String ssid) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        Log.w(DEBUG_TAG, String.format("Found %d existing networks", existingConfigs.size()));
        for (WifiConfiguration existingConfig : existingConfigs) {
            Log.w(DEBUG_TAG, "Found ID: " + ssid);
            if (existingConfig.SSID.equals(ssid)) {
                return existingConfig.networkId;
            }
        }

        return null;
    }
}
