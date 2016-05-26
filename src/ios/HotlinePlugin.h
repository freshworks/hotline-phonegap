//
//  HotlinePlugin.h
//  HotlineSDK
//
//  copyright (c) 2016 Freshdesk. All rights reserved.
//

#import <Cordova/CDV.h>
#import "Hotline.h"

@interface HotlinePlugin:CDVPlugin
    
-(void)init:(CDVInvokedUrlCommand*)command;
-(void)clearUserData:(CDVInvokedUrlCommand*)command;
-(void)unreadCount:(CDVInvokedUrlCommand*)command;
-(void)registerPushNotification:(CDVInvokedUrlCommand*)command;
-(void)updateUser:(CDVInvokedUrlCommand*)command;
-(void)updateUserProperties:(CDVInvokedUrlCommand*)command;
-(void)getVersionName:(CDVInvokedUrlCommand*)command;
-(void)showConversations:(CDVInvokedUrlCommand*)command;
-(void)showFAQs:(CDVInvokedUrlCommand*)command;

@end