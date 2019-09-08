package com.java.zhangyiwei_chengjiawen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewsShowActivity extends AppCompatActivity {

    Pattern pattern = Pattern.compile("[\\[ ](.*?)[,\\]]");
    boolean clickCollect = false;
    String newsID = null;
    CollectedItem thisItem;
    boolean changed = false;

    //数据
    String publishTime;
    String title;
    String content;
    String publisher;
    String category;
    String image;
    List<String> imageList = new ArrayList<>();
    JSONArray keyWords;
    String[] itemRecommend = new String[3];


    //TODO mark changed
    ArrayList<HashMap<String, String>> itemList = new ArrayList<>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsshow_main);

        //子控件
        final ImageView newsBannerCollect = findViewById(R.id.newsBannerCollect);
        final LinearLayout newsShowMain = findViewById(R.id.newsShowMain);

        //分享初始化
        MobSDK.init(NewsShowActivity.this);

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

        final Intent intent = getIntent();

        //获得数据
        final String info = intent.getStringExtra("info");
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
            keyWords = jsonObject.getJSONArray("keywords");
            Matcher matcher = pattern.matcher(image);
            while (matcher.find()) {
                String url = matcher.group(1);
                imageList.add(url);
            }
            ((TextView) view.findViewById(R.id.newsShowContentTitle)).setText(title);
            ((TextView) view.findViewById(R.id.newsShowContentTime)).setText(publishTime);
            ((TextView) view.findViewById(R.id.newsShowContentPublisher)).setText(publisher);
            ((TextView) view.findViewById(R.id.newsShowContentContent)).setText(content);
            thisItem = new CollectedItem(newsID, info, title, publisher, publishTime, content, imageList.get(0));
            if (Common.collected.contains(thisItem)){
                clickCollect = true;
            }
            Common.nowNews = thisItem;
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

        getNews(0);
        getNews(1);
        getNews(2);
        NewsShowActivity.this.findViewById(R.id.newsSHowContentRecommendItem1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(NewsShowActivity.this, NewsShowActivity.class);
                intent1.putExtra("info", itemRecommend[0]);
                startActivityForResult(intent1, 0);
            }
        });
        NewsShowActivity.this.findViewById(R.id.newsSHowContentRecommendItem2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(NewsShowActivity.this, NewsShowActivity.class);
                intent2.putExtra("info", itemRecommend[1]);
                startActivityForResult(intent2, 0);
            }
        });
        NewsShowActivity.this.findViewById(R.id.newsSHowContentRecommendItem3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(NewsShowActivity.this, NewsShowActivity.class);
                intent3.putExtra("info", itemRecommend[2]);
                startActivityForResult(intent3, 0);
            }
        });
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


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1){
                NewsShowActivity.this.findViewById(R.id.newsSHowContentRecommend).setVisibility(View.GONE);
            } else {
                try{
                    String[] result = (String[]) msg.obj;
                    JSONObject object = new JSONObject(result[0]);
                    String whichItem = result[1];
                    String keyWord = keyWords.getJSONObject(Integer.parseInt(result[1])).getString("word");
                    JSONArray allData = object.getJSONArray("data");
                    Log.d("alldata", allData.toString());
                    double maxScore = 0.0;
                    int index = 0;
                    for (int i = 0; i < allData.length(); ++i){
                        JSONObject data = allData.getJSONObject(i);
                        String tempNewsTitle = data.getString("title");
                        if (!tempNewsTitle.equals(title)){
                            JSONArray dataArraySocre = data.getJSONArray("keywords");
                            double tempScore = 0.0;
                            for (int j = 0; j < dataArraySocre.length(); j++){
                                if (dataArraySocre.getJSONObject(j).get("word").equals(keyWord)){
                                    tempScore = dataArraySocre.getJSONObject(j).getDouble("score");
                                    break;
                                }
                            }
                            if (tempScore > maxScore){
                                maxScore = tempScore;
                                index = i;
                            }
                        }
                    }
                    switch (whichItem){
                        case "0":
                            if (allData.length() != 0){
                                JSONObject dataItem1 = allData.getJSONObject(index);
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemTitle1)).setText(dataItem1.getString("title"));
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemSubtitle1)).setText(dataItem1.getString("publisher"));
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemTime1)).setText(dataItem1.getString("publishTime"));
                                String info1 = dataItem1.toString();
                                itemRecommend[0] = info1;
                            } else {
                                View tempView1 = NewsShowActivity.this.findViewById(R.id.newsSHowContentRecommendItem1);
                                tempView1.setVisibility(View.GONE);
                                tempView1.setPadding(0,0,0,0);
                            }
                            break;
                        case "1":
                            if (allData.length() != 0){
                                JSONObject dataItem2 = allData.getJSONObject(index);
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemTitle2)).setText(dataItem2.getString("title"));
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemSubtitle2)).setText(dataItem2.getString("publisher"));
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemTime2)).setText(dataItem2.getString("publishTime"));
                                String info2 = dataItem2.toString();
                                itemRecommend[1] = info2;
                            } else {
                                View tempView2 = NewsShowActivity.this.findViewById(R.id.newsSHowContentRecommendItem2);
                                tempView2.setVisibility(View.GONE);
                                tempView2.setPadding(0,0,0,0);
                            }
                            break;
                        case "2":
                            if (allData.length()!=0){
                                JSONObject dataItem3 = allData.getJSONObject(index);
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemTitle3)).setText(dataItem3.getString("title"));
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemSubtitle3)).setText(dataItem3.getString("publisher"));
                                ((TextView)NewsShowActivity.this.findViewById(R.id.itemTime3)).setText(dataItem3.getString("publishTime"));
                                String info3 = dataItem3.toString();
                                itemRecommend[2] = info3;
                            } else {
                                View tempView3 = NewsShowActivity.this.findViewById(R.id.newsSHowContentRecommendItem3);
                                tempView3.setVisibility(View.GONE);
                                tempView3.setPadding(0,0,0,0);
                            }
                            break;
                        default:
                            break;
                    }


                } catch (Exception e){

                }
            }
            return false;
        }
    });

    private void getNews(final int i){
        try {
            String size = "10";
            String startDate = "";
            String endDate = sdf.format(new Date());
            String words = keyWords.getJSONObject(i).getString("word");
            String categories = category;
            String page = "1";
            final String url = Common.encodingToUrl(size, startDate, endDate, words, categories, page);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    BufferedReader br = null;
                    try {
                        connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                            String result = br.readLine();
                            handler.sendMessage(handler.obtainMessage(0, new String[]{result, Integer.toString(i)}));
                        } else
                            handler.sendMessage(handler.obtainMessage(1,null));
                    } catch (Exception e) {
                        handler.sendMessage(handler.obtainMessage(1, null));
                    } finally {
                        if (br != null)
                            try {
                                br.close();
                            } catch (IOException e) {
                            }
                        connection.disconnect();
                    }
                }
            }).start();
        } catch (Exception e){

        }
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
