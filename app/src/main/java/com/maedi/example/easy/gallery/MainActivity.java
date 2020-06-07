package com.maedi.example.easy.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.maedi.soft.ino.base.BuildActivity;
import com.maedi.soft.ino.base.func_interface.ActivityListener;
import com.maedi.soft.ino.base.store.MapDataParcelable;
import com.maedi.soft.ino.gallery.Gallery;
import com.maedi.soft.ino.gallery.model.ListObject;
import com.maedi.soft.ino.gallery.utils.DataStatic;
import com.maedi.soft.ino.gallery.view.GridSpacingItemDecoration;

import java.io.File;
import java.util.Map;

import adapter.GalleryAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends BuildActivity<View> implements ActivityListener<Integer> {

    private final String TAG = this.getClass().getName() +"- MAIN_ACTIVITY - ";

    private FragmentActivity f;

    private int req_gallery = 105;

    private final int VERTICAL_ITEM_SPACE = 10;

    private GalleryAdapter adapter;

    private ListObject listImages;

    @BindView(R.id.listView)
    RecyclerView listView;

    @OnClick(R.id.post1)
    public void OpenGellery() {
        Intent intent = new Intent(f, Gallery.class);
        startActivityForResult(intent, req_gallery);
    }

    @Override
    public int baseContentView() {
        return R.layout.activity_main;
    }

    @Override
    public ActivityListener createListenerForActivity() {
        return this;
    }

    @Override
    public void onCreateActivity(Bundle savedInstanceState) {
        f = this;
        ButterKnife.bind(this);
    }

    @Override
    public void onBuildActivityCreated() {
        listView.addItemDecoration(new GridSpacingItemDecoration(4, VERTICAL_ITEM_SPACE, true, 0));
        LinearLayoutManager layoutManager = new GridLayoutManager(f, 4);
        listView.setLayoutManager(layoutManager);
        listView.setNestedScrollingEnabled(false);
        listImages = new ListObject();
        adapter = new GalleryAdapter(f, R.layout.list_gallery, listImages);
        listView.setAdapter(adapter);
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

    }

    @Override
    public void onActivityMResult(int requestCode, int resultCode, Intent data) {
        Timber.d(TAG+"RequestCode - "+requestCode + " | resultCode - " +resultCode);
        switch (requestCode) {
            case DataStatic.REQUEST_INTENT_GALLERY:
                if (null != data)
                {
                    String successData = data.getStringExtra(DataStatic.successSubmitNewData);
                    if(null != successData) {
                        if (successData.equalsIgnoreCase("1")) {
                            String submitData = data.getStringExtra("data");
                            Map m = new Gson().fromJson(submitData, Map.class);
                            listImages.clear();
                            ListObject nwListImages = new ListObject();
                            for(Object o : m.keySet())
                            {
                                String val = (String) m.get(o);
                                //Timber.d(TAG+"VAL_IMAGE - "+val);
                                String[] arrVal = val.split("\\|");
                                Timber.d(TAG+"IMAGE_IS - "+arrVal[0]);
                                nwListImages.add(new ListObject(arrVal[0]));
                            }

                            adapter.addAll(nwListImages);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean onActivitySecure() {
        return false;
    }

    @Override
    public int setPermission() {
        return 0;
    }

    @Override
    public boolean setAnalytics() {
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
        return null;
    }

    @Override
    public void getViewTreeObserverActivity() {

    }

    @Override
    public Intent setResultIntent() {
        return null;
    }

    @Override
    public String getTagDataIntentFromActivity() {
        return null;
    }

    @Override
    public void getMapDataIntentFromActivity(MapDataParcelable parcleable) {

    }

    @Override
    public MapDataParcelable setMapDataIntentToNextActivity(MapDataParcelable parcleable) {
        return null;
    }

}
