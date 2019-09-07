package com.java.zhangyiwei_chengjiawen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {
    public static final int SEARCH = 1;
    public static final int RESULT = 2;

    private SearchFragment searchFragment;
    private NewsFragment resultFragment;

    EditText searchText;
    TextView searchButton;

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return getResources().getDimensionPixelSize(resourceId);
        else return 25;
    }

    void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setFragment(int type, String word) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (searchFragment != null) ft.hide(searchFragment);
        if (resultFragment != null) ft.hide(resultFragment);
        if (type == SEARCH)
            if (searchFragment == null) {
                searchFragment = new SearchFragment();
                ft.add(R.id.fragmentContainer, searchFragment);
            } else ft.show(searchFragment);
        else if (type == RESULT)
            if (resultFragment == null) {
                resultFragment = new NewsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "");
                bundle.putString("word", word);
                resultFragment.setArguments(bundle);
                ft.add(R.id.fragmentContainer, resultFragment);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("type", "");
                bundle.putString("word", word);
                resultFragment.setArguments(bundle);
                ft.show(resultFragment);
            }
        ft.commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_activity);

        //Translucent status bar
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            LinearLayout statusBar = findViewById(R.id.statusBar);
            ViewGroup.LayoutParams params = statusBar.getLayoutParams();
            params.height = getStatusBarHeight();
            statusBar.setLayoutParams(params);
        }

        //Search box
        searchText = findViewById(R.id.searchText);
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    setFragment(SEARCH, null);
                else hideKeyboard(v);
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                    searchButton.performClick();
                return false;
            }
        });
        searchText.requestFocus();
        showKeyboard(searchText);

        //Search Button
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                searchText.clearFocus();
                String text = searchText.getText().toString();
                if (text.equals("")) {
                    text = getResources().getString(R.string.searchBoxText);
                    searchText.setText(text);
                }
                Common.history.remove(text);
                if (Common.history.size() < 10)
                    Common.history.add(text);
                setFragment(RESULT, text);
                Common.saveData(SearchActivity.this);
            }
        });

        findViewById(R.id.backIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //New/Get fragment
        //Set default fragment to content
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
            if (fragment != null) {
                ft.remove(fragment);
                fm.popBackStack();
                ft.commit();
            }
            setFragment(SEARCH, null);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
