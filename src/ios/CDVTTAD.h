//
//  CDVTTAD.h
//  百变魔方
//
//  Created by 王新远 on 2020/6/21.
//

#ifndef CDVTTAD_h
#define CDVTTAD_h


#endif /* CDVTTAD_h */
#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>
#import <BUAdSDK/BUNativeExpressBannerView.h>

#import <BUAdSDK/BUAdSDK.h>
@interface CordovaTTADPlugin : CDVPlugin

- (void)showAdBanner:(CDVInvokedUrlCommand*)command;

@property(nonatomic, copy) NSDictionary *sizeDcit;
@property(nonatomic, strong) BUNativeExpressBannerView *bannerView;
@property (nonatomic, strong) BUNativeExpressFullscreenVideoAd *fullscreenAd;
@property (nonatomic, strong) BUNativeExpressRewardedVideoAd *rewardedAd;
@end
