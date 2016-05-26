//
//  HotlinePlugin.java
//
//  Copyright (c) 2014 Freshdesk. All rights reserved.


package com.freshdesk.hotline.android;


import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.widget.Toast;

import com.freshdesk.hotline.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;




public class HotlinePlugin extends CordovaPlugin {

    private boolean isInitialized = false;
    private HotlineConfig hotlineConfig;
    private Context cordovaContext;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 3458;
    private static final String LOG_TAG = "Hotline";
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaContext = cordova.getActivity();
    }
    
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(cordovaContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(cordova.getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "Play Services not available on this device"); 
            }
            return false;
        }
        return true;
    }

    public Bundle jsonToBundle(JSONObject jsonObject) throws JSONException {
        Bundle bundle = new Bundle();
        Iterator iterator = jsonObject.keys();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            String value = jsonObject.getString(key);
            bundle.putString(key,value);
        }
        return bundle;
     }

    @Override
    public boolean execute(String action, JSONArray args,final CallbackContext callbackContext) throws JSONException {
        
        
        if(action.equals("init")) {
            JSONObject initArgs = new JSONObject(args.getString(0));
            Log.i(LOG_TAG,"inside init call");
            String appId = initArgs.getString("appId");
            String appKey = initArgs.getString("appKey");
            
            HotlineConfig hotlineConfig = new HotlineConfig(appId,appKey);
            
            if(initArgs.getString("domain") != null) {
                hotlineConfig.setDomain(initArgs.getString("domain"));
            }
            hotlineConfig.setAgentAvatarEnabled(initArgs.getBoolean("agentAvatarEnabled"));
            hotlineConfig.setCameraCaptureEnabled(initArgs.getBoolean("cameraCaptureEnabled"));
            hotlineConfig.setVoiceMessagingEnabled(initArgs.getBoolean("voiceMessagingEnabled"));
            hotlineConfig.setPictureMessagingEnabled(initArgs.getBoolean("pictureMessagingEnabled")); 

            //option to show FAQ as grid vs list 
            
            
            Hotline.getInstance(cordovaContext).init(hotlineConfig);
            
            this.isInitialized = true;
            this.hotlineConfig = hotlineConfig;
            
            callbackContext.success();
            return true;
        }
        
        if(action.equals("showFAQs")) {
            Log.i(LOG_TAG,"Show FAQs has been called");
            Hotline.showFAQs(cordovaContext);
            
            callbackContext.success();
            return true;
        }

        if(action.equals("showConversations")) {
            Log.i(LOG_TAG,"show Conversations has been called");
            Hotline.showConversations(cordovaContext);
            
            callbackContext.success();
            return true;
        }

        if(action.equals("clearUserData")) {   
            Log.i(LOG_TAG,"inside clearUserData");
            Hotline.clearUserData(cordovaContext);
            
            callbackContext.success();
            return true;
        }

        if(action.equals("updateUser")) {
            JSONObject jsonArgs = new JSONObject(args.getString(0));
            Log.i(LOG_TAG,"inside updateUser");
            
            HotlineUser hotlineUser=Hotline.getInstance(cordovaContext).getUser();
           
            if(jsonArgs.getString("name") != null) {
                hotlineUser.setName(jsonArgs.getString("name"));    
            }
            if(jsonArgs.getString("email") != null) {
                hotlineUser.setName(jsonArgs.getString("email"));    
            }
            if(jsonArgs.getString("externalId") != null) {
                hotlineUser.setName(jsonArgs.getString("externalId"));    
            }
            if(jsonArgs.getString("countryCode") != null && jsonArgs.getString("phoneNumber") != null) {
                hotlineUser.setPhone(jsonArgs.getString("countryCode"),jsonArgs.getString("phoneNumber"));
            }

            Hotline.getInstance(cordovaContext).updateUser(hotlineUser);

            callbackContext.success();
            return true;
        }

        if(action.equals("updateUserProperties")) {
            JSONObject metadata = new JSONObject(args.getString(0));
            Log.i(LOG_TAG,"inside updateUserMeta");

            Map<String, String> userMeta = new HashMap<String, String>();
            Iterator<String> keys  = metadata.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                Log.i(LOG_TAG,"the key:"+key+"value:"+metadata.getString(key));
                userMeta.put(key, metadata.getString(key));
            }
            Hotline.getInstance(cordovaContext).updateUserProperties(userMeta);
            
            callbackContext.success();
            return true;
        }

        if(action.equals("unreadCount")) {
            Hotline.getInstance(cordovaContext).getUnreadCountAsync(new UnreadCountCallback() {
                @Override
                public void onResult(HotlineCallbackStatus hotlineCallbackStatus, int count)
                {
                    if (hotlineCallbackStatus == HotlineCallbackStatus.STATUS_SUCCESS) {
                        Log.i(LOG_TAG,"unreadcount is :"+count);
                        callbackContext.success(count);
                    }
                    else
                        callbackContext.error(hotlineCallbackStatus.toString());
                    }
            });
            return true;
        }

        if(action.equals("registerPushNotification")) {
            Log.i(LOG_TAG,"inside Android Notification Registeration");
            
            if(checkPlayServices()) {
                Intent intent = new Intent(cordovaContext, MyGcmRegistrationService.class);
                cordovaContext.startService(intent);
                return true;
            }
            return false;
        }

        if(action.equals("getVersionName")) {
            Log.i(LOG_TAG,"version number called");
            int versionNumber = Hotline.getInstance(cordovaContext).getSDKVersionCode();
            callbackContext.success(versionNumber);
            return true;  
        } 

        if(action.equals("isHotlinePushNotificationInternal")) {
            Log.i(LOG_TAG,"check if a particular push notificaiton is a hotline push notification or not");
            JSONObject jsonArgs = new JSONObject(args.getString(0));
            Bundle bundle = jsonToBundle(jsonArgs);
            if(Hotline.getInstance(cordovaContext).isHotlineNotification(bundle)) {
                callbackContext.success(1);
            }
            else {
                callbackContext.success(0);
            }
            return true;
        }

        if(action.equals("handlePushNotification")) {
            Log.i(LOG_TAG,"Handling Push Notification!");
            JSONObject jsonArgs = new JSONObject(args.getString(0));
            Bundle bundle = jsonToBundle(jsonArgs);
            Hotline.getInstance(cordovaContext).handleGcmMessage(bundle);
            return true;
        }

        if(action.equals("updateRegistrationToken")) {
            String token = args.getString(0);
            Log.i(LOG_TAG,"update GCM registration has been called");
            Hotline.getInstance(cordovaContext).updateGcmRegistrationToken(token);
            return true;
        }

        Log.i(LOG_TAG,"action does not have a function to match it:"+action);
        return true;
    }

    
    
}
