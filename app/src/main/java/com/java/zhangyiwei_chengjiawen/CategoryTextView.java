package com.java.zhangyiwei_chengjiawen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CategoryTextView extends AppCompatTextView {
    private int index;
    private boolean available;

    public CategoryTextView(Context context) {
        super(context);
    }

    public CategoryTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CategoryTextView(final Context context, final int index) {
        super(context);
        this.index = index;
        available = true;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(MainActivity.dpToPx(getContext(), getResources().getInteger(R.integer.categoryTextPadding)));
        params.setMarginEnd(MainActivity.dpToPx(getContext(), getResources().getInteger(R.integer.categoryTextPadding)));
        setLayoutParams(params);
        setTextSize(getResources().getInteger(R.integer.textSize));
        setText(Common.category[index]);
        setClickable(true);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == Common.added.get(Common.currentItem)) return;
                int previous = Common.currentItem;
                Common.currentItem = Common.added.indexOf(index);
                LinearLayout categoryMenu = (LinearLayout) v.getParent();
                categoryMenu.getChildAt(previous).invalidate();
                invalidate();
                ((ViewPager) ((Activity) context).findViewById(R.id.newsViewPaper)).setCurrentItem(Common.currentItem, false);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!available) return;
        if (index == Common.added.get(Common.currentItem))
            setTextColor(ContextCompat.getColor(getContext(), R.color.categoryTextSelected));
        else setTextColor(ContextCompat.getColor(getContext(), R.color.categoryTextNotSelected));
        super.onDraw(canvas);
    }
}
