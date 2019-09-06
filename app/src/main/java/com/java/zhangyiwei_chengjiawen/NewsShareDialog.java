package com.java.zhangyiwei_chengjiawen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewsShareDialog extends PopupWindow {
    private Context context;
    private int[] icon = {R.mipmap.qq, R.mipmap.wechat};
    private String[] iconName = {"微信", "QQ"};
    private List<Map<String, Object>> data_list;
    private GridView gridView;
    private SimpleAdapter simpleAdapter;
    private Button button;
    private LinearLayout view;

    public NewsShareDialog(Context context) {
        super(context);
        this.context = context;
        initialize();
        view.findViewById(R.id.newsShareExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });
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
//
//    //这些实现了动画退出...是吧，还是只是慢了点？
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


