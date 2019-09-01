package com.java.zhangyiwei_chengjiawen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment, container, false);
        view.findViewById(R.id.clearHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.history.clear();
                ((MainActivity) getActivity()).saveData();
                view.findViewById(R.id.search_fragment).setVisibility(View.INVISIBLE);
            }
        });
        ((ListView) view.findViewById(R.id.historyList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = Common.history.get(Common.history.size() - 1 - position);
                ((EditText) getActivity().findViewById(R.id.searchText)).setText(text);
                getActivity().findViewById(R.id.searchButton).performClick();
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        LinearLayout searchFragment = view.findViewById(R.id.search_fragment);
        if (hidden) {
            searchFragment.setVisibility(View.INVISIBLE);
            return;
        }
        if (Common.history.isEmpty()) searchFragment.setVisibility(View.INVISIBLE);
        else {
            searchFragment.setVisibility(View.VISIBLE);
            ListView historyList = view.findViewById(R.id.historyList);
            ArrayList<Map<String, String>> data = new ArrayList<>();
            for (int i = Common.history.size() - 1; i >= 0; --i) {
                HashMap<String, String> map = new HashMap<>();
                map.put("historyText", Common.history.get(i));
                data.add(map);
            }
            historyList.setAdapter(new SimpleAdapter(
                    getContext(), data, R.layout.history_item,
                    new String[]{"historyText"},
                    new int[]{R.id.historyText}));
            historyList.invalidate();
        }
    }
}
