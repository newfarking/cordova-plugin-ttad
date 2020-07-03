//
//  CDVTTAD.m
//  百变魔方
//
//  Created by 王新远 on 2020/6/21.
//

#import <Foundation/Foundation.h>

#import "CDVTTAD.h"
#import <Cordova/CDV.h>
#import <BUAdSDK/BUAdSDKManager.h>

#import <BUAdSDK/BUAdSDK.h>
#import "BUDMacros.h"
#import "BUDSlotID.h"

@interface CordovaTTADPlugin ()<BUNativeExpressFullscreenVideoAdDelegate, BUNativeExpressRewardedVideoAdDelegate>
@end

@implementation CordovaTTADPlugin
- (void)pluginInitialize {
    [super pluginInitialize];
    self.sizeDcit = @{
    express_banner_ID         :  [NSValue valueWithCGSize:CGSizeMake(600, 90)],
    express_banner_ID_60090   :  [NSValue valueWithCGSize:CGSizeMake(600, 90)],
    express_banner_ID_640100  :  [NSValue valueWithCGSize:CGSizeMake(640, 100)],
    express_banner_ID_600150  :  [NSValue valueWithCGSize:CGSizeMake(600, 150)],
    express_banner_ID_690388  :  [NSValue valueWithCGSize:CGSizeMake(690, 388)],
    express_banner_ID_600260  :  [NSValue valueWithCGSize:CGSizeMake(600, 260)],
    express_banner_ID_600300  :  [NSValue valueWithCGSize:CGSizeMake(600, 300)],
    express_banner_ID_600400_both  :  [NSValue valueWithCGSize:CGSizeMake(600, 400)],
    express_banner_ID_600500_both  :  [NSValue valueWithCGSize:CGSizeMake(600, 500)],
    };
    
    [BUAdSDKManager setAppID:@"5077375"];
}

