package com.java.zhangyiwei_chengjiawen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TabLayout mainTab;
    private ViewPager mainViewPager;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private final int[] activate = new int[]{R.mipmap.collection_activate, R.mipmap.news_activate, R.mipmap.setting_activate};
    private final int[] noactivate = new int[]{R.mipmap.collection_noactivate, R.mipmap.news_noactivate, R.mipmap.setting_noactivate};

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
        return 25;
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
        mainTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((ImageView)tab.getCustomView().findViewById(R.id.icon)).setImageResource(activate[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((ImageView)tab.getCustomView().findViewById(R.id.icon)).setImageResource(noactivate[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mainViewPager.setOffscreenPageLimit(3);
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
        });
        mainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTab));
        mainTab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mainViewPager));
        for (int i = 0; i < activate.length; ++i) {
            View view = getLayoutInflater().inflate(R.layout.tab_item, null);
            ((ImageView) view.findViewById(R.id.icon)).setImageResource(noactivate[i]);
            ((TextView) view.findViewById(R.id.title)).setText(titles.get(i));
            mainTab.addTab(mainTab.newTab().setCustomView(view));
        }
        mainTab.getTabAt(1).select();
    }
}