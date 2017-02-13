# Hotline Phonegap plugin
[![Twitter](https://img.shields.io/badge/twitter-@GetHotline-orange.svg?style=flat)](https://twitter.com/GetHotline)

This plugin integrates Hotline's SDK into a Phonegap/Cordova project.

You can reach us anytime at contactus@hotline.io if you run into trouble.

AppId and AppKey
You'll need these keys while integrating Hotline SDK with your app. you can get the same from the [Settings -> API&SDK](https://web.hotline.io/settings/apisdk) page. Do not share them with anyone.
If you do not have an account, you can get started for free at [hotline.io](https://hotline.io/) 

[Where to find AppId and AppKey](https://hotline.freshdesk.com/solution/articles/9000041894-where-to-find-app-id-and-app-key-)

For platform specific details please refer to the [Documentation](http://support.hotline.io/support/solutions/160796)

Supported platforms :
* Android
* iOS

**Note : This is an early version and so expect changes to the API**

### Integrating the Plugin :

1. Add required platforms to your PhoneGap project
```shell
cordova platform add android
cordova platform add ios
```

2. Add the hotline plugin to your project.

You can add the plugin from command line like:
```shell
cordova plugin add hotline
```
You can also add it to your config.xml like 
```javascript
<plugin name="hotline" source="npm"/>
```

### Initializing the plugin

_Hotline.init_  needs to be called from _ondeviceready_  event listener to make sure the SDK is initialized before use.

```javascript
document.addEventListener("deviceready", function(){
  // Initialize Hotline with your AppId & AppKey from your portal https://web.hotline.io/settings/apisdk
  window.Hotline.init({
    appId       : "<Your App Id>",
    appKey      : "<Your App Key>"
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
    
    The following FAQOptions can be passed to the showFAQs() call
    
        -showFaqCategoriesAsGrid
        -showContactUsOnAppBar
        -showContactUsOnFaqScreens
        -showContactUsOnFaqNotHelpful
        
    Here is a sample call to showFAQs() with the additional parameters:
    ```javascript
    window.Hotline.showFAQs( {
        showFaqCategoriesAsGrid     :true,
        showContactUsOnAppBar       :true,
        showContactUsOnFaqScreens   :true,
        showContactUsOnFaqNotHelpful:false
    });
    ```
    Tags can also be passed as parameters to filer solution articles,
    Here is a sample call to showFAQs() implementing tags.
    
    ```javascript
    window.Hotline.showFAQs( {
        tags :["sample","video"],
        filteredViewTitle   : "Tags"
    });
    ```
    v1.1 adds support for filtering both categories and filters,
    Example of filtering FAQs by Articles.
    ```javascript
    window.Hotline.showFAQs( {
        tags :["sample","video"],
        filteredViewTitle   : "Tags",
        articleType   : Hotline.FilterType.ARTICLE
    });
    ```
    v1.1 adds support for filtering both categories and filters,
    Example of filtering FAQs by Categories.
    ```javascript
    window.Hotline.showFAQs( {
        tags :["sample","video"],
        filteredViewTitle   : "Tags",
        articleType   : Hotline.FilterType.CATEGORY
    });
    ```
    Not specifying an articleType, by default filters by Article.

* Hotline.showConversations()
    - Launch Channels / Conversations.
  
  v1.1 also adds supportto filter conversations with tags. This filters the list of channels shown to the user.
  Example showing how to filter converstions using tags.
  ```javascript
    window.Hotline.showConversations( {
        tags :["new","test"],
        filteredViewTitle   : "Tags"
    });
    ```
    NOTE:- Filtering conversations is also supported inside FAQs, i.e show conversation button from the category list
    or the article list view can also be filtered. Here is a sample.
    ```javascript
    window.Hotline.showFAQs( {
        tags :["sample","video"],
        filteredViewTitle   : "Tags",
        articleType   : Hotline.FilterType.CATEGORY,
        contactusTags : ["test"], 
        contactusFilterTitle: "contactusTags"
    });
    ```
In the above example clicking on show conversations in the filtered category list view takes you to a conversation
view filtered by the tag "test".

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

You can pass in an optional callback function to an API as the last parameter, which gets called when native API is completed. 
Eg.
```javascript
window.Hotline.unreadCount(function(success,val) {
    //success indicates whether the API call was successful
    //val contains the no of unread messages
});
```

#### Push Notifications

To setup push notifications we recommend using our forked version of the phonegap-plugin-push available [here] (https://github.com/freshdesk/phonegap-plugin-push) .
It can be installed by the following command : 
```shell
cordova plugin add https://github.com/freshdesk/phonegap-plugin-push.git
```
Or you can add it to your config.xml like:
```javascript
<plugin name="phonegap-plugin-push" spec="https://github.com/freshdesk/phonegap-plugin-push.git">
        <param name="SENDER_ID" value="XXXXXXXXXX" />
    </plugin>
```

Initialize the push plugin and it will handle registering the tokens and displaying the notifications.
here is a sample init function, call this in your onDeviceReady
```javascript
function initializePush() {
    var push = PushNotification.init({
        "android":{
            "senderID":"20738924380"
        },
        "ios": {
            "alert": "true",
            "badge": "true",
            "sound": "true"
        },
        "windows": {}
    });
}
```
If you decide to handle push notifications with some other plugin, here are some of the APIs you will need.

When you receive a deviceToken from GCM or APNS , you need to update the deviceToken in hotline as follows
*warning* updateRegistrationToken is now deprecated and replaced with updatePushNotificationToken from v1.2.0 and above
```javascript
    // Example illustrates usage for phonegap-push-plugin
    push.on('registration',function(data) {
        window.Hotline.updatePushNotificationToken(data.registrationId);
     });
```

Whenever a push notification is received. You will need to check if the notification originated from Hotline and have Hotline SDK handle it.

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

They follow the same priority order as Android's NotificaitonCompat.
##### DEPRECATED!

If you have been using the registerPushNotification call up until now, we recommend you use the method suggested above as we are removing support for it

```javascript
window.Hotline.registerPushNotification('ANDROID_SENDER_ID'); // takes care of registration and handling of push notification on iOS and Android.
```

updateRegistrationToken has been replaced from v1.2.0
#### Caveats

##### Android :
* Needs appcompat-v7 : 21+
* Needs support-v4 : 21+
* MinSdkVersion must be atleast 10 (in config.xml)

##### iOS
* Needs iOS 7 and above
