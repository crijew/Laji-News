package com.java.zhangyiwei_chengjiawen;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NewsListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> data;

    NewsListAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        class ViewHolder {
            private TextView itemTitle;
            private ImageView itemImage;
            private TextView itemSubtitle;
            private TextView itemTime;
        }

        ViewHolder viewHolder;
        HashMap<String, String> map = data.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.news_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemTitle = convertView.findViewById(R.id.itemTitle);
            viewHolder.itemImage = convertView.findViewById(R.id.itemImage);
            viewHolder.itemSubtitle = convertView.findViewById(R.id.itemSubtitle);
            viewHolder.itemTime = convertView.findViewById(R.id.itemTime);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.itemImage.setImageDrawable(null);
        String image = map.get("itemImage");
        if (image != null && !image.equals("")) {
            viewHolder.itemImage.setVisibility(View.VISIBLE);
            viewHolder.itemImage.setPadding(MainActivity.dpToPx(context, context.getResources().getInteger(R.integer.paddingLR)), 0, 0, 0);
            viewHolder.itemImage.getLayoutParams().width = MainActivity.dpToPx(context, 120);
            viewHolder.itemImage.getLayoutParams().height = MainActivity.dpToPx(context, 67.5f);
            Glide.with(context)
                    .load(image)
                    //.placeholder(R.drawable.blank)
                    .override(MainActivity.dpToPx(context, 120), MainActivity.dpToPx(context, 67.5f))
                    .centerCrop()
                    .transition(new DrawableTransitionOptions().crossFade(300))
                    .into(viewHolder.itemImage);
        } else {
            viewHolder.itemImage.setVisibility(View.INVISIBLE);
            viewHolder.itemImage.setPadding(0, 0, 0, 0);
            viewHolder.itemImage.getLayoutParams().width = 0;
            viewHolder.itemImage.getLayoutParams().height = 0;
        }
        viewHolder.itemTitle.setText(map.get("itemTitle"));
        viewHolder.itemSubtitle.setText(map.get("itemSubtitle"));
        viewHolder.itemTime.setText(map.get("itemTime"));
        return convertView;
    }
}

public class NewsFragment extends Fragment {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private final Pattern pattern = Pattern.compile("^\\[(.*?)[,\\]]");
    private ListView newsList = null;
    private NewsListAdapter adapter = null;
    private int pageNum = 0;
    ArrayList<HashMap<String, String>> itemList = new ArrayList<>();

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //Successfully get data -> split
            if (adapter == null) {
                adapter = new NewsListAdapter(getContext(), itemList);
                newsList.setAdapter(adapter);
            }
            if (msg.what == 0 || msg.what == 1) {
                try {
                    if (msg.what == 0) itemList.clear();
                    String result = (String) msg.obj;
                    JSONObject object = new JSONObject(result);
                    JSONArray allData = object.getJSONArray("data");
                    for (int i = 0; i < allData.length(); ++i) {
                        JSONObject data = allData.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();
                        Matcher matcher = pattern.matcher(data.getString("image"));
                        if (matcher.find()) map.put("itemImage", matcher.group(1));
                        else map.put("itemImage", "");
                        map.put("itemTitle", data.getString("title"));
                        map.put("itemSubtitle", data.getString("publisher"));
                        map.put("itemTime", data.getString("publishTime"));
                        itemList.add(map);
                    }
                } catch (JSONException e) {
                }
            } else {
                itemList.clear();
                HashMap<String, String> map = new HashMap<>();
                map.put("itemImage", "");
                map.put("itemTitle", "Getting news item failed");
                map.put("itemSubtitle", (String) msg.obj);
                map.put("itemTime", "");
                itemList.add(map);
            }
            adapter.notifyDataSetChanged();
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        View view = inflater.inflate(R.layout.news_fragment, container, false);
        newsList = view.findViewById(R.id.newsList);
        getNews(true);

        //Refresh
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
//        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
//        refreshLayout.setColorSchemeColors(
//                Color.rgb(182, 182, 182),
//                Color.rgb(92, 172, 238),
//                Color.rgb(92, 172, 238),
//                Color.rgb(92, 172, 238)
//        );
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getNews(false);
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getNews(true);
                refreshLayout.finishRefresh();
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) return;
        newsList.setAdapter(new NewsListAdapter(getContext(), new ArrayList<HashMap<String, String>>()));
        getNews(true);
    }

    private void getNews(final boolean refresh) {
        if (refresh) pageNum = 0;
        pageNum += 1;
        String size = "10";
        String startDate = "";
        String endDate = sdf.format(new Date());
        String words = (String) getArguments().get("word");
        String categories = (String) getArguments().get("type");
        String page = Integer.toString(pageNum);
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
                        if (refresh)
                            handler.sendMessage(handler.obtainMessage(0, result));
                        else
                            handler.sendMessage(handler.obtainMessage(1, result));
                    } else
                        handler.sendMessage(handler.obtainMessage(2, "Status code: " + connection.getResponseCode()));
                } catch (MalformedURLException e) {
                    handler.sendMessage(handler.obtainMessage(3, "MalformedURLException"));
                } catch (IOException e) {
                    handler.sendMessage(handler.obtainMessage(4, "IOException"));
                } finally {
                    if (br != null)
                        try {
                            br.close();
                        } catch (IOException e) {
                            handler.sendMessage(handler.obtainMessage(4, "IOException"));
                        }
                    connection.disconnect();
                }
            }
        }).start();
    }
}
