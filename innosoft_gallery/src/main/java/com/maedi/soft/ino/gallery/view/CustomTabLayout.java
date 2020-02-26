package com.maedi.soft.ino.gallery.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.maedi.soft.ino.base.utils.ScreenSize;
import com.maedi.soft.ino.gallery.R;

import java.lang.reflect.Field;

public class CustomTabLayout extends TabLayout {

    private static final int WIDTH_INDEX = 0;
    private static int DIVIDER_FACTOR = 4;
    private static final String SCROLLABLE_TAB_MIN_WIDTH = "mScrollableTabMinWidth";

    public CustomTabLayout(Context context) {
        super(context);
        initTabMinWidth();
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTabs, 0, 0
        );
        int countTab = attr.getInt(R.styleable.CustomTabs_countTab, 1);
        DIVIDER_FACTOR = countTab;
        initTabMinWidth();
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTabMinWidth();
    }

    private void initTabMinWidth() {
        //int[] wh = WindowScreen.getScreenSize(getContext());
        //int tabMinWidth = wh[WIDTH_INDEX] / DIVIDER_FACTOR;
        int tabMinWidth = ScreenSize.instance(getContext()).getWidth() / DIVIDER_FACTOR;

        Field field;
        try {
            field = TabLayout.class.getDeclaredField(SCROLLABLE_TAB_MIN_WIDTH);
            field.setAccessible(true);
            field.set(this, tabMinWidth);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup tabLayout = (ViewGroup)getChildAt(0);

        int childCount = tabLayout.getChildCount();

        int widths[] = new int[childCount+1];

        for(int i = 0; i < childCount; i++){
            widths[i] = tabLayout.getChildAt(i).getMeasuredWidth();
            widths[childCount] += widths[i];
        }

        int measuredWidth = getMeasuredWidth();
        for(int i = 0; i < childCount; i++){
            tabLayout.getChildAt(i).setMinimumWidth(measuredWidth*widths[i]/widths[childCount]);
        }

    }

}