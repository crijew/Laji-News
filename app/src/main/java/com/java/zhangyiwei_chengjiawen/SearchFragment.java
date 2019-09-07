package com.java.zhangyiwei_chengjiawen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

class HistoryListAdapter extends BaseAdapter {
    private Context context;
    private View historyTitle;

    private class ViewHolder {
        private TextView historyText;
        private TextView deleteHistory;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (getCount() == 0)
            historyTitle.setVisibility(View.GONE);
        else
            historyTitle.setVisibility(View.VISIBLE);
    }

    HistoryListAdapter(Context context, View historyTitle) {
        this.context = context;
        this.historyTitle = historyTitle;
    }

    @Override
    public int getCount() {
        return Common.history.size();
    }

    @Override
    public Object getItem(int position) {
        return Common.history.get(getCount() - 1 - position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final String data = (String) getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.historyText = convertView.findViewById(R.id.historyText);
            viewHolder.deleteHistory = convertView.findViewById(R.id.deleteHistory);
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.historyText.setText(data);
        viewHolder.deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.history.remove(getCount() - 1 - position);
                Common.saveData(context);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}

public class SearchFragment extends Fragment {
    View rootView;
    View historyTitle;
    ListView historyList;
    HistoryListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_fragment, container, false);

        historyTitle = rootView.findViewById(R.id.history_title);
        adapter = new HistoryListAdapter(getContext(), historyTitle);
        rootView.findViewById(R.id.clearHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.history.clear();
                adapter.notifyDataSetChanged();
                Common.saveData(getContext());
                historyTitle.setVisibility(View.GONE);
            }
        });

        historyList = rootView.findViewById(R.id.historyList);
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) adapter.getItem(position);
                ((EditText) getActivity().findViewById(R.id.searchText)).setText(text);
                getActivity().findViewById(R.id.searchButton).performClick();
            }
        });
        historyList.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
            adapter.notifyDataSetChanged();
    }
}
