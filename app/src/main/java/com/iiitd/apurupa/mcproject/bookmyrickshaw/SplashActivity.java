package com.iiitd.apurupa.mcproject.bookmyrickshaw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {
    ImageView app_icon;
    private static int SPLASH_TIME_OUT = 4000;
    public static final String mypreference = "mypref";
    private Intent serviceintent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        app_icon = (ImageView) findViewById(R.id.imageView);
//Animation

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        app_icon.setAnimation(anim);


        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (!runtime_permissions())
                    enable_service();


                new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        SharedPreferences preferences = getSharedPreferences(mypreference,
                                Context.MODE_PRIVATE);
                        if (preferences.getBoolean("isloggedin", true)) {

                            Intent i = new Intent(getApplicationContext(), TrialMapsActivity.class);
                            startActivity(i);
                        } else {
                            // close this activity
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                        // finish();
                    }


                }, SPLASH_TIME_OUT);
            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void enable_service() {

        serviceintent=new Intent(this, TimeService.class);
        startService(serviceintent);

    }
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_service();
            }else {
                runtime_permissions();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("In Splash Activity","Service");
        stopService(serviceintent);
    }
}





























