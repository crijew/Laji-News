package com.java.zhangyiwei_chengjiawen;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

class Common {
    static boolean nightMode = false;
    static ArrayList<String> history = new ArrayList<>();
    static int currentItem = 0;
    final static String[] category = {"收藏", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
    static ArrayList<Integer> added = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    static ArrayList<Integer> deleted = new ArrayList<>();

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

    static boolean loadData(Context context) {
        boolean flag = true;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput("data");
        } catch (Exception e) {
            return false;
        }
        try {
            ois = new ObjectInputStream(fis);
            Object[] objects = (Object[]) ois.readObject();
            Common.history = (ArrayList<String>) objects[0];
            Common.added = (ArrayList<Integer>) objects[1];
            Common.deleted = (ArrayList<Integer>) objects[2];
            Common.nightMode = (Boolean) objects[3];
        } catch (Exception e) {
            flag = false;
        }
        try {
            fis.close();
        } catch (Exception e) {
            flag = false;
        }
        try {
            ois.close();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    static boolean saveData(Context context) {
        boolean flag = true;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput("data", Context.MODE_PRIVATE);
        } catch (Exception e) {
            return false;
        }
        try {
            oos = new ObjectOutputStream(fos);
            Object[] objects = {Common.history, Common.added, Common.deleted, Common.nightMode};
            oos.writeObject(objects);
        } catch (Exception e) {
            flag = false;
        }
        try {
            oos.close();
        } catch (Exception e) {
            flag = false;
        }
        try {
            fos.close();
        } catch (IOException e) {
            flag = false;
        }
        return flag;
    }
}
