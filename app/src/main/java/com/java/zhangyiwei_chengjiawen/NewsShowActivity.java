package com.java.zhangyiwei_chengjiawen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

public class NewsShowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsshow_main);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        String publishTime;
        String title;
        String content;
        String publisher;
        String category;

        ConstraintLayout newsShowMain = findViewById(R.id.newsShowMain);
        ScrollView view = (ScrollView) LayoutInflater.from(this).inflate(R.layout.newsshow_content, null);
        newsShowMain.addView(view);

        try {
            JSONObject jsonObject = new JSONObject(info);
            publishTime = jsonObject.getString("publishTime");
            title = jsonObject.getString("title");
            content = jsonObject.getString("content");
            publisher = jsonObject.getString("publisher");
            category = jsonObject.getString("category");
            ((TextView) view.findViewById(R.id.newsShowContentTitle)).setText(title);
            ((TextView) view.findViewById(R.id.newsShowContentTime)).setText(publishTime);

        } catch (Exception e) {
        }


    }

}
