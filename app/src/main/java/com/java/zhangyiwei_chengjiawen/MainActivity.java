package com.java.zhangyiwei_chengjiawen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/*TODO
 * 存储
 *
 * */

public class MainActivity extends AppCompatActivity {
    private int current = 0;
    public static final int CONTENT = 0;
    public static final int SEARCH = 1;
    public static final int RESULT = 2;

    private ContentFragment contentFragment;
    private SearchFragment searchFragment;
    private NewsFragment resultFragment;

    private ImageView searchIcon;
    TransitionDrawable td;

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

    @Override
    public void onBackPressed() {
        if (current == CONTENT) super.onBackPressed();
        else {
            setFragment(CONTENT, "");
            findViewById(R.id.searchText).clearFocus();
            td.reverseTransition(200);
        }
    }

    public void setFragment(int type, String word) {
        current = type;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (contentFragment != null) ft.hide(contentFragment);
        if (searchFragment != null) ft.hide(searchFragment);
        if (resultFragment != null) ft.hide(resultFragment);
        if (type == CONTENT)
            if (contentFragment == null) {
                contentFragment = new ContentFragment();
                ft.add(R.id.fragmentContainer, contentFragment);
            } else ft.show(contentFragment);
        else if (type == SEARCH)
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

    void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    boolean saveData() {
        boolean flag = true;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput("data", MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            Object[] objects = {Common.history, Common.added, Common.deleted};
            oos.writeObject(objects);
        } catch (Exception e) {
            flag = false;
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (Exception e) {
                    flag = false;
                }
            else flag = false;
            if (oos != null)
                try {
                    oos.close();
                } catch (Exception e) {
                    flag = false;
                }
            else flag = false;
        }
        return flag;
    }

    boolean loadData() {
        boolean flag = true;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput("data");
            ois = new ObjectInputStream(fis);
            Object[] objects = (Object[]) ois.readObject();
            Common.history = (ArrayList<String>) objects[0];
            Common.added = (ArrayList<Integer>) objects[1];
            Common.deleted = (ArrayList<Integer>) objects[2];
        } catch (Exception e) {
            flag = false;
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (Exception e) {
                    flag = false;
                }
            if (ois != null)
                try {
                    ois.close();
                } catch (Exception e) {
                    flag = false;
                }
        }
        return flag;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        loadData();

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        //Translucent status bar
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            LinearLayout statusBar = findViewById(R.id.statusBar);
            ViewGroup.LayoutParams params = statusBar.getLayoutParams();
            params.height = getStatusBarHeight();
            statusBar.setLayoutParams(params);
        }

        //Search Button
        final TextView searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                //Pass text
                EditText searchText = findViewById(R.id.searchText);
                searchText.clearFocus();
                String text = searchText.getText().toString();
                if (text.equals("")) {
                    text = getResources().getString(R.string.searchBoxText);
                    searchText.setText(text);
                }
                Common.history.remove(text);
                if (Common.history.size() < 10)
                    Common.history.add(text);
                saveData();
                if (current == CONTENT)
                    td.startTransition(200);
                setFragment(RESULT, text);
            }
        });

        //Search box
        final EditText searchText = findViewById(R.id.searchText);
        searchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (current == CONTENT)
                    td.startTransition(200);
                setFragment(SEARCH, null);
                v.performClick();
                return false;
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchButton.performClick();
                }
                return false;
            }
        });
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    hideKeyboard(v);
            }
        });

        //Search icon
        td = new TransitionDrawable(new Drawable[]{
                ContextCompat.getDrawable(this, R.mipmap.search_icon),
                ContextCompat.getDrawable(this, R.mipmap.back)
        });
        td.setCrossFadeEnabled(true);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setImageDrawable(td);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current == CONTENT)
                    searchText.performClick();
                else {
                    setFragment(CONTENT, "");
                    findViewById(R.id.searchText).clearFocus();
                    td.reverseTransition(200);
                }
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
            setFragment(CONTENT, null);
        }
    }
}