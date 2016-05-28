package com.freshdesk.hotline.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.freshdesk.hotline.Hotline;
import com.google.android.gms.gcm.GcmListenerService;

public class HotlineGcmListenerService extends GcmListenerService {

    @Override
	public void onMessageReceived(String from, Bundle data) {

        Log.i("test","notification received");
		Hotline instance = Hotline.getInstance(this);
		Intent intent = new Intent();
		intent.putExtras(data);

		if (instance.isHotlineNotification(intent)) {
			instance.handleGcmMessage(intent);
			return;
		} else {
			//process your app's notification messages
      //
      //TODO : Need to check if app's notification is also received
		}
	}
}