- (void)showRewardAd:(CDVInvokedUrlCommand *)command
{
    __block CDVPluginResult* pluginResult = nil;
    [self.commandDelegate runInBackground:^{
        [self loadRewardVideoAdWithSlotID:@"945284113"];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

#pragma mark - BUNativeExpressRewardedVideoAdDelegate
- (void)nativeExpressRewardedVideoAdDidLoad:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAd:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd didFailWithError:(NSError *_Nullable)error {
    BUD_Log(@"%s",__func__);
    NSLog(@"error code : %ld , error message : %@",(long)error.code,error.description);
}

- (void)nativeExpressRewardedVideoAdCallback:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd withType:(BUNativeExpressRewardedVideoAdType)nativeExpressVideoType{
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdDidDownLoadVideo:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdViewRenderSuccess:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    [self showRewardVideoAd];
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdViewRenderFail:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd error:(NSError *_Nullable)error {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdWillVisible:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdDidVisible:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdWillClose:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdDidClose:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdDidClick:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdDidClickSkip:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdDidPlayFinish:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd didFailWithError:(NSError *_Nullable)error {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdServerRewardDidSucceed:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd verify:(BOOL)verify {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdServerRewardDidFail:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd {
    BUD_Log(@"%s",__func__);
}

- (void)nativeExpressRewardedVideoAdDidCloseOtherController:(BUNativeExpressRewardedVideoAd *)rewardedVideoAd interactionType:(BUInteractionType)interactionType {
    NSString *str = nil;
    if (interactionType == BUInteractionTypePage) {
        str = @"ladingpage";
    } else if (interactionType == BUInteractionTypeVideoAdDetail) {
        str = @"videoDetail";
    } else {
        str = @"appstoreInApp";
    }
    BUD_Log(@"%s __ %@",__func__,str);
}

// end


- (void)loadRewardVideoAdWithSlotID:(NSString *)slotID {
    BURewardedVideoModel *model = [[BURewardedVideoModel alloc] init];
    model.userId = @"123";
    self.rewardedAd = [[BUNativeExpressRewardedVideoAd alloc] initWithSlotID:slotID rewardedVideoModel:model];
    self.rewardedAd.delegate = self;
    [self.rewardedAd loadAdData];
}

- (void)showRewardVideoAd {
    if (self.rewardedAd) {
        [self.rewardedAd showAdFromRootViewController:self.viewController];
    }
}


- (void)showInteractionAd:(CDVInvokedUrlCommand *)command
{
    __block CDVPluginResult* pluginResult = nil;
    [self.commandDelegate runInBackground:^{
        [self loadFullscreenVideoAdWithSlotID:@"945284110"];
        //[self resizeViews];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}
- (void)nativeExpressFullscreenVideoAdViewRenderSuccess:(BUNativeExpressFullscreenVideoAd *) rewardedVideoAd {
    [self showFullscreenVideoAd];
}

- (void)nativeExpressFullscreenVideoAdViewRenderFail:(BUNativeExpressFullscreenVideoAd *)rewardedVideoAd error:(NSError *_Nullable)error {
   
    BUD_Log(@"%s",__func__);
}
- (void)loadFullscreenVideoAdWithSlotID:(NSString *)slotID {
    self.fullscreenAd = [[BUNativeExpressFullscreenVideoAd alloc] initWithSlotID:slotID];
    self.fullscreenAd.delegate = self;
    [self.fullscreenAd loadAdData];
}

- (void)showFullscreenVideoAd {
    if (self.fullscreenAd) {
        [self.fullscreenAd showAdFromRootViewController:self.viewController];
    }
}

- (void)showAdBanner:(CDVInvokedUrlCommand *)command
{
    __block CDVPluginResult* pluginResult = nil;
    
    [self.commandDelegate runInBackground:^{
        NSLog(@"InMobi plugin called");
        NSString *slotID = @"945282151";
        NSValue *sizeValue = [self.sizeDcit objectForKey: slotID];
        CGSize size = [sizeValue CGSizeValue];
        CGFloat screenWidth = CGRectGetWidth([UIScreen mainScreen].bounds);
        CGFloat screenHeight = CGRectGetHeight([UIScreen mainScreen].bounds);
        CGFloat bannerHeigh = screenWidth/size.width*size.height;
    #warning 升级的用户请注意，初始化方法去掉了imgSize参数
        // 轮播
        self.bannerView = [[BUNativeExpressBannerView alloc] initWithSlotID:slotID rootViewController:self.viewController adSize:CGSizeMake(screenWidth, bannerHeigh) IsSupportDeepLink:YES interval:30];
        

        self.bannerView.frame = CGRectMake(0, screenHeight - bannerHeigh, screenWidth, bannerHeigh);
        self.bannerView.delegate = self;
        [self.bannerView loadAdData];
        
        UIView* parentView = [self.webView superview];
        [parentView addSubview:self.bannerView];
        [parentView bringSubviewToFront:self.bannerView];
        
        //[self resizeViews];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)resizeViews {
    // Frame of the main container view that holds the Cordova webview.
    CGRect pr = self.webView.superview.bounds, wf = pr;
    //NSLog(@"super view: %d x %d", (int)pr.size.width, (int)pr.size.height);

    // iOS7 Hack, handle the Statusbar
    //BOOL isIOS7 = ([[UIDevice currentDevice].systemVersion floatValue] >= 7);
    //CGRect sf = [[UIApplication sharedApplication] statusBarFrame];
    //CGFloat top = isIOS7 ? MIN(sf.size.height, sf.size.width) : 0.0;
    float top = 0.0;

    //if(! self.offsetTopBar) top = 0.0;

    wf.origin.y = top;
    wf.size.height = pr.size.height - top;

    CGRect bf = self.bannerView.frame;

    // If the ad is not showing or the ad is hidden, we don't want to resize anything.
    UIView* parentView = [self.webView superview];
    // move webview to top
    wf.origin.y = top;
    bf.origin.y = pr.size.height - bf.size.height;

    if (@available(iOS 11.0, *)) {
        bf.origin.y -= parentView.safeAreaInsets.bottom;
        bf.size.width = wf.size.width - parentView.safeAreaInsets.left - parentView.safeAreaInsets.right;
        wf.size.height -= parentView.safeAreaInsets.bottom;

    }
    wf.size.height -= bf.size.height;

    bf.origin.x = (pr.size.width - bf.size.width) * 0.5f;

    self.bannerView.frame = bf;

    self.webView.frame = wf;
}
@end
