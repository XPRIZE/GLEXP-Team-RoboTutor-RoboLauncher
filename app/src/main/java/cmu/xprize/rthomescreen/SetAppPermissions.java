package cmu.xprize.rthomescreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
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

public class SetAppPermissions extends Activity {

    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;
    private String mPackageName;

    private PackageManager mPackageManager;


    @Override
    public void onCreate(Bundle savedInsanceState) {
        super.onCreate(savedInsanceState);

        // stuff needed for kiosk mode
        // TODO move this to one class
        mAdminComponentName = AdminReceiver.getComponentName(this);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPackageManager = getPackageManager();
        mPackageName = getPackageName();


        String pkg = "cmu.xprize.service_ftp";

        PackageInfo info = null;
        try {
            info = mPackageManager.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if ( info != null && info.requestedPermissions != null) {
            for (String requestedPerm : info.requestedPermissions) {
                try {
                    PermissionInfo pInfo = mPackageManager.getPermissionInfo(requestedPerm, 0);

                    if (pInfo != null) {
                        if ((pInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS) {
                            Log.w("DEBUG_PERMISSIONS", pkg + " - " + pInfo.name);

                            mDevicePolicyManager.setPermissionGrantState(mAdminComponentName, pkg, pInfo.name, DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
                        }
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        finish();

    }


}
