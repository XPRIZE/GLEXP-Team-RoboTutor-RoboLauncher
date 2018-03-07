package cmu.xprize.rthomescreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;

import static android.os.UserManager.DISALLOW_ADD_USER;
import static android.os.UserManager.DISALLOW_FACTORY_RESET;
import static android.os.UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA;
import static android.os.UserManager.DISALLOW_SAFE_BOOT;

/**
 * RoboSuiteLauncher
 * <p>
 * Created by kevindeland on 3/2/18.
 */

public class RemoveAdmin extends Activity {

    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;
    private String mPackageName;


    @Override
    public void onCreate(Bundle savedInsanceState) {
        super.onCreate(savedInsanceState);

        // stuff needed for kiosk mode
        // TODO move this to one class
        mAdminComponentName = AdminReceiver.getComponentName(this);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        mPackageName = getPackageName();

        // REVERSE Kiosk Policies
        setDefaultKioskPolicies(false);

        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        String packageName = getPackageName();

        manager.clearDeviceOwnerApp(packageName);

        finish();

    }

    /**
     *
     *
     * @param active
     * @throws SecurityException if this package doesn't have Device Owner privileges
     */
    private void setDefaultKioskPolicies (boolean active) throws SecurityException {

        // set user restrictions
        setUserRestriction(DISALLOW_SAFE_BOOT, active);
        setUserRestriction(DISALLOW_FACTORY_RESET, active);
        setUserRestriction(DISALLOW_ADD_USER, active);
        setUserRestriction(DISALLOW_MOUNT_PHYSICAL_MEDIA, active);

        // XXX disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);


        // XXX default home screen
        if (active) {
            // create an intent filter
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
            intentFilter.addCategory(Intent.CATEGORY_HOME);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

            // set as default home screen
            mDevicePolicyManager.addPersistentPreferredActivity(mAdminComponentName, intentFilter,
                    new ComponentName(mPackageName, HomeActivity.class.getName()));
        } else {
            // otherwise clear deafult home screen
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(mAdminComponentName, mPackageName);
        }

        // XXX setLockTaskPackages
        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName, new String[]{});


    }

    /**
     * for setting user restrictions
     *
     * @param restriction
     * @param disallow
     */
    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName, restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName, restriction);
        }
    }

}
