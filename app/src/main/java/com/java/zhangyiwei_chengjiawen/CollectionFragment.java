package com.java.zhangyiwei_chengjiawen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;

class CollectionNewsListAdapter extends BaseAdapter{
    private Context context;

    class CollectionViewHolder{
        private TextView itemTitle;
        private ImageView itemImage;
        private TextView itemSubtitle;
        private TextView itemTime;
        String info;
    }

    CollectionNewsListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return Common.collected.size();
    }
//TODO
// 不知道会不会调用，如果出现问题注意修改这里
    @Override
    public Object getItem(int i) {
        return Common.collected.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CollectionViewHolder viewHolder;
        CollectedItem collectedItem = Common.collected.get(position);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.news_item, parent, false);
            viewHolder = new CollectionViewHolder();
            viewHolder.itemTitle = convertView.findViewById(R.id.itemTitle);
            viewHolder.itemImage = convertView.findViewById(R.id.itemImage);
            viewHolder.itemSubtitle = convertView.findViewById(R.id.itemSubtitle);
            viewHolder.itemTime = convertView.findViewById(R.id.itemTime);
            convertView.setTag(viewHolder);
        } else viewHolder = (CollectionViewHolder) convertView.getTag();
        viewHolder.itemImage.setImageDrawable(null);
        viewHolder.itemImage.setVisibility(View.INVISIBLE);
        viewHolder.itemImage.setPadding(0, 0, 0, 0);
        viewHolder.itemImage.getLayoutParams().width = 0;
        viewHolder.itemImage.getLayoutParams().height = 0;
        viewHolder.itemTitle.setText(collectedItem.title);
        viewHolder.itemSubtitle.setText(collectedItem.subtitle);
        viewHolder.itemTime.setText(collectedItem.time);
        viewHolder.info = collectedItem.info;
        return convertView;
    }
}

public class CollectionFragment extends Fragment {
    private ListView newsList = null;
    private CollectionNewsListAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View rootView;        //根视图
        View rootView = inflater.inflate(R.layout.collection_fragment, container, false);
        newsList = rootView.findViewById(R.id.newsList);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CollectionNewsListAdapter.CollectionViewHolder viewHolder = (CollectionNewsListAdapter.CollectionViewHolder) view.getTag();
                Intent intent = new Intent(getContext(), NewsShowActivity.class);
                intent.putExtra("info", viewHolder.info);
                startActivityForResult(intent, 0);
            }
        });
        if (adapter == null) {
            adapter = new CollectionNewsListAdapter(getContext());
            newsList.setAdapter(adapter);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.changed) {
            adapter.notifyDataSetChanged();
        }
    }
}
