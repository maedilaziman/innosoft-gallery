package com.maedi.soft.ino.gallery.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.maedi.soft.ino.base.BaseFragment;
import com.maedi.soft.ino.base.utils.DataUtility;
import com.maedi.soft.ino.gallery.R;
import com.maedi.soft.ino.gallery.adapter.FormSpinnerAdapter;
import com.maedi.soft.ino.gallery.adapter.Galley_Adt;
import com.maedi.soft.ino.gallery.adapter.MainAdapter;
import com.maedi.soft.ino.gallery.func_interface.CommGallery;
import com.maedi.soft.ino.gallery.model.AlbumGallery;
import com.maedi.soft.ino.gallery.model.ListObject;
import com.maedi.soft.ino.gallery.model.PhotoGallery;
import com.maedi.soft.ino.gallery.utils.Image_Loader;
import com.maedi.soft.ino.gallery.view.CircleLayoutLinear;
import com.maedi.soft.ino.gallery.view.GridSpacingItemDecoration;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class Gallery extends BaseFragment implements Galley_Adt.CommGallery_Adt, MainAdapter.CommMainAdapter_Fragment1 {

    private final String TAG = this.getClass().getName()+"- GALLERY_FRAGMENT - ";

    private FragmentActivity f;

    private final int VERTICAL_ITEM_SPACE = 10;

    private Galley_Adt galleryAdapter;

    public final int PERMISSION_ACCESS_READ_EXTERNAL_STORAGE = 10;

    private CommGallery.LoadImage listenLoadImage;

    private ListObject listOfAllImages;

    private RecyclerView listView;

    private Spinner spinner;

    private FormSpinnerAdapter spinnerAdapter;

    private LinearLayout.LayoutParams params;

    private LinearLayout layoutListGallery;

    private ArrayList<AlbumGallery> albumGalleries;

    private ArrayList<String> albumsNames;

    private ArrayList<PhotoGallery> albumsPhotos;

    private int countImageChoosed = 1;

    private ArrayList<String> photoChoosed;

    private LinearLayout layoutButtonDone;

    private Map tempPhoto;

    public static Gallery newInstance() {
        Gallery gallery = new Gallery();
        return gallery;
    }

    @Override
    public int baseContentView() {
        return R.layout.fragment_gallery;
    }

    @Override
    public void onCreateMView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View v) {
        init(v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewMCreated(View v, Bundle savedInstanceState) {
        listenLoadImage = new Image_Loader(f);
        listenLoadImage.setListView(listView);
        listOfAllImages = new ListObject();

        galleryAdapter = new Galley_Adt(f, R.layout.list_item_gallery, this, listOfAllImages);
        listView.setAdapter(galleryAdapter);

        if (ContextCompat.checkSelfPermission(f, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(f, "Access for read external storage",
                    PERMISSION_ACCESS_READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        else
        {
            initGallery();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                for ( AlbumGallery album : albumGalleries ) {
                    if (album.getName().replaceAll("\\s", "").equalsIgnoreCase(spinner.getSelectedItem().toString().replaceAll("\\s", ""))) {
                        listOfAllImages.clear();
                        tempPhoto.clear();
                        countImageChoosed = 1;
                        //actualPhotoGallery = album.getAlbumPhotos();
                        for(PhotoGallery p : album.getAlbumPhotos())
                        {
                            listOfAllImages.add(new ListObject(p.getPhotoUri(), getRandomNumber(), 0));
                            Timber.d(TAG+p.getPhotoUri() + " | " +album.getName() + " | " + spinner.getSelectedItem().toString().replaceAll("\\s", ""));
                        }
                        galleryAdapter.addAll(listOfAllImages);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private void init(View v)
    {
        listView = (RecyclerView) v.findViewById(R.id.listView);
        spinner = (Spinner) v.findViewById(R.id.spinner);
        layoutListGallery = (LinearLayout) v.findViewById(R.id.layout_list_gallery);
        layoutButtonDone = (LinearLayout) v.findViewById(R.id.button_done);

        listView.addItemDecoration(new GridSpacingItemDecoration(4, VERTICAL_ITEM_SPACE, true, 0));
        LinearLayoutManager layoutManager = new GridLayoutManager(f, 4);
        listView.setLayoutManager(layoutManager);
        listView.setNestedScrollingEnabled(false);

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        albumGalleries = new ArrayList<>();
        albumsNames = new ArrayList<>();
        photoChoosed = new ArrayList<>();
        tempPhoto = new HashMap<>();

        layoutButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.maedi.soft.ino.gallery.Gallery gly = (com.maedi.soft.ino.gallery.Gallery) f;
                gly.getData(tempPhoto);
            }
        });
    }

    private void initGallery()
    {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                listOfAllImages = getGalleryPhotos();
                galleryAdapter.addAll(listOfAllImages);
            }
        }, 150);
    }

    private ListObject getGalleryPhotos()
    {
        ListObject listOfImages = new ListObject();
        Uri uri;
        int column_image_uri, column_image_id, column_bucket_name;
        String bucketName, imageId;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        final String[] columns = { MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imagecursor = f.getContentResolver().query(uri, columns, null, null, orderBy);

        if (imagecursor != null && imagecursor.getCount() > 0) {
            imagecursor.moveToFirst();
            while (imagecursor.moveToNext()) {
                column_image_uri = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                column_image_id = imagecursor.getColumnIndex(MediaStore.Images.Media._ID );
                column_bucket_name = imagecursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                imageId = imagecursor.getString(column_image_id);
                bucketName = imagecursor.getString(column_bucket_name);
                absolutePathOfImage = imagecursor.getString(column_image_uri);

                PhotoGallery photo = new PhotoGallery();
                photo.setAlbumName(bucketName);
                photo.setPhotoUri(absolutePathOfImage);
                photo.setId( Integer.valueOf(imageId));

                if ( albumsNames.contains( bucketName ) ) {
                    for ( AlbumGallery album : albumGalleries ) {
                        if ( album.getName().equalsIgnoreCase( bucketName ) ) {
                            album.getAlbumPhotos().add( photo );
                            break;
                        }
                    }
                }
                else {
                    albumsPhotos = new ArrayList<>();
                    AlbumGallery album = new AlbumGallery();
                    album.setId( photo.getId() );
                    album.setName( bucketName );
                    album.setCoverUri( photo.getPhotoUri() );
                    album.setAlbumPhotos(albumsPhotos);
                    album.getAlbumPhotos().add( photo );

                    albumGalleries.add( album );
                    albumsNames.add( bucketName );
                }

                listOfImages.add(new ListObject(absolutePathOfImage, getRandomNumber(), 0));
            }
        }
        imagecursor.close();

        Collections.reverse(listOfImages);

        spinnerAdapter = new FormSpinnerAdapter(f, R.layout.form_spinner_item, albumsNames);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);

        return listOfImages;
    }

    private int getRandomNumber(){
        SecureRandom random = new SecureRandom();

        char[] CHARSET_AZ_09 = "012345678912345".toCharArray();
        char[] result = new char[9];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }

        String resRandom = new String(result);

        return Integer.parseInt(resRandom);
    }

    @Override
    public void setMUserVisibleHint(boolean isVisibleToUser) {
    }

    @Override
    public void onMAttach(Context context) {
        if (context instanceof Activity) {
            f = (FragmentActivity) context;
        }
    }

    @Override
    public void onMDetach() {
        if(null != f)f = null;
    }

    @Override
    public void onMStop() {

    }

    @Override
    public void onMDestroy() {

    }

    @Override
    public void onMDestroyView() {

    }

    @Override
    public void onMRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    @Override
    public void onMActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void setImage(String uri, View view) {
        listenLoadImage.setImage(uri, view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void chooseImage(int id, int position, String uri, int sequence, View v1, View v2, ListObject listObj) {
        CircleLayoutLinear cl = (CircleLayoutLinear) v1;
        TextView t = (TextView) v2;
        int seq = 0;
        if(cl.getBackgroundColor() == f.getColor(R.color.blue_ocean))
        {
            cl.setBackgroundColor(f.getColor(R.color.em_white_54));
            t.setTextColor(Color.BLACK);
            t.setText("");
            if(countImageChoosed > 1)countImageChoosed--;
            tempPhoto.remove(String.valueOf(position));
            galleryAdapter.updateListByIndex(position, new ListObject(listObj.s1, listObj.i1, 0));
            Iterator iterate = tempPhoto.entrySet().iterator();
            while (iterate.hasNext()) {
                Map.Entry it = (Map.Entry) iterate.next();
                String[] s = it.getValue().toString().split("\\|");
                int nc = sequence < Integer.parseInt(s[2]) ? Integer.parseInt(s[2])-1 : Integer.parseInt(s[2]);
                galleryAdapter.updateListByIndex(Integer.parseInt(it.getKey().toString()), new ListObject(s[0], Integer.parseInt(s[1]), nc));
                String data2 = s[0] + "|" + s[1] + "|" + nc;
                tempPhoto.put(it.getKey().toString(), data2);
            }
        }
        else {
            cl.setBackgroundColor(f.getColor(R.color.blue_ocean));
            t.setTextColor(Color.WHITE);
            t.setText(String.valueOf(countImageChoosed));
            String data = id + "|" + countImageChoosed;
            photoChoosed.add(data);
            String data2 = uri + "|" + id + "|" + countImageChoosed;
            tempPhoto.put(String.valueOf(position), data2);
            Iterator iterate = tempPhoto.entrySet().iterator();
            while (iterate.hasNext()) {
                Map.Entry it = (Map.Entry) iterate.next();
                String[] s = it.getValue().toString().split("\\|");
                galleryAdapter.updateListByIndex(Integer.parseInt(it.getKey().toString()), new ListObject(s[0], Integer.parseInt(s[1]), Integer.parseInt(s[2])));
            }

            countImageChoosed++;
        }
    }

    @Override
    public void cameraPermissionsResult(int requestCode, int[] grantResults) {
        if(requestCode == PERMISSION_ACCESS_READ_EXTERNAL_STORAGE ||
                requestCode == DataUtility.PERMISSIONS_REQUEST_CAMERA_ABOVE6)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGallery();
            }
        }
    }

    @Override
    public void getTabHeight(int t) {
        params.setMargins(0, 0, 0, t);//l,t,r,b
        layoutListGallery.setLayoutParams(params);
    }
}