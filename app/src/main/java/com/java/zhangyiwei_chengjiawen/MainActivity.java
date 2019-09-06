package com.java.zhangyiwei_chengjiawen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TabLayout mainTab;
    ViewPager mainViewPager;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<Fragment> fragments = new ArrayList<>();
    int currentFragment;

    public static int dpToPx(Context context, float dp) {
        if (context == null) {
            return (int) (dp * 3.f + 0.5f);
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return getResources().getDimensionPixelSize(resourceId);
        else return 25;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Common.loadData(this);
            if (Common.nightMode) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);

        //Translucent status bar
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            LinearLayout statusBar = findViewById(R.id.statusBar);
            ViewGroup.LayoutParams params = statusBar.getLayoutParams();
            params.height = getStatusBarHeight();
            statusBar.setLayoutParams(params);
        }

        mainTab = findViewById(R.id.mainTab);
        mainViewPager = findViewById(R.id.mainViewPager);
        titles.add("收藏");
        titles.add("新闻");
        titles.add("设置");
        fragments.add(new CollectionFragment());
        fragments.add(new MainFragment());
        fragments.add(new SettingFragment());
        mainViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }

            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                currentFragment = position;
                super.setPrimaryItem(container, position, object);
            }
        });
        mainTab.setupWithViewPager(mainViewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Log.d("checkCollect", "oooook");
            ((CollectionFragment)fragments.get(0)).getAdapter().notifyDataSetChanged();
        }
        Log.d("checkCollect", "fuck");
    }

}