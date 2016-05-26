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
        if (typeof argumentsArray[0] == 'function')
            userCallback = argumentsArray.splice(0,1)[0];    //remove first param and store it in userCallback
        else
            userCallback = function() {}
        success = function(e) { userCallback(true,e);   }
        failure = function(e) { userCallback(false,e);  }
        
        //Call corresponding native function
		return cordova.exec(success,failure,"HotlinePlugin",functionName,argumentsArray);
    }
}

var Hotline = {}

Hotline.init = function(args) {
    args = args || {};
    
    console.log("inside init");
    //Assign default values
    var configDefaults = {
        agentAvatarEnabled      : false,
        cameraCaptureEnabled    : true,
        voiceMessagingEnabled   : false,
        pictureMessagingEnabled : true,
        displayFAQsAsGrid       : true,
        showNotificationBanner  : true
    };
    for (k in configDefaults)
        args[k] = args[k] || configDefaults[k];
    
    //Call native function
    createWrapperForNativeFunction("init")(args);
}

Hotline.isHotlinePushNotification = function(cb, args){
    
    Hotline.isHotlinePushNotificationInternal(function(success, isHotline){
        if( isHotline === 1){
            cb(true);
        }
        else {
            cb(false);
        }
    }, args);
}

var createEnum = function(constants) {  
    var Enum = {};
    constants.forEach(function(constant) {
        Enum[constant] = constant;
    })
    return Enum;
}

//Add Wrapper functions to Hotline
var functionList = [
    "unreadCount",
    "registerPushNotification",
    "clearUserData",
    "updateUser",
    "updateUserProperties",
    "showConversations",
    "showFAQs",
    "getVersionName",
    "isHotlinePushNotificationInternal",
    "handlePushNotification",
    "updateRegistrationToken"
];

functionList.forEach(function(funcName) {
    Hotline[funcName] = createWrapperForNativeFunction(funcName);
});


Hotline.HotlineCallbackStatus = createEnum([
	"STATUS_INVALID_APP",
	"STATUS_NO_NETWORK_CONNECTION",
	"STATUS_NO_TICKETS_CREATED",
	"STATUS_SUCCESS",
	"STATUS_UNKNOWN"
]);
module.exports = Hotline;
