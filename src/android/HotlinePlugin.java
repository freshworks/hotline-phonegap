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
    private HotlineUser hotlineUser;
    private Map<String, String> userMeta;
    private Bundle bundle;
    
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
                Log.e(LOG_TAG, "Play Services not available on this device"); 
            }
            return false;
        }
        return true;
    }

    public Bundle jsonToBundle(JSONObject jsonObject) throws JSONException {
        Bundle bundle = new Bundle();
        if(jsonObject == null) {
            return bundle;
        }
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
        
        try {
                if(action.equals("init")) {
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide parameters for initializing Hotline");
                        return false;
                    }
                    JSONObject initArgs = new JSONObject(args.getString(0));
                    Log.d(LOG_TAG,"inside init call");
                    String appId = initArgs.getString("appId");
                    String appKey = initArgs.getString("appKey");

                    hotlineConfig = new HotlineConfig(appId,appKey);

                    if(initArgs.getString("domain") != null) {
                        hotlineConfig.setDomain(initArgs.getString("domain"));
                    }
                    hotlineConfig.setAgentAvatarEnabled(initArgs.getBoolean("agentAvatarEnabled"));
                    hotlineConfig.setCameraCaptureEnabled(initArgs.getBoolean("cameraCaptureEnabled"));
                    hotlineConfig.setVoiceMessagingEnabled(initArgs.getBoolean("voiceMessagingEnabled"));
                    hotlineConfig.setPictureMessagingEnabled(initArgs.getBoolean("pictureMessagingEnabled")); 

                    //option to show FAQ as grid vs list 
                    cordova.getThreadPool().execute( new Runnable() {
                       public void run() {
                            Hotline.getInstance(cordovaContext).init(hotlineConfig);       
                            callbackContext.success();
                       } 
                    });

                    this.isInitialized = true;
                    this.hotlineConfig = hotlineConfig;

                    return true;
                }

                if(action.equals("showFAQs")) {
                    Log.d(LOG_TAG,"Show FAQs has been called");
                    Hotline.showFAQs(cordovaContext);      
                    callbackContext.success();
                    return true;
                }

                if(action.equals("showConversations")) {
                    Log.d(LOG_TAG,"show Conversations has been called");
                    Hotline.showConversations(cordovaContext);      
                    callbackContext.success();
                    return true;
                }

                if(action.equals("clearUserData")) {   
                    Log.d(LOG_TAG,"inside clearUserData");
                    Hotline.clearUserData(cordovaContext);      
                    callbackContext.success();
                    return true;
                }

                if(action.equals("updateUser")) {
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide parameters to update a user");
                        return false;
                    }
                    JSONObject jsonArgs = new JSONObject(args.getString(0));
                    Log.d(LOG_TAG,"inside updateUser");

                    hotlineUser=Hotline.getInstance(cordovaContext).getUser();

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

                    cordova.getThreadPool().execute( new Runnable() {
                       public void run() {
                            Hotline.getInstance(cordovaContext).updateUser(hotlineUser);
                            callbackContext.success();
                       } 
                    });
                    return true;
                }

                if(action.equals("updateUserProperties")) {

                        if(args.length() == 0) {
                            Log.e(LOG_TAG,"Please provide user properties to update the user");
                            return false;
                        }

                        JSONObject metadata = new JSONObject(args.getString(0));
                        Log.d(LOG_TAG,"inside updateUserMeta");
                        userMeta = new HashMap<String, String>();
                        Iterator<String> keys  = metadata.keys();

                        while(keys.hasNext()) {
                                String key = keys.next();
                                Log.d(LOG_TAG,"the key:"+key+"value:"+metadata.getString(key));
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

                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide the sender Id to register for push notification");
                        return false;
                    }
                    String senderId = args.getString(0);
                    Log.i(LOG_TAG,"inside Android Notification Registeration with sender Id: " + senderId);
                    if(checkPlayServices()) {
                        Intent intent = new Intent(cordovaContext, HotlineGcmRegistrationService.class);
                        intent.putExtra("id", senderId);
                        cordovaContext.startService(intent);
                        return true;
                    }
                    return false;
                }

                if(action.equals("getVersionName")) {
                    Log.d(LOG_TAG,"version number called");
                    int versionNumber = Hotline.getInstance(cordovaContext).getSDKVersionCode();
                    callbackContext.success(versionNumber);
                    return true;  
                } 

                if(action.equals("isHotlinePushNotificationInternal")) {
                    Log.d(LOG_TAG,"check if a particular push notificaiton is a hotline push notification or not");
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide the notification payload to be verified ");
                        return false;
                    }
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
                    Log.d(LOG_TAG,"Handling Push Notification!");
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide parameters for initializing Hotline");
                        return false;
                    }
                    JSONObject jsonArgs = new JSONObject(args.getString(0));
                    bundle = jsonToBundle(jsonArgs);
                    cordova.getThreadPool().execute( new Runnable() {
                       public void run() {
                            Hotline.getInstance(cordovaContext).handleGcmMessage(bundle);
                            callbackContext.success();
                       } 
                    });

                    return true;
                }

                if(action.equals("updateRegistrationToken")) {
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide the token to register");
                        return false;
                    }
                    String token = args.getString(0);
                    Log.i(LOG_TAG,"update GCM registration has been called");
                    Hotline.getInstance(cordovaContext).updateGcmRegistrationToken(token);
                    return true;
                }

                Log.d(LOG_TAG,"action does not have a function to match it:"+action);
                
            } catch (Exception e) {
                Log.e(LOG_TAG,"exception while perfroming action:"+action,e);
                callbackContext.error("exception while performing action"+action);
            }
        return true;
    }

    
    
}
