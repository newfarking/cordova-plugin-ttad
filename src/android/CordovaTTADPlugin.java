package cordova.plugin.ttad;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.Toast;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import cordova.plugin.ttad.TTAdManagerHolder;
import cordova.plugin.ttad.TToast;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaTTADPlugin extends CordovaPlugin {

    private TTNativeExpressAd mTTAd;
    private TTNativeExpressAd mTTAdInteraction;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean inited = false;
    private TTAdNative mTTAdNative;
    private boolean mHasShowDownloadActive = false;
    private ViewGroup parentView;
    private static CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        TTAdManagerHolder.init(cordova.getActivity());
        mTTAdNative = TTAdManagerHolder.get().createAdNative(cordova.getActivity());
        TTAdManagerHolder.get().requestPermissionIfNecessary(cordova.getActivity());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if (action.equals("showAdBanner")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    showAdBanner();
                }
            });
            return true;
        } else if (action.equals("showInteractionAd")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    showInteractionAd();
                }
            });
            return true;
        } else if (action.equals("showRewardAd")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    CordovaTTADPlugin.callbackContext = callbackContext;
                    showRewardAd();
                }
            });
        }
        return false;
    }

    private void showRewardAd() {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId("945243360")
                    .setSupportDeepLink(true)
                    .setRewardName("观看完视频获取1个提示奖励") //奖励的名称
                    .setRewardAmount(1)  //奖励的数量
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                    .setExpressViewAcceptedSize(500,500)
                    .setUserID("38268")//用户id,必传参数
                    //.setMediaExtra("media_extra") //附加参数，可选
                    .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();
        
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                //Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
                TToast.show(cordova.getActivity(), message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                //Log.e(TAG, "Callback --> onRewardVideoCached");
                //TToast.show(cordova.getActivity(), "Callback --> rewardVideoAd video cached");

            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                //Log.e(TAG, "Callback --> onRewardVideoAdLoad");

                //TToast.show(cordova.getActivity(), "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        //Log.d(TAG, "Callback --> rewardVideoAd show");
                        //TToast.show(cordova.getActivity(), "rewardVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        //Log.d(TAG, "Callback --> rewardVideoAd bar click");
                        //TToast.show(cordova.getActivity(), "rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        //Log.d(TAG, "Callback --> rewardVideoAd close");
                        //TToast.show(cordova.getActivity(), "rewardVideoAd close");
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        //Log.d(TAG, "Callback --> rewardVideoAd complete");
                        //TToast.show(cordova.getActivity(), "rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        //Log.e(TAG, "Callback --> rewardVideoAd error");
                        //TToast.show(cordova.getActivity(), "rewardVideoAd error");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName;
                        //Log.e(TAG, "Callback --> " + logString);
                        //TToast.show(cordova.getActivity(), logString);
                        if (rewardVerify) {
                            //TToast.show(cordova.getActivity(), "before");
                            CordovaTTADPlugin.callbackContext.success();
                            //TToast.show(cordova.getActivity(), "after");
                        }
                    }

                    @Override
                    public void onSkippedVideo() {
                        //Log.e(TAG, "Callback --> rewardVideoAd has onSkippedVideo");
                        TToast.show(cordova.getActivity(), "rewardVideoAd has onSkippedVideo");
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        //Log.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            TToast.show(cordova.getActivity(), "下载中，点击下载区域暂停", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        //Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(cordova.getActivity(), "下载暂停，点击下载区域继续", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        //Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(cordova.getActivity(), "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        //Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(cordova.getActivity(), "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        //Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        TToast.show(cordova.getActivity(), "安装完成，点击下载区域打开", Toast.LENGTH_LONG);
                    }
                });

                mttRewardVideoAd.showRewardVideoAd(cordova.getActivity());
            }
        });
    }

    private void showInteractionAd() {
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945241996") //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(450, 300) //期望模板广告view的size,单位dp
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(cordova.getActivity(), "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAdInteraction = ads.get(0);
                bindAdListenerInteraction(mTTAdInteraction);
                
                mTTAdInteraction.render();
            }
        });
    }


    private void bindAdListenerInteraction(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override
            public void onAdDismiss() {
                //TToast.show(cordova.getActivity(), "广告关闭");
            }

            @Override
            public void onAdClicked(View view, int type) {
                //TToast.show(cordova.getActivity(), "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
                //TToast.show(cordova.getActivity(), "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                //Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                TToast.show(cordova.getActivity(), msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
                //TToast.show(cordova.getActivity(), "渲染成功");
                mTTAdInteraction.showInteractionExpressAd(cordova.getActivity());

            }
        });
        //bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(cordova.getActivity(), "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    TToast.show(cordova.getActivity(), "下载中，点击暂停", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(cordova.getActivity(), "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                TToast.show(cordova.getActivity(), "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                TToast.show(cordova.getActivity(), "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                TToast.show(cordova.getActivity(), "点击安装", Toast.LENGTH_LONG);
            }
        });
    }


    private void showAdBanner() {

        AdSlot adSlot = new AdSlot.Builder()
                  .setCodeId("945235157") //广告位id
                  .setSupportDeepLink(true)
                  .setAdCount(1) //请求广告数量为1到3条
                  .setExpressViewAcceptedSize(480,60) //期望个性化模板广告view的size,单位dp
                  //.setImageAcceptedSize(640,320 )//这个参数设置即可，不影响个性化模板广告的size
                  .build();
        
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
                     @Override
                     public void onError(int code, String message) {
                         TToast.show(cordova.getActivity(), "load error : " + code + ", " + message);
                         // mExpressContainer.removeAllViews();
                     }

                     @Override
                     public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                         if (ads == null || ads.size() == 0){
                             return;
                         }
                         mTTAd = ads.get(0);
                         mTTAd.setSlideIntervalTime(30*1000);//设置轮播间隔 ms,不调用则不进行轮播展示
                         bindAdListener(mTTAd);
                         mTTAd.render();//调用render开始渲染广告
                     }
                 });
    }


    private View getWebView() {
        CordovaWebView _webView = webView;
        try {
            return (View) _webView.getClass().getMethod("getView").invoke(_webView);
        } catch (Exception e) {
            return (View) _webView;
        }
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                //TToast.show(cordova.getActivity(), "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
                //TToast.show(cordova.getActivity(), "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                //Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                //TToast.show(cordova.getActivity(), msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //Log.e("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
                //TToast.show(cordova.getActivity(), "渲染成功");
                // mExpressContainer.removeAllViews();
                // mExpressContainer.addView(view);
                // 
                /* overlap
                CordovaWebView _webView = webView;
                RelativeLayout adViewLayout = new RelativeLayout(cordova.getActivity());
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                try {
                    ((ViewGroup) (((View) _webView.getClass().getMethod("getView").invoke(_webView)).getParent())).addView(adViewLayout, params1);
                } catch (Exception e) {
                    ((ViewGroup) _webView).addView(adViewLayout, params1);
                }

                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);// RelativeLayout.ALIGN_PARENT_BOTTOM);

                adViewLayout.addView(view, params2);
                adViewLayout.bringToFront();
                */

                ViewGroup wvParentView = (ViewGroup) getWebView().getParent();
                if (parentView == null) {
                    parentView = new LinearLayout(webView.getContext());
                }
                if (wvParentView != null && wvParentView != parentView) {
                    ViewGroup rootView = (ViewGroup)(getWebView().getParent());
                    wvParentView.removeView(getWebView());
                    ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
                    parentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                    getWebView().setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
                    parentView.addView(getWebView());
                    rootView.addView(parentView);
                }

                boolean top = false;
                if (top) {
                    parentView.addView(view, 0);
                } else {
                    parentView.addView(view);
                }
                parentView.bringToFront();
                parentView.requestLayout();
                parentView.requestFocus();
            }
        });
        
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                TToast.show(cordova.getActivity(), "点击开始下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                //if (!mHasShowDownloadActive) {
                //    mHasShowDownloadActive = true;
                //    TToast.show(cordova.getActivity(), "下载中，点击暂停", Toast.LENGTH_LONG);
                // }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                // TToast.show(cordova.getActivity(), "下载暂停，点击继续", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                //TToast.show(BannerExpressActivity.this, "下载失败，点击重新下载", Toast.LENGTH_LONG);
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                //TToast.show(BannerExpressActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG);
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                //TToast.show(BannerExpressActivity.this, "点击安装", Toast.LENGTH_LONG);
            }
        });
    }

    

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }*/

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }


    private String getAdType(int type) {
        switch (type) {
            case TTAdConstant.AD_TYPE_COMMON_VIDEO:
                return "普通激励视频，type=" + type;
            case TTAdConstant.AD_TYPE_PLAYABLE_VIDEO:
                return "Playable激励视频，type=" + type;
            case TTAdConstant.AD_TYPE_PLAYABLE:
                return "纯Playable，type=" + type;
        }

        return "未知类型+type=" + type;
    }
}
