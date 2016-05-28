package com.freshdesk.hotline.android;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freshdesk.hotline.Hotline;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class HotlineGcmRegistrationService extends IntentService {

	private static final String TAG = "MyGcmRegService";

	public HotlineGcmRegistrationService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			// Initially this call goes out to the network to retrieve the token, subsequent calls are local.
            String senderId = intent.getStringExtra("id");
            Log.i("test","this is handle intent");
			InstanceID instanceID = InstanceID.getInstance(this);
            if(senderId != null && !senderId.isEmpty()) {
                String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                sendRegistrationToServer(this, token);
            }
			else {
                Log.i(TAG,"Please provide a valid GCM sender Id");
            }			
		} catch (Exception e) {
			Log.d(TAG, "Failed to complete token refresh", e);
			// If an exception happens while fetching the new token or updating our registration data
			// on a third-party server, make sure to attempt the update at a later time.
		}
	}

	private void sendRegistrationToServer(Context context, String token) {
		Hotline.getInstance(context).updateGcmRegistrationToken(token);
	}
}
