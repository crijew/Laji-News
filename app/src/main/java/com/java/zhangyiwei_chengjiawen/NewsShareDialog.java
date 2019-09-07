package com.java.zhangyiwei_chengjiawen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.mob.tools.utils.Strings.getString;


public class NewsShareDialog extends PopupWindow {
    private Context context;
    private int[] icon = {R.mipmap.fenxiang, R.mipmap.qq, R.mipmap.wechat, R.mipmap.weibo};
    private String[] iconName = {"分享", "QQ", "微信", "微博"};
    private List<Map<String, Object>> data_list;
    private GridView gridView;
    private SimpleAdapter simpleAdapter;
    private Button button;
    private LinearLayout view;
    PlatformActionListener platformActionListener=new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Log.e("kid","分享成功");
        }
        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e("kid","分享失败");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Log.e("kid","分享取消");
        }
    };

    public NewsShareDialog(Context context) {
        super(context);
        this.context = context;
        initialize();
        view.findViewById(R.id.newsShareExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                //dismiss();
                //showShare();
//                Log.d("aaaa", "Exit:" + isShowing() + "!!!!!");
            }
        });
    }

    private void initialize() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = (LinearLayout) layoutInflater.inflate(R.layout.dialog_newsshare, null);
        gridView = view.findViewById(R.id.newsShareGrid);
        button = view.findViewById(R.id.newsShareExit);
        setContentView(view);
        data_list = new ArrayList<>();
        getData_list();
        String[] from = {"image", "text"};
        int[] to = {R.id.newsShareItemImage, R.id.newsShareItemText};
        simpleAdapter = new SimpleAdapter(this.context, data_list, R.layout.item_newsshareapp, from, to);
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent textIntent = new Intent(Intent.ACTION_SEND);
//                textIntent.setType("text/plain");
//                textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
//                context.startActivity(Intent.createChooser(textIntent, "分享"));
                switch (i){
                    case 0:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, Common.nowNews.title+" "+Common.nowNews.imageURL+" "+Common.nowNews.content);
                        context.startActivity(Intent.createChooser(shareIntent, "分享"));
                        break;
                    case 1:
                        ShareUtils.shareQQ(Common.nowNews.title, Common.nowNews.content, Common.nowNews.imageURL, platformActionListener);
                        break;
                    case 2:
                        ShareUtils.shareWechat(Common.nowNews.title, Common.nowNews.content, Common.nowNews.imageURL, platformActionListener);
                        break;
                    case 3:
                        ShareUtils.shareWeibo(Common.nowNews.title, Common.nowNews.content, Common.nowNews.imageURL, platformActionListener);
                        break;
                    default:
                        break;
                }

                dismiss();
            }
        });
        initWindow();
    }

    private void initWindow() {
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) context, 0.5f);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((Activity) context, 1f);
            }
        });
    }

    private void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view) {
//        showAsDropDown(view, Math.abs((view.getWidth()-getWidth())/2), 10);\
        setAnimationStyle(R.style.PopupWindowEnter);
        this.update();
        this.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//        Log.d("bbbb", "in:" + isShowing() + "!!!!!");
    }

    public List<Map<String, Object>> getData_list() {
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }
    //test
    private void showShare() {
//        OnekeyShare oks = new OnekeyShare();
//        oks.disableSSOWhenAuthorize();
//        // title标题，微信、QQ和QQ空间等平台使用
//        oks.setTitle(getString(R.string.app_name));
//        // titleUrl QQ和QQ空间跳转链接
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
//        // imagePath是图片的本地路径，确保SDcard下面存在此张图片
//        //oks.setImagePath("/sdcard/test.jpg");
//        // url在微信、Facebook等平台中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网使用
//        oks.setComment("我是测试评论文本");
//        // 启动分享GUI
//        oks.show(this.context);

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle("测试分享的标题");
        sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
        sp.setText("测试分享的文本");
//        sp.setImageUrl("http://www.someserver.com/测试图片网络地址.jpg");
        sp.setSite("发布分享的网站名称");
        sp.setSiteUrl("发布分享网站的地址");

        Platform qq = ShareSDK.getPlatform (QQ.NAME);
        qq.share(sp);
    }










//    //这些实现了动画退出...但是有点问题...
//    public void superDismiss() {
//        super.dismiss();
//    }
//
//    @Override
//    public void dismiss() {
//        animateOut(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                superDismiss();
//            }
//        });
//    }
//
//    private void animateOut(final Animator.AnimatorListener listener) {
//        setAnimationStyle(R.style.PopupWindowEnter);
//        this.update();
//        view.animate().setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                listener.onAnimationEnd(animation);
//                view.animate().setListener(null);
//            }
//        }).start();
//    }
}


