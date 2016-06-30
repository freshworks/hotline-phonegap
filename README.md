# Hotline Phonegap plugin

This plugin integrates Hotline's SDK into a Phonegap/Cordova project. 

For platform specific details please refer to the [Documentation](https://hotline.freshdesk.com/support/solutions/9000100231)

Supported platforms :
* Android
* iOS

**Note : This is an early version and so expect changes to the API**

### Integrating the Plugin : 

1. Add required platforms to your PhoneGap project 
```
cordova platform add android
cordova platform add ios
```

2. Add the hotline plugin to your project.
```
cordova plugin add https://github.com/freshdesk/hotline-phonegap.git
```


### Initializing the plugin

_Hotline.init_ needs to be called from _ondeviceready_ event listener to make sure the SDK is initialized before use.

```javascript
document.addEventListener("deviceready", function(){
  // Initialize Hotline with your AppId & AppKey from your portal https://web.hotline.io/settings/apisdk
  window.Hotline.init({
    appId       : "<Your App Id>",
    appKey      : "<Your App Key>"
  });
});
```
 
 If you have are a Konotor user add a key called "domain" and "app.konotor.com". so your init code would look like this:
 ```javascript
document.addEventListener("deviceready", function(){
  //Initialize Hotline
  window.Hotline.init({
    appId       : "<Your App Id>",
    appKey      : "<Your App Key>",
    domain      : "app.konotor.com"
  });
});
 ```
 
 The following optional boolean parameters can be passed to the init Object 
 -  agentAvatarEnabled  
 -  cameraCaptureEnabled 
 -  voiceMessagingEnabled 
 -  pictureMessagingEnabled
 -  showNotificationBanner _(ios only)_
 
 Here is a sample init code with the optional parameters
 
 ```javascript
 window.Hotline.init({
            appId       : "<Your App Id>",
            appKey      : "<Your App Key>",
            agentAvatarEnabled      : true,
            cameraCaptureEnabled    : false,
            voiceMessagingEnabled   : true,
            pictureMessagingEnabled : true
        });
 ```
 The init function is also a callback function and can be implemented like so:
 
 ```javascript
 window.Hotline.init({
            appId       : "<Your App Id>",
            appKey      : "<Your App Key>",
            agentAvatarEnabled      : true,
            cameraCaptureEnabled    : false,
            voiceMessagingEnabled   : true,
            pictureMessagingEnabled : true
        }, function(success){
            console.log("This is called form the init callback");
        });
 ```
 
 Once initialized you can call Hotline APIs using the window.Hotline object. 
 
 ```javascript 
//After initializing Hotline 
showSupportChat = function() { 
    window.Hotline.showConversations();
};
document.getElementById("launch_conversations").onclick = showSupportChat;


//in index.html
//<button id="launch_conversations"> Inbox </button>
 ```

### Hotline APIs
* Hotline.showFAQs()
    - Launch FAQ / Self Help
* Hotline.showConversations()
    - Launch Channels / Conversations. 
* Hotline.unreadCount(callback)
    - Fetch count of unread messages from agents. 
* Hotline.updateUser(userInfo)
    - Update user info. Accepts a JSON with the following format  
```javascript
{ 
   "name" : "John Doe", 
   "email" : "johndoe@dead.man", 
   "externalId" : "some unique Identifier from your system", 
   "countryCode" : "+91", 
   "phoneNumber" : "1234234123"
}
```
* Hotline.updateUserProperties(userPropertiesJson)
    - Update custom user properties using a Json containing key, value pairs. A sample json follows
```javascript
{
   "user_type" : "Paid",
   "plan" : "Gold"
}
```
* Hotline.clearUserData()
    - Clear user data when users logs off your app. 

You can pass in an optional callback function to an API as the first parameter, which gets called when native API is completed. 
Eg. 
```javascript
window.Hotline.unreadCount(function(success,val) {
    //success indicates whether the API call was successful
    //val contains the no of unread messages
});
```

#### Push Notifications 

There are two options to integrate Push Notifications. 

##### You have push notification setup in your app. 

When you receive a deviceToken from GCM or APNS , you need to update the deviceToken in hotline as follows

``` 
    // Example illustrates usage for phonegap-push-plugin
    push.on('registration',function(data) {
        window.Hotline.GCMRegisteration(data.registrationId);
     });
```

Whenever a push notification is received. You will need to check if the notification originated from Hotline and have Hotline SDK handle it. 

```
    // Example illustrates usage for phonegap-push-plugin
    push.on('notification',function(data) {
        window.Hotline.isHotlineNotificationFunction(function(success, isHotlineNotif) {
          if( success && isHotlineNotif ) { 
            window.Hotline.handlePush(data.additionalData);
          }
}, data.additionalData);
    });
```

##### You currently do not use push notifications

All you need to do is ask hotline to do the push notification registration and we will take care of everything from that point . 

```
window.Hotline.registerPushNotification('ANDROID_SENDER_ID'); // takes care of registration and handling of push notification on iOS and Android. 
```

#### Caveats

##### Android : 
* Needs appcompat-v7 : 21+
* Needs support-v4 : 21+
* MinSdkVersion must be atleast 10 (in config.xml)

##### iOS
* Needs iOS 7 and above

