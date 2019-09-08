package com.java.zhangyiwei_chengjiawen;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class ShareUtils {
    //QQ好友分享
    static void shareQQ(String title, String text, String imageUrl, PlatformActionListener listener) {
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        QQ.ShareParams sp = new QQ.ShareParams();
        sp.setTitle(title);
        sp.setText(text);
        sp.setImageUrl(imageUrl);
        sp.setTitleUrl("https://www.baidu.com"); // 标题的超链接
        qq.setPlatformActionListener(listener);
        qq.SSOSetting(false);
        qq.isSSODisable();
        qq.share(sp);
    }

    //新浪微博
    static void shareWeibo(String title, String text, String imageUrl, PlatformActionListener listener) {
        Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setTitle(title);
        sp.setText(text);
        sp.setImageUrl(imageUrl);
        sina.setPlatformActionListener(listener);
        sina.share(sp);
    }

    //微信
    static void shareWechat(String title, String text, String imageUrl, PlatformActionListener listener) {
        Platform weixin = ShareSDK.getPlatform(Wechat.NAME);
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setUrl("https://www.baidu.com");
        sp.setTitle(title);
        sp.setSite("QQ空间标题");
        sp.setImageUrl(imageUrl);
        sp.setText(text);
        weixin.setPlatformActionListener(listener);
        weixin.share(sp);
    }

    //微信朋友圈
    public static void sharepyq(String title, String text, String imageUrl, PlatformActionListener listener) {
        Platform weixin = ShareSDK.getPlatform(WechatMoments.NAME);
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setTitle(title);
        sp.setImageUrl(imageUrl);
        sp.setText(text);
        weixin.setPlatformActionListener(listener);
        weixin.share(sp);
    }


}
