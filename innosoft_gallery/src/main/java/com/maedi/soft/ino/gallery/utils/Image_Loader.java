package com.maedi.soft.ino.gallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.maedi.soft.ino.gallery.func_interface.CommGallery;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class Image_Loader implements CommGallery.LoadImage {

    private ImageLoader imageLoader;

    private PauseOnScrollListener pauseOnScrollListener;

    private FragmentActivity f;

    private LinearLayout.LayoutParams layoutParamsImage;

    private ImageView imageView;

    public Image_Loader(FragmentActivity f)
    {
        this.f = f;
        initImageLoader(f);
    }

    protected void initImageLoader(FragmentActivity f)
    {
        //default resize image
        int wimg = 280, himg = 280;
        layoutParamsImage = new LinearLayout.LayoutParams(wimg, himg);
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.innosoft_temp";
            new File(CACHE_DIR).mkdirs();
            File cacheDir = StorageUtils.getOwnCacheDirectory(f, CACHE_DIR);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true) // default false
                    .cacheInMemory(false) // default true
                    .cacheOnDisk(true) // default false
                    .postProcessor(new BitmapProcessor() {
                        @Override
                        public Bitmap process(Bitmap bmp) {
                            return Bitmap.createScaledBitmap(bmp, wimg, himg, false);
                        }
                    })
                    .considerExifParams(false) // default
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                    .displayer(new SimpleBitmapDisplayer()) // default
                    .handler(new Handler()) // default
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(f)
                    .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                    .diskCacheExtraOptions(480, 800, null)
                    .threadPoolSize(2) // default 3
                    .threadPriority(Thread.NORM_PRIORITY - 2) // default
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                    .imageDownloader(new BaseImageDownloader(f)) // default
                    .defaultDisplayImageOptions(options).memoryCache(new WeakMemoryCache())
                    .build();

            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

            pauseOnScrollListener = new PauseOnScrollListener(imageLoader, true, true);

        } catch (Exception e) {

        }
    }

    @Override
    public void setImage(Object t1, Object t2) {
        imageView = (ImageView) t2;
        imageView.setLayoutParams(layoutParamsImage);
        imageLoader.displayImage(
                "file://" + t1,
                imageView,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View arg1,
                                                  Bitmap bmp) {
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                    }
                });
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public RecyclerView setListView(Object t1) {
        RecyclerView recyclerView = (RecyclerView) t1;
        recyclerView.addOnScrollListener(pauseOnScrollListener);
        return recyclerView;
    }
}
