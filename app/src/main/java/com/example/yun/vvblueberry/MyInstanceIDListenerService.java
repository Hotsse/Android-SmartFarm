package com.example.yun.vvblueberry;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Yun on 2017-11-18.
 */

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private  static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh(){
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
