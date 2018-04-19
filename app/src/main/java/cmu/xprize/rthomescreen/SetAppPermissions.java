package cmu.xprize.rthomescreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.util.Log;

import static android.os.UserManager.DISALLOW_ADD_USER;
import static android.os.UserManager.DISALLOW_FACTORY_RESET;
import static android.os.UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA;
import static android.os.UserManager.DISALLOW_SAFE_BOOT;

/**
 * RoboSuiteLauncher
 * <p>A class for testing the setting of app permissions</p>
 * Created by kevindeland on 3/2/18.
 */

public class SetAppPermissions extends BroadcastReceiver {

    private String flPackage = "com.example.iris.login1";
    private String rtPackage = "cmu.xprize.robotutor";
    private String ftpPackage = "cmu.xprize.service_ftp";

    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;
    private String mPackageName;

    private PackageManager mPackageManager;

    String DEBUG_TAG = "DEBUG_LAUNCH:PERMISSIONS";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(DEBUG_TAG, "Received request to SetAppPermissions");

        // stuff needed for kiosk mode
        // TODO move this to one class
        mAdminComponentName = AdminReceiver.getComponentName(context);
        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPackageManager = context.getPackageManager();
        mPackageName = context.getPackageName();


        setAppPermissions();

    }

    /**
     * Enables all dangerous permissions for each of our APKs
     */
    private void setAppPermissions() {

        String[] pkgs = {rtPackage, flPackage, ftpPackage};

        for (String pkg : pkgs) {
            PackageInfo info = null;
            try {
                // get permissions
                info = mPackageManager.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS);
                Log.i(DEBUG_TAG, pkg + ": " + info.requestedPermissions.length);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (info != null && info.requestedPermissions != null) {
                for (String requestedPerm : info.requestedPermissions) {
                    try {
                        PermissionInfo pInfo = mPackageManager.getPermissionInfo(requestedPerm, 0);

                        if (pInfo != null) {
                            // only do for permissions that require user permission
                            if ((pInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS) {
                                Log.w(DEBUG_TAG, pkg + " - " + pInfo.name);

                                // set to GRANTED
                                mDevicePolicyManager.setPermissionGrantState(mAdminComponentName, pkg, pInfo.name, DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
                            }
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
