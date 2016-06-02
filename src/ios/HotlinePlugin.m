//
//  HotlinePlugin.m
//  HotlineSDK
//
//  Copyright(c) 2016 Freshdesk. All rights reserved.

#import<Cordova/CDV.h>


#import "HotlinePlugin.h"
#import "Hotline.h"
#import "Appdelegate+HotlinePush.h"

#define SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)

@implementation HotlinePlugin:CDVPlugin

-(void) callbackToJavascriptWithResult:(CDVPluginResult*)result ForCommand:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

-(void) callbackToJavascriptWithoutResultForCommand:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* emptyResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    [self callbackToJavascriptWithResult:emptyResult ForCommand:command];
}

-(void) callbackToJavascriptWithException:(NSException*)e ForCommand:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[e name]];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}

-(void) init:(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSDictionary* initParams;
    if(arguments != nil && arguments.count > 0) {
        initParams = [arguments firstObject];
    } else {
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    NSString* domain = [initParams objectForKey:@"domain"];
    NSString* appId = [initParams objectForKey:@"appId"];
    NSString* appKey = [initParams objectForKey:@"appKey"];
    
    BOOL cameraCapture = [[initParams objectForKey:@"cameraCaptureEnabled"] boolValue];
    BOOL voiceMessaging = [[initParams objectForKey:@"voiceMessagingEnabled"] boolValue];
    BOOL pictureMessaging = [[initParams objectForKey:@"pictureMessagingEnabled"] boolValue];
    BOOL agentAvatar = [[initParams objectForKey:@"agentAvatarEnabled"] boolValue];
    BOOL FAQsAsGrid = [[initParams objectForKey:@"displayFAQsAsGrid"] boolValue];
    BOOL notificationAsBanner = [[initParams objectForKey:@"showNotificationBanner"] boolValue];
    
    HotlineConfig *config = [[HotlineConfig alloc]initWithAppID:appId  andAppKey:appKey];
    NSLog(@"Inside init appId:%@ appKey:%@ domain:%@", appId, appKey, domain);
    
    if(domain) {
        NSLog(@"domain value: %@",domain);
        config.domain = domain;
    }
    
    if( cameraCapture) {
        config.cameraCaptureEnabled = cameraCapture;
    }
    
    if(voiceMessaging) {
        config.voiceMessagingEnabled = voiceMessaging;
    }
    
    if(pictureMessaging) {
        config.pictureMessagingEnabled = pictureMessaging;
    }
    
    if(agentAvatar) {
        config.agentAvatarEnabled = agentAvatar;
    }

    if(FAQsAsGrid) {
        config.displayFAQsAsGrid = FAQsAsGrid;
    }

    if(notificationAsBanner) {
        config.showNotificationBanner = notificationAsBanner;
    }
    [[Hotline sharedInstance] initWithConfig:config];
}

- (void) showConversations :(CDVInvokedUrlCommand*)command {
    [[Hotline sharedInstance] showConversations:[self viewController]];
}

- (void) showFAQs :(CDVInvokedUrlCommand*)command {
    [[Hotline sharedInstance] showFAQs:[self viewController]];
}

- (void) clearUserData : (CDVInvokedUrlCommand*)command {
    [self.commandDelegate runInBackground:^{
        [[Hotline sharedInstance]clearUserDataWithCompletion:nil];
    }];
}

- (void) unreadCount :(CDVInvokedUrlCommand*)command {
    NSInteger unreadCount = [[Hotline sharedInstance] unreadCount];
    NSLog(@" The unread count value is: %d", unreadCount);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)unreadCount];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}

- (void) updateUser :(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSDictionary* args;
    if(arguments != nil && arguments.count > 0) {
        args = [arguments firstObject];
    } else {
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    HotlineUser *user = [HotlineUser sharedInstance];

    if([args objectForKey:@"name"] != nil) {
        user.name = [args objectForKey:@"name"];
    }
    if([args objectForKey:@"email"] != nil) {
        user.name = [args objectForKey:@"email"];
    }
    if([args objectForKey:@"countryCode"] != nil) {
        user.name = [args objectForKey:@"countryCode"];
    }
    if([args objectForKey:@"phoneNumber"] != nil) {
        user.name = [args objectForKey:@"phoneNumber"];
    }
    if([args objectForKey:@"externalId"] != nil) {
        user.name = [args objectForKey:@"externalId"];
    }   
    [self.commandDelegate runInBackground:^{
        [[Hotline sharedInstance] updateUser:user];
    }];
}

- (void) updateUserProperties :(CDVInvokedUrlCommand*)command {

    NSArray* arguments = [command arguments];
    NSDictionary* args;
    if(arguments != nil && arguments.count > 0) {
        args = [arguments firstObject];
    } else {
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    
    NSArray *arrayOfKeys = [args allKeys];
    NSArray *arrayOfValues = [args allValues];

    NSString *key;
    NSString *value;
    
        for(int i=0; i<arrayOfKeys.count; i++) {
            key = [arrayOfKeys objectAtIndex:i];
            value = [arrayOfValues objectAtIndex:i];
            NSLog(@" The userMeta key is: %@ value is: %@", key,value);
            [[Hotline sharedInstance] updateUserPropertyforKey:key withValue:value];
        }
}

- (void) registerPushNotification : (CDVInvokedUrlCommand*)command{
    NSLog(@"Notification is being registered");
    if (SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"8.0")) {
        [[UIApplication sharedApplication] registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:(UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge) categories:nil]];
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    }else{
        [[UIApplication sharedApplication] registerForRemoteNotificationTypes: (UIRemoteNotificationTypeNewsstandContentAvailability| UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
    }
}

- (void) getVersionName :(CDVInvokedUrlCommand*)command {
    NSInteger versionNumber = [Hotline SDKVersion];
    NSLog(@"Hotline version: %@", versionNumber);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)versionNumber];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}

- (void) updateRegistrationToken :(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSData* devToken;
    if(arguments != nil && arguments.count > 0) {
        devToken = [arguments firstObject];
    } else {
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    NSLog(@"Registration token value: %@", devToken);
    [self.commandDelegate runInBackground:^{
        [[Hotline sharedInstance] updateDeviceToken:devToken];
    }];
    NSLog(@"Registration token has been updated");
}

- (void) isHotlinePushNotificationInternal :(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSDictionary* info;
    if(arguments != nil && arguments.count > 0) {
        info = [arguments firstObject];
    } else {
       [self callbackToJavascriptWithoutResultForCommand:command];
    }
    if ([[Hotline sharedInstance]isHotlineNotification:info]) {
        NSLog(@"It is a hotline notification");
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)1];
        [self callbackToJavascriptWithResult:result ForCommand:command];
    }
}

- (void) handlePushNotification : (CDVInvokedUrlCommand*)command {
    NSLog(@"Received a hotline push notification");
    NSArray* arguments = [command arguments];
    NSDictionary* info = [arguments firstObject];
    [self.commandDelegate runInBackground:^{
        [[Hotline sharedInstance]handleRemoteNotification:info andAppstate:[UIApplication sharedApplication].applicationState];
    }];
}
@end
