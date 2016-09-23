//
//  Hotline.h
//  Konotor
//
//  Copyright (c) 2015 Freshdesk. All rights reserved.
//
//  Contact support@hotline.io

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#define HOTLINE_UNREAD_MESSAGE_COUNT @"com.freshdesk.hotline_unread_notification_count"

@class HotlineConfig, HotlineUser, FAQOptions;

@interface HotlineConfig : NSObject

/*
 * App ID of your App. This is used to identify the SDK for your app to hotline.io. 
 * Please see API & App under Settings ( https://web.hotline.io/settings/apisdk ) to get your App ID.
 */
@property (strong, nonatomic) NSString *appID;
/*
 * App Key of your App. This is used to authenticate the SDK for your app to hotline.io.
 * Please see API & App under Settings ( https://web.hotline.io/settings/apisdk ) to get your App Key.
 */
@property (strong, nonatomic) NSString *appKey;
/*
 * Domain for Hotline. Do not change this. Set to "https://app.konotor.com" if you are migrating from Konotor
 */
@property (strong, nonatomic) NSString *domain;
/*
 * Enable/disable voice messages. When enabled, users can record and send audio messages to the Agents. Default is set to NO.
 */
@property (nonatomic, assign) BOOL voiceMessagingEnabled;
/*
 * Enable/disable picture messages. When enabled, users can send images over chat. Default is set to YES.
 */
@property (nonatomic, assign) BOOL pictureMessagingEnabled;
/**
 * Option to supply the SDK with your theme file's name. Make sure themeName is the same as the
 * theme plist file's name. Hotline needs this for theming to work.
 * The setter throws an exception for an invalid filename
 */
@property (nonatomic, strong) NSString *themeName;
/**
 * Option to supply the SDK with a strings bundle for localization
 */
@property (nonatomic, strong) NSString *stringsBundle;
/*
 * Allow the user to attach images using the camera. Defaults to YES.
 */
@property (nonatomic, assign) BOOL cameraCaptureEnabled;
/*
 * Enable alert sound when a notification is received. Defaults to YES.
 */
@property (nonatomic, assign) BOOL notificationSoundEnabled;
/*
 * Show/Hide Agent Avatar on the Chat. It is enabled by default. Default YES
 */
@property (nonatomic, assign) BOOL agentAvatarEnabled;
/*
 * Enable/Disable Notification banner when a support message is received. Defaults to YES
 */
@property (nonatomic, assign) BOOL showNotificationBanner;
/*
 * Enable/Disable Polling for messages when the app is Active . Defaults to NO
 */
@property (nonatomic, assign) BOOL pollWhenAppActive;

/**
 *  Initialize Hotline.
 *
 *  @discussion In order to initialize Hotline, you'll need the App ID and App Key. Place the Hotline initialization code in your app delegate, preferably at the top of the application:didFinishLaunchingWithOptions method.
 *
 *  @param appID  The App ID assigned to your app when it was created on the portal.
 *  @param appKey The App Key assigned to your app when it was created on the portal.
 *
 */
-(instancetype)initWithAppID:(NSString*)appID andAppKey:(NSString*)appKey;

@end

@interface Hotline : NSObject

@property(nonatomic, strong, readonly) HotlineConfig *config;

+(NSString *)SDKVersion;

/**
 *  Access the Hotline instance.
 *
 *  @discussion Using the returned shared instance, you can access all the instance methods available in Hotline.
 */
+(instancetype) sharedInstance;

/**
 *  Initialize configuration for Config.
 *
 *  @param config Hotline Configuration of type HotlineConfig
 */

-(void)initWithConfig:(HotlineConfig *)config;

/**
 *  Show the Conversations / Chat to the user.
 *
 *  @discussion This method lets you launch and present the Channels list to the user. The user directly lands in the Conversation view if there is only one channel.
 *
 *  @param controller The view controller from where you present the Conversations view.
 *
 */
-(void)showConversations:(UIViewController *)controller;
/**
 *  Show the FAQs to the user.
 *
 *  @discussion This method lets you show the FAQ view.
 *
 *  @param controller The view controller from where you present the FAQ view.
 *
 */
-(void)showFAQs:(UIViewController *)controller;

/**
 *  Show the FAQs to the user.
 *
 *  @discussion This method lets you show the FAQ view.
 *
 *  @param controller The view controller from where you present the FAQ view.
 *
 *  @param options filter by tags or control FAQ screen options
 *
 */
-(void)showFAQs:(UIViewController *)controller withOptions:(FAQOptions *)options;
/**
 *  Update user Info
 *
 *  @discussion Sends user information updates to the server. User properties such as Name, Email, Phone, Country Code and external Identifier.That are set will be synced with the server. External Identifier provided could be any unique value that your App can use to identify the user.
 *
 *  @param user User instance with the values to be updated.
 *
 */
-(void)updateUser:(HotlineUser *) user;
/**
 *  Clear User Data
 *
 *  @discussion Use this function when your user needs to log out of the app . 
 *  This will clean up all the data associated with the SDK for the user.
 *
 */
-(void)clearUserData __attribute__((deprecated("Please use clearUserDataWithCompletion: instead")));
/**
 *  Clear User Data
 *
 *  @discussion Use this function when your user needs to log out of the app .
 *  This will clean up all the data associated with the SDK for the user.
 *  Please use the completion block if you are updating user information or subsequently calling init 
 *  
 * @param Completion block to be called when clearData is completed
 *
 */
