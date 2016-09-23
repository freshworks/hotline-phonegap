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
import java.util.ArrayList;
import java.util.List;





public class HotlinePlugin extends CordovaPlugin {

    private boolean isInitialized = false;
    private HotlineConfig hotlineConfig;
    private FaqOptions faqOptions;
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

                    if(initArgs.has("domain")) {
                        hotlineConfig.setDomain(initArgs.getString("domain"));
                    }
                    if(initArgs.has("agentAvatarEnabled")) {
                        hotlineConfig.setAgentAvatarEnabled(initArgs.getBoolean("agentAvatarEnabled"));
                    }
                    if(initArgs.has("cameraCaptureEnabled")) {
                        hotlineConfig.setCameraCaptureEnabled(initArgs.getBoolean("cameraCaptureEnabled"));
                    }
                    if(initArgs.has("voiceMessagingEnabled")) {
                        hotlineConfig.setVoiceMessagingEnabled(initArgs.getBoolean("voiceMessagingEnabled"));
                    }
                    if(initArgs.has("pictureMessagingEnabled")) {
                        hotlineConfig.setPictureMessagingEnabled(initArgs.getBoolean("pictureMessagingEnabled"));
                    }
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
                    if(args.length() == 0) {
                        Hotline.showFAQs(cordovaContext);
                        return true;
                    }
                    JSONObject faqArgs = new JSONObject(args.getString(0));
                    faqOptions = new FaqOptions();
                    if(faqArgs.has("showFaqCategoriesAsGrid")) {
                        faqOptions.showFaqCategoriesAsGrid(faqArgs.getBoolean("showFaqCategoriesAsGrid"));
                    }
                    if(faqArgs.has("showContactUsOnAppBar")) {
                        faqOptions.showContactUsOnAppBar(faqArgs.getBoolean("showContactUsOnAppBar"));
                    }
                    if(faqArgs.has("showContactUsOnFaqScreens")) {
                        faqOptions.showContactUsOnFaqScreens(faqArgs.getBoolean("showContactUsOnFaqScreens"));
                    }
                    if(faqArgs.has("showContactUsOnFaqNotHelpful")) {
                        faqOptions.showContactUsOnFaqNotHelpful(faqArgs.getBoolean("showContactUsOnFaqNotHelpful"));
                    }

                    List<String> tagsList = new ArrayList<String>();
                    if(faqArgs.optJSONArray("tags") != null) {
                        JSONArray tags = faqArgs.getJSONArray("tags");
                        for (int i = 0; i < tags.length(); i++) {
                            tagsList.add(tags.getString(i));
                        }
                        faqOptions.filterByTags(tagsList, faqArgs.getString("filteredViewTitle"));
                    }
                    Hotline.showFAQs(cordovaContext, faqOptions);
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

                    hotlineUser=Hotline.getInstance(cordovaContext).getUser();

                    if(jsonArgs.has("name")) {
                        hotlineUser.setName(jsonArgs.getString("name"));
                    }
                    if(jsonArgs.has("email")) {
                        hotlineUser.setEmail(jsonArgs.getString("email"));
                    }
                    if(jsonArgs.has("externalId")) {
                        hotlineUser.setExternalId(jsonArgs.getString("externalId"));
                    }
                    if(jsonArgs.has("countryCode") && jsonArgs.has("phoneNumber")) {
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
                        userMeta = new HashMap<String, String>();
                        Iterator<String> keys  = metadata.keys();

                        while(keys.hasNext()) {
                                String key = keys.next();
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
