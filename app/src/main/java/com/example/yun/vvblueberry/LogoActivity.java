package com.example.yun.vvblueberry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Yun on 2017-09-17.
 */

public class LogoActivity extends Activity {

    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        imgLogo = (ImageView)findViewById(R.id.imgLogo);

        Animation translate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
        imgLogo.startAnimation(translate);

        translate.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }

            @Override
            public void onAnimationStart(Animation arg0) {
                //Functionality here
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                //Functionality here
            }


        });

    }



}