-(void)clearUserDataWithCompletion:(void (^)())completion;
/**
 *  Update User properties
 *
 *  @discussion Tag users with custom properties (key-value pairs) . The user properties associated here will be shown on the dashboard for the agent and also be used for segmentation for campaigns
 *
 *  @param props An NSDictionary containing the Properties for the User.
 *
 */
-(void)updateUserProperties:(NSDictionary*)props;
/**
 *  Update user property
 *
 *  @discussion Use this method to update a single property for the user. Use updateUserProperties instead where possible.
 *
 *  @param key Property name
 *
 *  @param value Property value
 *
 */
-(void)updateUserPropertyforKey:(NSString *) key withValue:(NSString *)value;
/**
 *  Update the APNS device token
 *
 *  @discussion Update the APNS device token when APNS registration is successful. The SDK uses this to send push notification when there are replies from the agent.
 *
 *  @param deviceToken APNS device token
 *
 */
-(void)updateDeviceToken:(NSData *) deviceToken;
/**
 *  Check if a push notification was from Hotline
 *
 *  @discussion Checks if the push notification received originated from Hotline by examining the payload dictionary. Use this in conjunction with handleRemoteNotification
 *
 *  @param info NSDictionary object in didReceiveRemoteNotification for Push Notification.
 */
-(BOOL)isHotlineNotification:(NSDictionary *)info;
/**
 *  Handle the Hotline push notifications
 *
 *  @discussion Needs to be called when a push notification is received from Hotline. This will present the conversation if user tapped on a push, or show a drop down notification, or update a currently active conversation screen depending on context.
 *
 *  @param info Dictionary received in didReceiveRemoteNotification for Push Notification.
 *  
 *  @param appState UIApplicationState object that helps the app be aware of whether it was already active and in the foreground when receiving the push notification, or was opened from the background
 */
-(void)handleRemoteNotification:(NSDictionary *)info andAppstate:(UIApplicationState)appState;
/**
 *  Get an embeddable controller for FAQs
 *
 *  @discussion Return a controller with FAQs View that can be embedded in other Controllers (e.g. in a UITabBarController )
 *
 *  @return UIController for FAQs View
 */
-(UIViewController*) getFAQsControllerForEmbed;
/**
 *  Get an embeddable controller for Conversations
 *
 *  @discussion Return a controller with Conversation view that can be embedded in other Controllers (e.g. in a UITabBarController )
 *
 *  @return UIController for Conversation View
 *
 */
-(UIViewController*) getConversationsControllerForEmbed;
/**
 *  Get the last updated unread messages count.
 *
 *  @discussion This method returns the last updated count of conversations which require the user's attention. This may not always be up to date.
 *  
 *  @return unread count for messages
 */
-(NSInteger)unreadCount;

/**
 *  Get the unread conversations count.
 *
 *  @discussion This method lets you asynchronously fetch the latest count of conversations that require the user's attention. It is updated with a 2 min interval.
 *
 *  @param completion Completion block with count.
 *
 */
-(void)unreadCountWithCompletion:(void(^)(NSInteger count))completion;

-(void)updateConversationBannerMessage:(NSString *)message;

-(void)sendMessage:(NSString *)message onChannel:(NSString *)channelName;

@end

@interface HotlineUser : NSObject

/*
 * User name
 */
@property (strong, nonatomic) NSString *name;
/*
 * User email
 */
@property (strong, nonatomic) NSString *email;
/*
 * Phone Number - Preferably Mobile Number
 */
@property (strong, nonatomic) NSString *phoneNumber;
/*
 * Phone Country Code e.g +91 for India
 */
@property (strong, nonatomic) NSString *phoneCountryCode;
/*
 * Unique identifier for the user.
 */
@property (strong, nonatomic) NSString *externalID;


/*
 * Access the user info. If update user was called earlier, the instance would contain the persisted values.
 */
+(instancetype)sharedInstance;

@end

@interface FAQOptions : NSObject

/*
 * Option to Switch between Grid and List view in FAQs. Shows FAQ categories as a list when set to NO.
 * Default set to YES which presents a Grid view
 */
@property (nonatomic) BOOL showFaqCategoriesAsGrid;
/*
 * Option to show "contact us" button on the FAQ Screens,
 * Default set to YES which shows "contact us" button
 */
@property (nonatomic) BOOL showContactUsOnFaqScreens;
/*
 * Option to show "contact us" button on the navigation bar,
 * Default set to NO which hides "contact us" button on the navigation bar
 */
@property (nonatomic) BOOL showContactUsOnAppBar;

/**
 *  Show Filtered articles list
 *
 *  @discussion This method lets you to launch and present a controller with the list of articles filtered by the tags supplied
 *
 *  @param List of tags that are configured already the portal
 *
 *  @param Controller's navigation bar title
 *
 */
-(void)filterByTags:(NSArray *)tags withTitle:(NSString *)title;

/**
 *  Preferred navigation bar title
 *
 *  @discussion
 */
-(NSString *)filteredViewTitle;

/**
 *  List of tags you have supplied already
 *
 *  @discussion List of tags which are configured in portal
 */
-(NSArray *)tags;

@end
