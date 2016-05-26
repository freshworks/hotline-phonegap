#import<Cordova/CDV.h>
#import <objc/runtime.h>

#import "HotlinePlugin.h"
#import "Appdelegate+HotlinePush.h"

@implementation AppDelegate (hotlinePush)

+ (void)load {
    [self swizzleDidRegisterForRemoteNotification];
    [self swizzleDidFailToRegisterForRemoteNotification];
    [self swizzleDidReceiveRemoteNotification];
    [self swizzleDidFinishLaunchingWithOptions];
}



static void (*OriginalDidRegisterForRemoteNotification)(id, SEL, UIApplication *, NSData *);
static void (*OriginalDidFailToRegisterForRemoteNotification)(id, SEL, UIApplication *, NSError *);
static void (*OriginalDidReceiveRemoteNotification)(id, SEL, UIApplication *, NSDictionary *);
static BOOL (*OriginalDidFinishLaunchingWithOptions)(id, SEL, UIApplication *, NSDictionary *);

+ (void)swizzleDidRegisterForRemoteNotification {

        __block Class originalClass = [AppDelegate class];
        if (!originalClass) {
            return;
        }
    
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
        SEL originalSelector = @selector(application:didRegisterForRemoteNotificationsWithDeviceToken:);
        IMP replacement = imp_implementationWithBlock(^void (id _self, UIApplication *application, NSData *deviceToken) { 
                NSLog(@"Hotline Registered for remote notifications %@ ", deviceToken);
                [[Hotline sharedInstance] updateDeviceToken:deviceToken];
                if(*OriginalDidRegisterForRemoteNotification != NULL) {
                    OriginalDidRegisterForRemoteNotification(_self, _cmd, application, deviceToken);
                }
            });
            
            IMP *store = (IMP *) &OriginalDidRegisterForRemoteNotification;
            IMP originalImp = NULL;
            Method method = class_getInstanceMethod(originalClass, originalSelector);
            if (method) {
                    const char *type = method_getTypeEncoding(method);
                    originalImp = class_replaceMethod(originalClass, originalSelector, replacement, type);
                if (!originalImp) {
                    originalImp = method_getImplementation(method);
                }
                if (originalImp && store) {
                    *store = originalImp;
                }
            }else{
                *store = NULL;
                class_addMethod(originalClass, originalSelector, replacement, "v@:@@");
            }
     });    
}

+ (void)swizzleDidFailToRegisterForRemoteNotification {

        __block Class originalClass = [AppDelegate class];
        if (!originalClass) {
            return;
        }
    
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
        SEL originalSelector = @selector(application:didFailToRegisterForRemoteNotificationsWithError:);
        IMP replacement = imp_implementationWithBlock(^void (id _self, UIApplication *application, NSError *error) { 
                NSLog(@"Failed to register remote notification %@", error);
                if(*OriginalDidFailToRegisterForRemoteNotification != NULL) {
                    OriginalDidFailToRegisterForRemoteNotification(_self, _cmd, application, error);
                }
            });
            
            IMP *store = (IMP *) &OriginalDidFailToRegisterForRemoteNotification;
            IMP originalImp = NULL;
            Method method = class_getInstanceMethod(originalClass, originalSelector);
            if (method) {
                    const char *type = method_getTypeEncoding(method);
                    originalImp = class_replaceMethod(originalClass, originalSelector, replacement, type);
                if (!originalImp) {
                    originalImp = method_getImplementation(method);
                }
                if (originalImp && store) {
                    *store = originalImp;
                }
            }else{
                *store = NULL;
                class_addMethod(originalClass, originalSelector, replacement, "v@:@@");
            }
     });    
}

+ (void)swizzleDidReceiveRemoteNotification {

        __block Class originalClass = [AppDelegate class];
        if (!originalClass) {
            return;
        }
    
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
        SEL originalSelector = @selector(application:didReceiveRemoteNotification:);
        IMP replacement = imp_implementationWithBlock(^void (id _self, UIApplication *application, NSDictionary *info) {
                NSLog(@"Hotline:Received remote notifications");
                if([[Hotline sharedInstance]isHotlineNotification:info]) {
                    [[Hotline sharedInstance]handleRemoteNotification:info andAppstate:application.applicationState];
                }
              });
            
            IMP *store = (IMP *) &OriginalDidReceiveRemoteNotification;
            IMP originalImp = NULL;
            Method method = class_getInstanceMethod(originalClass, originalSelector);
            if (method) {
                    const char *type = method_getTypeEncoding(method);
                    originalImp = class_replaceMethod(originalClass, originalSelector, replacement, type);
                if (!originalImp) {
                    originalImp = method_getImplementation(method);
                }
                if (originalImp && store) {
                    *store = originalImp;
                }
            }else{
                *store = NULL;
                class_addMethod(originalClass, originalSelector, replacement, "v@:@@");
            }
     });    
}

+ (BOOL) swizzleDidFinishLaunchingWithOptions {
    __block Class originalClass = [AppDelegate class];
        if (!originalClass) {
            return false;
        }

        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            SEL originalSelector = @selector(application:didFinishLaunchingWithOptions:);
            IMP replacement = imp_implementationWithBlock(^void (id _self, UIApplication *application, NSDictionary *launchOptions) {
                if([[Hotline sharedInstance]isHotlineNotification:launchOptions]) {
                    [[Hotline sharedInstance]handleRemoteNotification:launchOptions andAppstate:application.applicationState];
                }
                if(OriginalDidFinishLaunchingWithOptions){
                    OriginalDidFinishLaunchingWithOptions(_self, _cmd, application, launchOptions);
                }
            });
            IMP *store = (IMP *) &OriginalDidFinishLaunchingWithOptions;
            IMP originalImp = NULL;
            Method method = class_getInstanceMethod(originalClass, originalSelector);
            if (method) {
                    const char *type = method_getTypeEncoding(method);
                    originalImp = class_replaceMethod(originalClass, originalSelector, replacement, type);
                if (!originalImp) {
                    originalImp = method_getImplementation(method);
                }
                if (originalImp && store) {
                    *store = originalImp;
                }
            }else{
                *store = NULL;
                class_addMethod(originalClass, originalSelector, replacement, "v@:@@");
            }
    });
 }
@end