package com.java.zhangyiwei_chengjiawen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

public class NewsShowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsshow_main);

        //Translucent status bar
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            LinearLayout statusBarNews = findViewById(R.id.statusBarNews);
            ViewGroup.LayoutParams params = statusBarNews.getLayoutParams();
            params.height = getStatusBarHeight();
            statusBarNews.setLayoutParams(params);
        }

        Intent intent = getIntent();

        String info = intent.getStringExtra("info");
        String publishTime;
        String title;
        String content;
        String publisher;
        String category;

        LinearLayout newsShowMain = findViewById(R.id.newsShowMain);
        ScrollView view = (ScrollView) LayoutInflater.from(this).inflate(R.layout.newsshow_content, null);
        newsShowMain.addView(view, -1);

        try {
            JSONObject jsonObject = new JSONObject(info);
            publishTime = jsonObject.getString("publishTime");
            title = jsonObject.getString("title");
            content = jsonObject.getString("content");
            publisher = jsonObject.getString("publisher");
            category = jsonObject.getString("category");
            ((TextView)view.findViewById(R.id.newsShowContentTitle)).setText(title);
            ((TextView)view.findViewById(R.id.newsShowContentTime)).setText(publishTime);
            ((TextView)view.findViewById(R.id.newsShowContentPublisher)).setText(publisher);
            ((TextView)view.findViewById(R.id.newsShowContentContent)).setText(content);

        } catch (Exception e){}

        newsShowMain.findViewById(R.id.newsBannerIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return getResources().getDimensionPixelSize(resourceId);
        else return 25;
    }

}
