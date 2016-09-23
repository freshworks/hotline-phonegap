//
//  HotlinePlugin.m
//  HotlineSDK
//
//  Copyright(c) 2016 Freshdesk. All rights reserved.

#import<Cordova/CDV.h>


#import "HotlinePlugin.h"
#import "Hotline.h"

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
    
    HotlineConfig *config = [[HotlineConfig alloc]initWithAppID:appId  andAppKey:appKey];
    NSLog(@"Inside init appId:%@ appKey:%@ domain:%@", appId, appKey, domain);
    
    if(domain) {
        NSLog(@"domain value: %@",domain);
        config.domain = domain;
    }
    
    if(initParams [@"cameraCaptureEnabled"]) {
        config.cameraCaptureEnabled = [[initParams objectForKey:@"cameraCaptureEnabled"] boolValue];
    }
    
    if(initParams [@"voiceMessagingEnabled"]) {
        config.voiceMessagingEnabled = [[initParams objectForKey:@"voiceMessagingEnabled"] boolValue];
    }
    
    if(initParams [@"pictureMessagingEnabled"]) {
        config.pictureMessagingEnabled = [[initParams objectForKey:@"pictureMessagingEnabled"] boolValue];
    }
    
    if(initParams [@"agentAvatarEnabled"]) {
        config.agentAvatarEnabled = [[initParams objectForKey:@"agentAvatarEnabled"] boolValue];
    }

    if(initParams [@"showNotificationBanner"]) {
        config.showNotificationBanner = [[initParams objectForKey:@"showNotificationBanner"] boolValue];
    }

    [[Hotline sharedInstance] updateUserPropertyforKey:@"Phonegap iOS" withValue:@"Version : 0.6"];
    [[Hotline sharedInstance] initWithConfig:config];
    [self callbackToJavascriptWithoutResultForCommand:command];
}

- (void) showConversations :(CDVInvokedUrlCommand*)command {
    [[Hotline sharedInstance] showConversations:[self viewController]];
}

- (void) showFAQs :(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSDictionary* faqParams;
    if(arguments != nil && arguments.count > 0) {
        faqParams = [arguments firstObject];
        FAQOptions *options = [FAQOptions new];
        
        if(faqParams [@"showFaqCategoriesAsGrid"]) {
            options.showFaqCategoriesAsGrid = [[faqParams objectForKey:@"showFaqCategoriesAsGrid"] boolValue];
        }
        if(faqParams [@"showContactUsOnFaqScreens"]) {
            options.showContactUsOnFaqScreens = [[faqParams objectForKey:@"showContactUsOnFaqScreens"] boolValue];
        }
        if(faqParams [@"showContactUsOnAppBar"]) {
            options.showContactUsOnAppBar = [[faqParams objectForKey:@"showContactUsOnAppBar"] boolValue];
        }
        NSMutableArray *tagsList = [NSMutableArray array];
        NSArray* tags = [faqParams objectForKey:@"tags"];
        if(tags != nil && tags.count > 0) {
            for(int i=0; i<tags.count; i++) {
                [tagsList addObject:[tags objectAtIndex:i]];
            }
            [options filterByTags:tagsList withTitle:[faqParams objectForKey:@"filteredViewTitle"]];
        }
        [[Hotline sharedInstance]showFAQs:[self viewController] withOptions:options];
    } else {
        [[Hotline sharedInstance] showFAQs:[self viewController]];
    }
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
        user.email = [args objectForKey:@"email"];
    }
    if([args objectForKey:@"countryCode"] != nil) {
        user.phoneCountryCode = [args objectForKey:@"countryCode"];
    }
    if([args objectForKey:@"phoneNumber"] != nil) {
        user.phoneNumber = [args objectForKey:@"phoneNumber"];
    }
    if([args objectForKey:@"externalId"] != nil) {
        user.externalID = [args objectForKey:@"externalId"];
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

- (void) getVersionName :(CDVInvokedUrlCommand*)command {
    NSString* versionNumber = [Hotline SDKVersion];
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
    NSLog(@"checking if hotline push notification");
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
