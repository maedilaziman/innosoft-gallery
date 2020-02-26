package com.maedi.soft.ino.gallery.adapter;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.maedi.soft.ino.gallery.R;
import com.maedi.soft.ino.gallery.fragment.Cameras;
import com.maedi.soft.ino.gallery.fragment.Gallery;
import com.maedi.soft.ino.gallery.model.ListObject;

import timber.log.Timber;

public class MainAdapter extends FragmentStatePagerAdapter {

    private FragmentActivity f;

    private Gallery fragment1;

    private Cameras fragment2;

    public interface CommMainAdapter_Fragment1
    {
        void cameraPermissionsResult(int reqCode, int[] grantResults);

        void getTabHeight(int t);
    }

    private CommMainAdapter_Fragment1 listenerFragment1;

    public void setListener_Fragment1(CommMainAdapter_Fragment1 listenFragment1)
    {
        this.listenerFragment1 = listenFragment1;
    }

    public interface CommMainAdapter_Fragment2
    {
        void onFinishAct();
    }

    private CommMainAdapter_Fragment2 listenerFragment2;

    public void setListener_Fragment2(CommMainAdapter_Fragment2 listenFragment2)
    {
        this.listenerFragment2 = listenFragment2;
    }

    public MainAdapter(FragmentManager supportFragmentManager, FragmentActivity f) {
        super(supportFragmentManager);
        this.f = f;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                fragment1 = Gallery.newInstance();
                return fragment1;
            case 1:
                fragment2 = Cameras.newInstance();
                return fragment2;
            default: return Gallery.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try {
            super.restoreState(state, loader);
        } catch (Exception e) {
            Timber.d("Error_Restore_State of Fragment : " + e.getLocalizedMessage());
        }
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(f).inflate(R.layout.custom_tablayout, null);
        TextView tv = (TextView) v.findViewById(R.id.text1);
        tv.setText(ListObject.MainMenuTabsTitle(f)[position]);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setSelectedTab(TabLayout tabLayout, int position, boolean selected){
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View view = tab.getCustomView();
        if(null != view) {
            TextView iv_text = (TextView) view.findViewById(R.id.text1);
            if (selected) {
                iv_text.setTextColor(f.getColor(R.color.tab_on));
            } else {
                iv_text.setTextColor(f.getColor(R.color.tab_off));
            }
        }
    }

    private void regFragmentListener_a(int c, Fragment fragment, int t, int[] t2)
    {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (c)
                {
                    case 0:
                        setListener_Fragment1((CommMainAdapter_Fragment1) fragment);
                        listenerFragment1.getTabHeight(t);
                        break;
                    case 1:
                        setListener_Fragment2((CommMainAdapter_Fragment2) fragment);
                        listenerFragment2.onFinishAct();
                        break;
                    case 2:
                        setListener_Fragment1((CommMainAdapter_Fragment1) fragment);
                        listenerFragment1.cameraPermissionsResult(t, t2);
                        break;
                }
            }
        }, 200);
    }

    public void cameraPermissionsResult(int reqCode, int[] grantResults)
    {
        if(null == fragment1)fragment1 = Gallery.newInstance();
        regFragmentListener_a(2, fragment1, reqCode, grantResults);
    }

    public void getTabHeight_fragment1(int t)
    {
        if(null == fragment1)fragment1 = Gallery.newInstance();
        regFragmentListener_a(0, fragment1, t, null);
    }

    public void getTabHeight_fragment2(int t)
    {
        if(null == fragment2)fragment2 = Cameras.newInstance();
        regFragmentListener_a(0, fragment2, t, null);
    }

    public void onFisnish_fragment2()
    {
        if(null == fragment2)fragment2 = Cameras.newInstance();
        regFragmentListener_a(1, fragment2, 0, null);
    }
}