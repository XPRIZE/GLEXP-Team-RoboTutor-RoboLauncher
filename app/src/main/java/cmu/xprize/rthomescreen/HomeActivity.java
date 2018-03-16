package cmu.xprize.rthomescreen;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import cmu.xprize.rthomescreen.startup.CMasterContainer;
import cmu.xprize.rthomescreen.startup.CStartView;
import cmu.xprize.util.IRoboTutor;

import static android.os.UserManager.DISALLOW_ADD_USER;
import static android.os.UserManager.DISALLOW_ADJUST_VOLUME;
import static android.os.UserManager.DISALLOW_FACTORY_RESET;
import static android.os.UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA;
import static android.os.UserManager.DISALLOW_SAFE_BOOT;

public class HomeActivity extends Activity implements IRoboTutor{

    static public CMasterContainer masterContainer;

    private CStartView startView;

    // kiosk info
    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;
    private PackageManager mPackageManager;
    private String mPackageName;
    private ArrayList<String> mKioskPackages;
    private String flPackage = "com.example.iris.login1";
    private String rtPackage = "cmu.xprize.robotutor";
    private String ftpPackage = "cmu.xprize.service_ftp";

    private static final String TAG = "RTHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // stuff needed for kiosk mode
        // TODO move this to one class
        mAdminComponentName = AdminReceiver.getComponentName(this);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPackageManager = getPackageManager();
        mPackageName = getPackageName();

        // mKisokPackages
        mKioskPackages = new ArrayList<>();
        mKioskPackages.add(flPackage);
        mKioskPackages.add(rtPackage);
        mKioskPackages.add(mPackageName);

        try {
            setDefaultKioskPolicies(true);
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "WARNING: This app is not the Device Owner. Kiosk mode not enabled.", Toast.LENGTH_LONG).show();
        }

        //setAppPermissions();


        // Get the primary container for tutors
        setContentView(R.layout.activity_home);
        masterContainer = (CMasterContainer)findViewById(R.id.master_container);

        setFullScreen();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Create the start dialog
        //
        startView = (CStartView)inflater.inflate(R.layout.start_layout, null );
        startView.setCallback(this);

        //
        masterContainer.addAndShow(startView);
        startView.startTapTutor();
        setFullScreen();

        try {
            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0);
        }
        catch(Exception e) {

        }
        launchFtpService();
    }

    /**
     * launch RoboTransfer, a service to transfer log files
     */
    private void launchFtpService() {
        // wait... why include this as library??? why not just make separate app?

        Intent ftpIntent = new Intent();
        ftpIntent.setComponent(
                new ComponentName("cmu.xprize.service_ftp", "cmu.xprize.service_ftp.RoboTransferReceiver")
        );

        ftpIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);


        Log.w("LAUNCH_DEBUG", "Launching RoboTransfer... " + ftpIntent.getPackage());

        ftpIntent.putExtra("FTP_ADDRESS",   getResources().getString(R.string.ftp_address));
        ftpIntent.putExtra("FTP_USER",      getResources().getString(R.string.ftp_user));
        ftpIntent.putExtra("FTP_PW",        getResources().getString(R.string.ftp_pw));
        ftpIntent.putExtra("FTP_PORT",      getResources().getInteger(R.integer.ftp_port));

        ftpIntent.putExtra("FTP_READ_DIRS", getResources().getStringArray(R.array.ftp_read_dirs));
        ftpIntent.putExtra("FTP_WRITE_DIRS",getResources().getStringArray(R.array.ftp_write_dirs));


        sendBroadcast(ftpIntent);
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
                                Log.w("DEBUG_PERMISSIONS", pkg + " - " + pInfo.name);

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
        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                active ? mKioskPackages.toArray(new String[]{}) : new String[]{});


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

    /**
     * Backdoor to allow exiting kiosk mode
     */
    public void onBackdoorPressed() {
        stopLockTask();
        setDefaultKioskPolicies(false);

        mPackageManager.setComponentEnabledSetting(
                new ComponentName(getPackageName(), getClass().getName()),
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP);

        //finish();
    }


    private void setFullScreen() {


        ((View) masterContainer).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    @Override
    public void onStartTutor() {

        Log.w(TAG, "Starting FaceLogin");
        Intent flIntent = mPackageManager.getLaunchIntentForPackage(flPackage);

        if(flIntent != null) {
            startActivity(flIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Please install FaceLogin", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


        // start lock task mode if it's not already active
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // ActivityManager.getLockTaskModeState api is not available in pre-M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!am.isInLockTaskMode()) {
                startLockTask();
            }
        } else {
            if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask();
            }
        }


        setFullScreen();
    }
}
