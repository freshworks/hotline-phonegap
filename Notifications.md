#HOTLINE PUSH NOTIFICATIONS SETUP GUIDE
This is a guide to setup push notifications with the Hotline Phonegap plugin.

The plugin supports GCM for push notifications in Android and APNS for iOS.

###Server Side 

After you setup your hotline account, go to https://web.hotline.io/settings/apisdk in this page,
add the server key for Android and the push certificate for iOS.

###Plugin Side

We support push notifications through a forked version of the phonegap push plugin 
found [here](https://github.com/freshdesk/phonegap-plugin-push).

It can be installed by the following command : 
```shell
cordova plugin add https://github.com/freshdesk/phonegap-plugin-push.git
```
If you are setting it up for Android as well, add the Sender Id as well, so the command would look like:
```shell
cordova plugin add https://github.com/freshdesk/phonegap-plugin-push.git --variable SENDER_ID=20738924380
```

This plugin needs to be installed for Hotline's push notifiations to work.

When you receive a deviceToken from GCM or APNS , you need to update the deviceToken in hotline as follows

```javascript
    push.on('registration',function(data) {
        window.Hotline.updateRegistrationToken(data.registrationId);
     });
```

Whenever a push notification is received. You will need to check if the notification originated from Hotline 
and have Hotline SDK handle it.

```javascript
// Example illustrates usage for phonegap-push-plugin
push.on('notification',function(data) {
  window.Hotline.isHotlinePushNotification(data.additionalData, function(success, isHotlineNotif) {
    if( success && isHotlineNotif ) {
      window.Hotline.handlePushNotification(data.additionalData);
    }
 });
});
```

Here is a snippet of both the methods together in a sample.

```javascript
function initializePush() {
    var push = PushNotification.init({
        "android":{
            "senderID":"XXXXXXXXXX"
        },
        "ios": {
            "alert": "true",
            "badge": "true",
            "sound": "true"
        },
        "windows": {}
    });

     push.on('registration',function(data) {
         console.log("Inside notification registration :"+data.registrationId);
        window.Hotline.updateRegistrationToken(data.registrationId);
     });

     push.on('notification',function(data) {
        console.log("Inside notification received function data"+data.toString());
         window.Hotline.isHotlinePushNotification(data.additionalData, function(success, isHotlineNotif){
             if(success && isHotlineNotif) {
                 console.log("This is a hotline message"+data.message);
                 console.log("This is a hotline additional data"+data.additionalData);
                 window.Hotline.handlePushNotification(data.additionalData);
             }
         });
        
    });
}
```

Android notification properties can be changed with the updateAndroidNotificationProperties API. The properties that can be updated are.

-  "notificationSoundEnabled" : Notifiction sound enabled or not.
-  "smallIcon" : Setting a small notification icon (move the image to drawbles folder and pass the name of the jpeg file as parameter).
-  "largeIcon" : setting a large notification icon.
-  "deepLinkTargetOnNotificationClick" : Toggles if the deeplink target of a notification should open on click.
-  "notificationPriority" : set the priority of notification through hotline.
-  "launchActivityOnFinish" : Activity to launch on up navigation from the messages screen launched from notification. The messages screen will have no activity to navigate up to in the backstack when its launched from notification. Specify the activity class name to be launched.


The API is called like:
    
```javascript
window.Hotline.updateAndroidNotificationProperties({
                "smallIcon" : "image",
                "largeIcon" : "image",
                "notificationPriority" : window.Hotline.NotificationPriority.PRIORITY_MAX,
                "notificationSoundEnabled" : false,
                "deepLinkTargetOnNotificationClick" : true
                "launchActivityOnFinish" : "MainActivity.class.getName()"
            });
```
List of hotline Priorities:

-  Hotline.NotificationPriority.PRIORITY_DEFAULT
-  Hotline.NotificationPriority.PRIORITY_HIGH
-  Hotline.NotificationPriority.PRIORITY_LOW
-  Hotline.NotificationPriority.PRIORITY_MAX
-  Hotline.NotificationPriority.PRIORITY_MIN
