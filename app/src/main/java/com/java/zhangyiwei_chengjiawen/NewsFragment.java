package com.java.zhangyiwei_chengjiawen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

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
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NewsListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> data;

    class ViewHolder {
        private TextView itemTitle;
        private ImageView itemImage;
        private TextView itemSubtitle;
        private TextView itemTime;
        String info;
    }

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
                    .override(MainActivity.dpToPx(context, 120), MainActivity.dpToPx(context, 80))
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
        viewHolder.info = map.get("info");
        return convertView;
    }
}

public class NewsFragment extends Fragment {
    private View rootView;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private final Pattern pattern = Pattern.compile("^\\[(.*?)[,\\]]");
    private ListView newsList = null;
    private NewsListAdapter adapter = null;
    private int pageNum = 0;
    ArrayList<HashMap<String, String>> itemList = new ArrayList<>();

    SmartRefreshLayout refreshLayout;
    View networkError;
    View resultError;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //Successfully get data -> split
            if (adapter == null) {
                adapter = new NewsListAdapter(getContext(), itemList);
                newsList.setAdapter(adapter);
            }
            if (msg.what == 0b11 || msg.what == 0b01) {
                if (msg.what == 0b11)
                    itemList.clear();
                try {
                    String result = (String) msg.obj;
                    JSONObject object = new JSONObject(result);
                    JSONArray allData = object.getJSONArray("data");
                    if (allData.length() == 0)
                        if (msg.what == 0b11) {
                            resultError.setVisibility(View.VISIBLE);
                            networkError.setVisibility(View.GONE);
                            refreshLayout.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getContext(), "没有更多了...", Toast.LENGTH_SHORT).show();
                        }
                    else {
                        resultError.setVisibility(View.GONE);
                        networkError.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < allData.length(); ++i) {
                        JSONObject data = allData.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();
                        Matcher matcher = pattern.matcher(data.getString("image"));
                        if (matcher.find()) map.put("itemImage", matcher.group(1));
                        else map.put("itemImage", "");
                        map.put("itemTitle", data.getString("title"));
                        map.put("itemSubtitle", data.getString("publisher"));
                        map.put("itemTime", data.getString("publishTime"));
                        map.put("info", allData.getString(i));
                        itemList.add(map);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                }
            } else if (msg.what == 0b10 && itemList.size() == 0) {
                resultError.setVisibility(View.GONE);
                networkError.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
            } else Toast.makeText(getContext(), "网络有点问题...", Toast.LENGTH_SHORT).show();
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_fragment, container, false);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        newsList = rootView.findViewById(R.id.newsList);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsListAdapter.ViewHolder viewHolder = (NewsListAdapter.ViewHolder) view.getTag();
                Intent intent = new Intent(getContext(), NewsShowActivity.class);
                intent.putExtra("info", viewHolder.info);
                startActivityForResult(intent, 0);
            }
        });
        getNews(true);

        networkError = rootView.findViewById(R.id.netWorkError);
        resultError = rootView.findViewById(R.id.resultError);

        //Refresh
        refreshLayout = rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setPrimaryColorsId(R.color.newsFragmentBG, R.color.waterDrop);
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

        rootView.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            itemList.clear();
            networkError.setVisibility(View.GONE);
            resultError.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            return;
        }
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
                        handler.sendMessage(handler.obtainMessage(refresh ? 0b11 : 0b01, result));
                    } else
                        handler.sendMessage(handler.obtainMessage(refresh ? 0b10 : 0b00, "Status code: " + connection.getResponseCode()));
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(refresh ? 0b10 : 0b00, "MalformedURLException"));
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
    }
}
