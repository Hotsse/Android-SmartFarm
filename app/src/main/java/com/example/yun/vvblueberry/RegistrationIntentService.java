package com.example.yun.vvblueberry;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by Yun on 2017-11-18.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "MyInstanceIDService";
    private static final String GCM_NOTICE = "notice";
    private static final String SENDID = "397666377864";

    public RegistrationIntentService() {
        super(TAG);
        Log.d(TAG, "RegistrationIntentService()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);

            String token = instanceID.getToken(SENDID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + GCM_NOTICE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
