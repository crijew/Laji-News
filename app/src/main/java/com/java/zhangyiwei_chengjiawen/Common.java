package com.java.zhangyiwei_chengjiawen;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class CollectedItem {
    String newsID;
    String info;
    String title;
    String subtitle;
    String time;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CollectedItem)) return false;
        return (((CollectedItem) obj).newsID.equals(this.newsID));
    }

    CollectedItem(String newsID, String info, String title, String subtitle, String time) {
        this.newsID = newsID;
        this.info = info;
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
    }
}


class Common {
    final static String[] category = {"娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
    static boolean nightMode = false;
    static ArrayList<String> history = new ArrayList<>();
    static int currentItem = 0;
    static ArrayList<Integer> added = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    static ArrayList<Integer> deleted = new ArrayList<>();
    static ArrayList<CollectedItem> collected = new ArrayList<>();
    static boolean changed = false;

    static String encodingToUrl(String size, String startDate, String endDate, String words, String categories, String page) {
        String[] args = new String[]{size, startDate, endDate, words, categories, page};
        String[] encoded = new String[6];
        for (int i = 0; i < args.length; ++i) {
            try {
                encoded[i] = URLEncoder.encode(args[i], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                encoded[i] = null;
            }
        }
        return String.format(
                "https://api2.newsminer.net/svc/news/queryNewsList?size=%s&startDate=%s&endDate=%s&words=%s&categories=%s&page=%s",
                encoded[0], encoded[1], encoded[2], encoded[3], encoded[4], encoded[5]);
    }

    static void loadData(Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        nightMode = sp.getBoolean("nightMode", false);
        history = gson.fromJson(sp.getString("history", "[]"), new TypeToken<ArrayList<String>>(){}.getType());
        added = gson.fromJson(sp.getString("added", "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]"), new TypeToken<ArrayList<Integer>>(){}.getType());
        deleted = gson.fromJson(sp.getString("deleted", "[]"), new TypeToken<ArrayList<Integer>>(){}.getType());
        collected = gson.fromJson(sp.getString("collected", "[]"), new TypeToken<ArrayList<CollectedItem>>(){}.getType());
    }

    static void saveData(Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("nightMode", nightMode);
        editor.putString("history", gson.toJson(history));
        editor.putString("added", gson.toJson(added));
        editor.putString("deleted", gson.toJson(deleted));
        editor.putString("collected", gson.toJson(collected));
        editor.apply();
    }
}
