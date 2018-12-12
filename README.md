[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# **RoboTutor Home Screen**


This repository contains the Home Screen that may be used to provide Android Home Screen support if RoboTutor is run in a Kiosk configuration.

This version was uploaded to XPRIZE on 11/20/2018. For subsequent changes, see [https://github.com/RoboTutorLLC/RoboSuiteLauncher](https://github.com/RoboTutorLLC/RoboSuiteLauncher).

## **Setup and Configuration:**

[Install Android Studio](http://developer.android.com/sdk/index.html)<br>

[Install GitHub Desktop](https://desktop.github.com/)<br>


## **Building Home_Screen:**

1. Clone Home_Screen to your computer using Git/GitHub

2. **Import** the Home_Screen project into Android Studio.

3. You may need to install different versions of the Android Studio build tools and SDKs.

4. Add a file named "keystore.properties" to your root project directory, and give it the following contents. The values should be based on the values you used to generate the keystore.
```
storePassword=<your_store_password>
keyPassword=<your_key_password>
keyAlias=<your_key_alias>
storeFile=<path_to_location_of_keystore>
```

5. Use Android Studio or gradlew to generate a signed APK with the flavor *xprize*. This will generate the file *RoboLaunch.xprize.1.0.0.apk*. This file should be transferred to the *apk* folder in your local SystemBuild directory.
