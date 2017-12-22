package cmu.xprize.rthomescreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cmu.xprize.rthomescreen.startup.CMasterContainer;
import cmu.xprize.rthomescreen.startup.CStartView;
import cmu.xprize.util.IRoboTutor;

public class HomeActivity extends Activity implements IRoboTutor{

    static public CMasterContainer masterContainer;
    static public Activity         activityLocal;

    private CStartView startView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLocal = this;

        // Get the primary container for tutors
        setContentView(R.layout.activity_home);
        masterContainer = (CMasterContainer)findViewById(R.id.master_container);

        setFullScreen();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Create the start dialog
        // TODO: This is a temporary log update mechanism - see below
        //
        startView = (CStartView)inflater.inflate(R.layout.start_layout, null );
        startView.setCallback(this);

        // TODO: This is a temporary log update mechanism - see below
        //
        masterContainer.addAndShow(startView);
        startView.startTapTutor();
        setFullScreen();

        try {
            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0);
        }
        catch(Exception e) {

        }

        startLockTask();
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

        String intent = "com.example.iris.login1";
        String intentData = "";
        String dataSourceJson = "";
        String features = "";

        Intent extIntent = new Intent();
        String extPackage;

        Log.d("Start", "Tutor");

        extPackage = intent.substring(0, intent.lastIndexOf('.'));

        extIntent.setClassName(extPackage, intent);
        extIntent.putExtra("intentdata", intentData);
        extIntent.putExtra("features", features);

        try {
            activityLocal.startActivity(extIntent);
        }
        catch(Exception e) {
            Log.e("Home", "Launch Error: " + e + " : " + intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        setFullScreen();
    }
}
