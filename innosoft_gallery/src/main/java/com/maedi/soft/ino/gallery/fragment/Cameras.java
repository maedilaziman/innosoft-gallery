package com.maedi.soft.ino.gallery.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.maedi.soft.ino.base.BaseFragment;
import com.maedi.soft.ino.gallery.R;
import com.maedi.soft.ino.gallery.adapter.MainAdapter;
import com.maedi.soft.ino.gallery.adapter.RecyclerPreviewPhotoCamera;
import com.maedi.soft.ino.gallery.model.ListObject;
import com.maedi.soft.ino.gallery.utils.DataStatic;
import com.maedi.soft.ino.gallery.utils.FuncHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Cameras extends BaseFragment implements MainAdapter.CommMainAdapter_Fragment1, MainAdapter.CommMainAdapter_Fragment2, Camera.PictureCallback, SurfaceHolder.Callback, RecyclerPreviewPhotoCamera.CommPreviewPhoto {

    private final String TAG = this.getClass().getName()+"- CAMERAS - ";

    private SurfaceView mCameraPreview;

    private Button btnCaptureImage;

    private RelativeLayout.LayoutParams params;

    private LinearLayout layoutCapture, layoutPin, layoutButtonDone;

    private RecyclerView listView;

    private ImageView rotateCamera, imagePin;

    private ProgressBar progressBar;

    private RecyclerPreviewPhotoCamera previewCameraAdapter;

    private FragmentActivity f;

    private Camera mCamera;

    private byte[] mCameraData;

    private boolean mIsCapturing;

    private Map<String, String> mPathResultImage;

    private int mkey;

    private ListObject listObject;

    private int currentCameraId = 0;

    private Map tempPhoto;

    private View.OnClickListener startCaptureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage();
        }
    };

    private View.OnClickListener rotateCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setCameras();
        }
    };

    private View.OnClickListener pinListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(imagePin.getTag().equals("0")) hideListImage();
            else showListImage();

        }
    };

    private void hideListImage()
    {
        imagePin.setTag("1");
        FuncHelper.hideOverlay(f, listView, null);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                imagePin.setRotation(0);
            }
        }, 500);
    }

    private void showListImage()
    {
        imagePin.setTag("0");
        FuncHelper.showOverlay(f, listView, null);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                imagePin.setRotation(180);
            }
        }, 500);
    }

    public static Cameras newInstance() {
        Cameras cameras = new Cameras();
        return cameras;
    }

    @Override
    public int baseContentView() {
        return R.layout.fragment_camera;
    }

    @Override
    public void onCreateMView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View v) {
        mCameraPreview = (SurfaceView) v.findViewById(R.id.preview_view);
        btnCaptureImage = (Button) v.findViewById(R.id.capture_image_button);
        layoutCapture = (LinearLayout) v.findViewById(R.id.layout_capture);
        layoutPin = (LinearLayout) v.findViewById(R.id.layout_pin);
        listView = (RecyclerView) v.findViewById(R.id.listView);
        rotateCamera = (ImageView) v.findViewById(R.id.rotate_camera);
        imagePin = (ImageView) v.findViewById(R.id.image_pin);
        layoutButtonDone = (LinearLayout) v.findViewById(R.id.button_done);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(f, LinearLayoutManager.HORIZONTAL, false);
        listView.setLayoutManager(layoutManager);

        listObject = new ListObject();
        previewCameraAdapter = new RecyclerPreviewPhotoCamera(f, f, R.layout.list_preview_photo_camera, listObject, this, "");
        listView.setAdapter(previewCameraAdapter);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnCaptureImage.setOnClickListener(startCaptureListener);
        rotateCamera.setOnClickListener(rotateCameraListener);
        layoutPin.setOnClickListener(pinListener);
        init();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewMCreated(View v, Bundle savedInstanceState) {
    }

    private void init()
    {
        mPathResultImage = new HashMap<String, String>();
        mkey = 0;
        mIsCapturing = true;
        imagePin.setTag("0");
        tempPhoto = new HashMap<>();
        SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        setCameras();

        layoutButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.maedi.soft.ino.gallery.Gallery gly = (com.maedi.soft.ino.gallery.Gallery) f;
                gly.getData(tempPhoto);
            }
        });
    }

    private void setCameras()
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;

        }

        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCamera = Camera.open(currentCameraId);

        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();

                parameters.set("orientation", "portrait");
                parameters.set("rotation", 90);

                List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
                int chosenSize = FuncHelper.getPictureSizeIndexForHeight(sizeList, 800);
                parameters.setPictureSize(sizeList.get(chosenSize).width, sizeList.get(chosenSize).height);

                mCamera.setParameters(parameters);

                //mCamera.setDisplayOrientation(90);
                setCameraDisplayOrientation(f, currentCameraId, mCamera);

                mCamera.setPreviewDisplay(mCameraPreview.getHolder());

                if (mIsCapturing) {
                    mCamera.startPreview();
                }

            }
            catch (Exception e) {
                Toast.makeText(f, "Cannot open cameras!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void captureImage() {
        if(null != mCamera) {
            setEnableOrDisabled(btnCaptureImage, false);
            progressBar.setVisibility(View.VISIBLE);
            mCamera.takePicture(null, null, this);
        }
    }

    private void setEnableOrDisabled(View e, boolean enabled)
    {
        e.setClickable(enabled);
        //e.setFocusable(enabled);
        //e.setFocusableInTouchMode(enabled);
    }

    private void setupImageDisplay() {

        if(null != mCamera) {
            mCamera.stopPreview();
            mCamera.startPreview();

            Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
            //mCameraImage.setImageBitmap(bitmap);

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            Timber.d(TAG+"rotation_orientation config.orientation - "+getResources().getConfiguration().orientation+" | "+Configuration.ORIENTATION_PORTRAIT);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Notice that width and height are reversed
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, screenHeight, screenWidth, true);
                int w = scaled.getWidth();
                int h = scaled.getHeight();
                // Setting post rotate to 90
                Matrix mtx = new Matrix();

                boolean cameraFront = true;
                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) cameraFront = false;

                int CameraEyeValue = setPhotoOrientation(f, cameraFront == true ? 1 : 0); // CameraID = 1 : front 0:back
                if (cameraFront) { // As Front camera is Mirrored so Fliping the Orientation
                    if (CameraEyeValue == 270) {
                        mtx.postRotate(90);
                    } else if (CameraEyeValue == 90) {
                        mtx.postRotate(270);
                    }
                } else {
                    mtx.postRotate(CameraEyeValue); // CameraEyeValue is default to Display Rotation
                }

                bitmap = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
            }

            String strImageName = "";

            if (Build.VERSION.SDK_INT >= 22) {
                strImageName = saveImageAPI23(bitmap);
            } else strImageName = saveImageAPIOlders(bitmap);

            listObject.add(new ListObject(strImageName, bitmap));
            previewCameraAdapter.notifyDataSetChanged();
            listView.smoothScrollToPosition(previewCameraAdapter.getItemCount() - 1);
            setEnableOrDisabled(btnCaptureImage, true);
            progressBar.setVisibility(View.GONE);
            layoutPin.setVisibility(View.VISIBLE);
            if(imagePin.getTag().equals("1"))
            {
                showListImage();
            }

            tempPhoto.clear();
            int x = 0;
            for(ListObject o : listObject)
            {
                String s = (String) o.s1;
                String data2 = s + "|0|" + x;
                tempPhoto.put(""+x, data2);
                x++;
            }

        }
    }

    private int setPhotoOrientation(FragmentActivity activity, int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        // do something for phones running an SDK before lollipop
        if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            Timber.d(TAG+"rotation_orientation info.orientation - "+info.orientation);
            result = (info.orientation - degrees + 360) % 360;
        }

        Timber.d(TAG+"rotation_orientation result - "+result);
        return result;
    }

    private String saveImageAPIOlders(Bitmap bitmap){
        String fname = FuncHelper.getRandomString("innosoft_gallery_")+ ".png";
        String folderImageName = "innosoft_gallery";
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + folderImageName);
        if(!myDir.exists())myDir.mkdirs();
        File filedir = new File(myDir, fname);
        String imagePath = filedir.toString();

        File filename = new File(imagePath);

        try {
            FileOutputStream out = new FileOutputStream(filename);
            if (null == bitmap) {
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mPathResultImage.put(""+mkey, imagePath);
        mkey++;
        return imagePath;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String saveImageAPI23(Bitmap bitmap){
        String fname = FuncHelper.getRandomString("innosoft_gallery_")+ ".png";
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        //String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Cameras/");
        if (!myDir.exists()) myDir.mkdirs();
        File filedir = new File(myDir, fname);
        String imagePath = filedir.toString();

        File filename = new File(imagePath);

        try {
            FileOutputStream out = new FileOutputStream(filename);
            if (null == bitmap) {
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mPathResultImage.put(""+mkey, imagePath);
        mkey++;
        return imagePath;
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
    public void cameraPermissionsResult(int reqCode, int[] grantResults) {

    }

    @Override
    public void getTabHeight(int t) {
        params.setMargins(0, 0, 0, t);
        layoutCapture.setLayoutParams(params);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;
        setupImageDisplay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                Toast.makeText(f, "Cannot show preview!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onResume() {
        super.onResume();
        setCameras();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onFinishAct() {
        if (mCameraData != null) {
            String jsonResult = new Gson().toJson(mPathResultImage);
            Intent intent = new Intent();
            intent.putExtra(DataStatic.EXTRA_PATH_RESULT_IMG_CAMERA, jsonResult);
            f.setResult(RESULT_OK, intent);
        }
        else {
            f.setResult(RESULT_CANCELED);
        }
    }

    @Override
    public void close(ListObject data, int position) {
        data.remove(position);
        previewCameraAdapter.notifyItemRemoved(position);
        previewCameraAdapter.notifyItemRangeChanged(position, data.size());
        if(data.size() == 0)
        {
            imagePin.setTag("0");
            layoutPin.setVisibility(View.GONE);
        }
        tempPhoto.clear();
        int x = 0;
        for(ListObject o : data)
        {
            String s = (String) o.s1;
            String data2 = s + "|0|" + x;
            tempPhoto.put(""+x, data2);
            x++;
        }
    }
}