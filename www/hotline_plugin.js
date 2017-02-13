//
//  hotline_plugin.js
//
//  Copytright (c) 2016 Freshdesk. All rights reserved.


// ------------------------------------------------------------------------------------
// --------------------------------UTILITY FUNCTIONS-----------------------------------
// ------------------------------------------------------------------------------------

//Function accepts a function Name as parameter and returns a closure which calls the native function of that name
//The frst argument is ALWAYS the class Name.

var createWrapperForNativeFunction = function(functionName) {
    return function() {
        var argumentsArray = Array.prototype.slice.call(arguments || []);
        var success,failure;
        
        //if user has provded callback
        //  remove callback from arguments and assign it to userCallback
        //else
        // have a dummy callback
        // Set the callback function to be called on success and failure
        var userCallback;
        var size = argumentsArray.length - 1;
        
        if (size != -1 && typeof argumentsArray[size] == 'function')
            userCallback = argumentsArray.splice(size,1)[0];    //remove the last param and store it in userCallback
        else
            userCallback = function() {}
        success = function(e) { userCallback(true,e);   }
        failure = function(e) { userCallback(false,e);  }

        //Call corresponding native function
        return cordova.exec(success,failure,"HotlinePlugin",functionName,argumentsArray);    
        
    }
}

var Hotline = {}

Hotline.isHotlinePushNotification = function(args, cb){
    
    Hotline._isHotlinePushNotification(args,function(success, isHotline){
        cb(success, isHotline === 1 );
    });
}

Hotline.init = function(args, cb){
    createWrapperForNativeFunction("init")(args,function(success){
        if(success){
            Hotline.trackPhoneGapSDKVersion();
        }
        if(cb) {
            cb(success);
        }
    });
}

Hotline.trackPhoneGapSDKVersion = function() {
  this.updateUserProperties({ Phonegap : "v1.2.0"});
}

Hotline.clearUserData = function() {
    createWrapperForNativeFunction("clearUserData")(function(success){
        if(success) {
          Hotline.trackPhoneGapSDKVersion();
        }
    });
}

//Add Wrapper functions to Hotline
var functionList = [
    "unreadCount",
    "updateUser",
    "updateUserProperties",
    "showConversations",
    "showFAQs",
    "getVersionName",
    "_isHotlinePushNotification",
    "handlePushNotification",
    "updatePushNotificationToken",
    "updateAndroidNotificationProperties"
];

functionList.forEach(function(funcName) {
    Hotline[funcName] = createWrapperForNativeFunction(funcName);
});

Hotline.NotificationPriority = {};
Hotline.NotificationPriority.PRIORITY_DEFAULT  = 0;
Hotline.NotificationPriority.PRIORITY_HIGH = 1;
Hotline.NotificationPriority.PRIORITY_LOW = -1;
Hotline.NotificationPriority.PRIORITY_MAX = 2;
Hotline.NotificationPriority.PRIORITY_MIN = -2;

Hotline.FilterType= {};
Hotline.FilterType.ARTICLE = "article";
Hotline.FilterType.CATEGORY = "category";

module.exports = Hotline;
