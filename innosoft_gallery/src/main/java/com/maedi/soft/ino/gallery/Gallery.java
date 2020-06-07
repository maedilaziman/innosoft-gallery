package com.maedi.soft.ino.gallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

import com.google.gson.Gson;
import com.maedi.soft.ino.base.BuildActivity;
import com.maedi.soft.ino.base.func_interface.ActivityListener;
import com.maedi.soft.ino.base.store.MapDataParcelable;
import com.maedi.soft.ino.gallery.adapter.MainAdapter;
import com.maedi.soft.ino.gallery.utils.DataStatic;
import com.maedi.soft.ino.gallery.utils.FuncHelper;
import com.maedi.soft.ino.gallery.view.CustomTabLayout;

import java.util.Map;

import timber.log.Timber;

public class Gallery extends BuildActivity<View> implements ActivityListener<Integer> {

    private final String TAG = this.getClass().getName()+"- _GALLERY_ - ";

    private FragmentActivity f;

    private ViewPager viewPager;

    private CustomTabLayout customTabLayout;

    private MainAdapter mainMenuAdapter;

    private int tabHeight = 0;

    //private Map mparcel;

    private String successSubmitData, submitData;

    @Override
    public int setPermission() {
        return DataStatic.PERMISSIONS_REQUEST_CAMERA;
    }

    @Override
    public boolean setAnalytics() {
        return false;
    }

    @Override
    public int baseContentView() {
        return R.layout.activity_gallery;
    }

    @Override
    public ActivityListener createListenerForActivity() {
        return this;
    }

    @Override
    public void onCreateActivity(Bundle savedInstanceState) {
        f = this;
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        customTabLayout = (CustomTabLayout) findViewById(R.id.tabs);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBuildActivityCreated() {
        setTabsMenuAdapter(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(customTabLayout));
        onTabSelectedListener(viewPager);
        successSubmitData = "0";
        submitData = "0";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTabsMenuAdapter(int tabPos) {
        mainMenuAdapter = new MainAdapter(f.getSupportFragmentManager(), f);
        viewPager.setAdapter(mainMenuAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(tabPos);
        customTabLayout.setupWithViewPager(viewPager);
        customTabLayout.setOnTabSelectedListener(onTabSelectedListener(viewPager));
        for (int i = 0; i < customTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = customTabLayout.getTabAt(i);
            tab.setCustomView(mainMenuAdapter.getTabView(i));
            if (i == tabPos) mainMenuAdapter.setSelectedTab(customTabLayout, i, true);
        }
    }

    public void getData(Map m)
    {
        successSubmitData = "1";
        submitData = new Gson().toJson(m);
        finish();
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityStop() {

    }

    @Override
    public void onActivityDestroy() {

    }

    @Override
    public boolean onActivityKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onActivityFinish() {
        mainMenuAdapter.onFisnish_fragment2();
    }

    @Override
    public void onActivityRestart() {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        FuncHelper.cameraRequestPermissionsResult(f, requestCode, grantResults);
        mainMenuAdapter.cameraPermissionsResult(requestCode, grantResults);
    }

    @Override
    public void onActivityMResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean onActivitySecure() {
        return false;
    }

    @Override
    public void setAnimationOnOpenActivity(Integer firstAnim, Integer secondAnim) {
        overridePendingTransition(firstAnim, secondAnim);
    }

    @Override
    public void setAnimationOnCloseActivity(Integer firstAnim, Integer secondAnim) {
        overridePendingTransition(firstAnim, secondAnim);
    }

    @Override
    public View setViewTreeObserverActivity() {
        return customTabLayout;
    }

    @Override
    public void getViewTreeObserverActivity() {
        tabHeight = customTabLayout.getMeasuredHeight();
        int h = tabHeight + (tabHeight/2);
        mainMenuAdapter.getTabHeight_fragment1(tabHeight);
        mainMenuAdapter.getTabHeight_fragment2(h);
    }

    @Override
    public Intent setResultIntent() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(DataStatic.successSubmitNewData, successSubmitData);
        resultIntent.putExtra("data", submitData);
        return resultIntent;
    }

    @Override
    public String getTagDataIntentFromActivity() {
        return null;//DataStatic.EXTRA_INTENT_GALLERY;
    }

    @Override
    public void getMapDataIntentFromActivity(MapDataParcelable parcleable) {
        //mparcel = parcleable.getMap();
    }

    @Override
    public MapDataParcelable setMapDataIntentToNextActivity(MapDataParcelable parcleable) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager viewPager) {

        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                mainMenuAdapter.setSelectedTab(customTabLayout, pos, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mainMenuAdapter.setSelectedTab(customTabLayout, tab.getPosition(), false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };
    }
}