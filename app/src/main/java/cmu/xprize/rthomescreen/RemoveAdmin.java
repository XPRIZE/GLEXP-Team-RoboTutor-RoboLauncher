package cmu.xprize.rthomescreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Bundle;

/**
 * RoboSuiteLauncher
 * <p>
 * Created by kevindeland on 3/2/18.
 */

public class RemoveAdmin extends Activity {

    @Override
    public void onCreate(Bundle savedInsanceState) {
        super.onCreate(savedInsanceState);

        DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        String packageName = getPackageName();

        manager.clearDeviceOwnerApp(packageName);

        finish();

    }
}
