package com.learn.mycamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.learn.mycamera.SaveMediaFile;

public class MainActivity extends Activity implements View.OnClickListener {
    public static String TAG = "MainActivity";
    private Camera mCamera;
    private CameraSurfaceView mCameraSurfaceView;
    private Button changeCamera;
    private Button takePhoto;
    private Button recordVideo;
    private Button captureRateButton;
    private ListView captureRateListView;
    private LinearLayout captureRateLayout;
    private RateItemAdapter rateItemAdapter;
    static int usingCameraId;
    private Handler handler;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    public static int selectPosition = 0;
    private List<RateItem> rateItemList;
    private double CAPTURE_RATE = 10;

    static String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private PermissionsChecker mPermissionsChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionsChecker = new PermissionsChecker(this);
        if(mPermissionsChecker.lacksPermissions(permissions)){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());
        initRateData();
        initViews();
        initEvents();

        if (checkCameraHardware(this)) {
            Log.d(TAG, "onCreate: 有摄像头");
        } else {
            Log.d(TAG, "onCreate: 无摄像头");
        }
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mCameraSurfaceView = new CameraSurfaceView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.framelayput_preview);
        preview.addView(mCameraSurfaceView);
    }

    private void initRateData() {
        rateItemList = new ArrayList<>();
        RateItem rateItem0 = new RateItem(R.drawable.ic_launcher,"1s");
        RateItem rateItem1 = new RateItem(R.drawable.ic_launcher,"1.5s");
        RateItem rateItem2 = new RateItem(R.drawable.ic_launcher,"2s");
        RateItem rateItem3 = new RateItem(R.drawable.ic_launcher,"2.5s");
        RateItem rateItem4 = new RateItem(R.drawable.ic_launcher,"3s");
        RateItem rateItem5 = new RateItem(R.drawable.ic_launcher,"5s");
        rateItemList.add(rateItem0);
        rateItemList.add(rateItem1);
        rateItemList.add(rateItem2);
        rateItemList.add(rateItem3);
        rateItemList.add(rateItem4);
        rateItemList.add(rateItem5);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCamera.startPreview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1){
            for (int i = 0; i < grantResults.length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    finish();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        releaseMediaRecorder();
    }

    private void releaseCamera() {
        if(mCamera != null){
            mCamera.release();
        }
    }

    private void releaseMediaRecorder() {
        if(mediaRecorder != null){
            mediaRecorder.release();
        }
    }

    private void initEvents() {
        changeCamera.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        recordVideo.setOnClickListener(this);
        captureRateButton.setOnClickListener(this);
        captureRateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                switch(position){
                    case 0:
                        CAPTURE_RATE = 10;
                        break;
                    case 1:
                        CAPTURE_RATE = 8;
                        break;
                    case 2:
                        CAPTURE_RATE = 6.6;
                        break;
                    case 3:
                        CAPTURE_RATE = 5.7;
                        break;
                    case 4:
                        CAPTURE_RATE = 5;
                        break;
                    case 5:
                        CAPTURE_RATE = 3.3;
                        break;
                }
                rateItemAdapter.notifyDataSetChanged();
                captureRateLayout.setVisibility(View.GONE);
            }
        });
    }

    private void initViews() {
        changeCamera = (Button) findViewById(R.id.bt_change_camera);
        takePhoto = (Button) findViewById(R.id.bt_takephoto);
        recordVideo = (Button) findViewById(R.id.bt_video);
        captureRateButton = (Button) findViewById(R.id.bt_capture_rate);
        captureRateLayout = (LinearLayout) findViewById(R.id.layout_rate_list);
        captureRateListView = (ListView) captureRateLayout.findViewById(R.id.rate_list);
        rateItemAdapter = new RateItemAdapter(this,R.layout.rate_list_item,rateItemList);
        captureRateListView.setAdapter(rateItemAdapter);
    }

    /**
     * 检查是否有镜头
     *
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            //有摄像头
            return true;
        } else {
            //无摄像头
            return false;
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        int cameraNumber = Camera.getNumberOfCameras();

        Log.d(TAG, "getCameraInstance: camera number :" + cameraNumber);
        try {
            c = Camera.open(0);

            usingCameraId = 0;
        } catch (Exception e) {
            Log.d(TAG, "getCameraInstance: camera is not available");
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_change_camera:
                break;
            case R.id.bt_takephoto:
                mCamera.takePicture(null,null,SaveMediaFile.mPictureCallback);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCamera.startPreview();
                    }
                },1000);
                break;
            case R.id.bt_video:
                if(isRecording){
                    stopRecording();
                    isRecording = false;
                }else {
                    Recording();
                    isRecording = true;
                }
                break;
            case R.id.bt_capture_rate:
                if(captureRateLayout.getVisibility() == View.GONE) {
                    captureRateLayout.setVisibility(View.VISIBLE);
                }else{
                    captureRateLayout.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void stopRecording() {
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.reset();
            releaseMediaRecorder();
            mCamera.lock();
        }
    }

    private void Recording() {
        mediaRecorder = new MediaRecorder();
        mCamera.unlock();
        //mCamera.setDisplayOrientation(90);
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_HIGH));

        mediaRecorder.setOutputFile(SaveMediaFile.getOutputMediaFile(SaveMediaFile.MEDIA_TYPE_VIDEO).toString());
        mediaRecorder.setCaptureRate(CAPTURE_RATE);
        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setPreviewDisplay(mCameraSurfaceView.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
        }
    }




}
