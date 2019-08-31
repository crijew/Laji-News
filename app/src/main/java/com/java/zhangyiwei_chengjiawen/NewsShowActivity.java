package com.java.zhangyiwei_chengjiawen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewsShowActivity extends AppCompatActivity {

    Pattern pattern = Pattern.compile("[\\[ ](.*?)[,\\]]");
    boolean clickCollect = false;
    String newsID = null;
    CollectedItem thisItem;
    boolean changed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsshow_main);

        //子控件
        final ImageView newsBannerCollect = findViewById(R.id.newsBannerCollect);

        //夜间模式topBar字体颜色
        View decor = getWindow().getDecorView();
        if (!Common.nightMode) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

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

        //数据
        final String info = intent.getStringExtra("info");
        String publishTime;
        String title;
        final String content;
        String publisher;
        String category;
        String image;
        List<String> imageList = new ArrayList<>();

        final LinearLayout newsShowMain = findViewById(R.id.newsShowMain);
        ScrollView view = (ScrollView) LayoutInflater.from(this).inflate(R.layout.newsshow_content, null);
        newsShowMain.addView(view, -1);
        try {
            JSONObject jsonObject = new JSONObject(info);
            publishTime = jsonObject.getString("publishTime");
            title = jsonObject.getString("title");
            content = jsonObject.getString("content");
            publisher = jsonObject.getString("publisher");
            category = jsonObject.getString("category");
            image = jsonObject.getString("image");
            newsID = jsonObject.getString("newsID");
            Matcher matcher = pattern.matcher(image);
            while (matcher.find()) {
                String url = matcher.group(1);
                imageList.add(url);
            }
            ((TextView) view.findViewById(R.id.newsShowContentTitle)).setText(title);
            ((TextView) view.findViewById(R.id.newsShowContentTime)).setText(publishTime);
            ((TextView) view.findViewById(R.id.newsShowContentPublisher)).setText(publisher);
            ((TextView) view.findViewById(R.id.newsShowContentContent)).setText(content);
            thisItem = new CollectedItem(newsID, info, title, publisher, publishTime);
            if (Common.collected.contains(thisItem)){
                clickCollect = true;
            }
        } catch (Exception e) {
        }


        //返回实现部分
        newsShowMain.findViewById(R.id.newsBannerIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //分享实现部分
        newsShowMain.findViewById(R.id.newsBannerMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewsShareDialog(NewsShowActivity.this).showAtBottom(findViewById(R.id.newsShowMain));
            }
        });


        //收藏部分
        if (clickCollect){
            newsBannerCollect.setImageResource(R.mipmap.collected);
        } else {
            newsBannerCollect.setImageResource(R.mipmap.collect);
        }
        newsShowMain.findViewById(R.id.newsBannerCollect).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (!clickCollect){
                    newsBannerCollect.setImageResource(R.mipmap.collected);
                    Common.collected.add(0, thisItem);
                } else {
                    newsBannerCollect.setImageResource(R.mipmap.collect);
                    Common.collected.remove(thisItem);
                }
                clickCollect = !clickCollect;
                changed = !changed;
                Common.saveData(NewsShowActivity.this);
            }
        });



        //图片显示
        //        for(String name : urlMaps.keySet()){
//            TextSliderView textSliderView = new TextSliderView(this);
//            textSliderView
//                    .description(name)//描述
//                    .image(urlMaps.get(name))//image方法可以传入图片url、资源id、File
//                    .setScaleType(BaseSliderView.ScaleType.Fit)//图片缩放类型
//                    .setOnSliderClickListener(onSliderClickListener);//图片点击
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle().putString("extra",name);//传入参数
//            mDemoSlider.addSlider(textSliderView);//添加一个滑动页面
//
//        }
        if (imageList.size() == 0 || imageList.get(0).equals("")) {
            SliderLayout mDemoSlider = (SliderLayout) findViewById(R.id.newsShowContentSlider);
            mDemoSlider.setVisibility(View.GONE);
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.newsShowContentImage);
            relativeLayout.setVisibility(View.GONE);
            relativeLayout.getLayoutParams().height = 0;
            relativeLayout.getLayoutParams().width = 0;
        } else {
            SliderLayout mDemoSlider = (SliderLayout) findViewById(R.id.newsShowContentSlider);
            for (int i = 0; i < imageList.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView
                        .description("图" + Integer.toString(i + 1))//描述
                        .image(imageList.get(i))//image方法可以传入图片url、资源id、File
                        .setScaleType(BaseSliderView.ScaleType.Fit);//图片缩放类型
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", "图" + Integer.toString(i + 1));//传入参数
                mDemoSlider.addSlider(textSliderView);//添加一个滑动页面
            }
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);//滑动动画
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);//默认指示器样式
            //        mDemoSlider.setCustomIndicator((PagerIndicator)findViewById(R.id.newsShowContentCustom_Indicator2));//自定义指示器
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());//设置图片描述显示动画
            mDemoSlider.setDuration(4000);//设置滚动时间，也是计时器时间
            mDemoSlider.addOnPageChangeListener(onPageChangeListener);
        }

    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return getResources().getDimensionPixelSize(resourceId);
        else return 25;
    }

    @Override
    public void finish() {
        Common.changed = changed;
        super.finish();
    }

    //页面改变监听
    private ViewPagerEx.OnPageChangeListener onPageChangeListener = new ViewPagerEx.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

}
