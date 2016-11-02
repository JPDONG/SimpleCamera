package com.learn.mycamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private ImageView mTimeLapseIndicator;

    static String[] permissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private PermissionsChecker mPermissionsChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(TAG, "onCreate: true");
        mPermissionsChecker = new PermissionsChecker(this);
        if(mPermissionsChecker.lacksPermissions(permissions)){
            ActivityCompat.requestPermissions(this,permissions,1);
        }else{
            prepare();
        }

            /*handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mCamera != null){
                        mCamera.startPreview();
                        mCamera.stopPreview();
                        mCamera.startPreview();
                    }else{
                        Log.d(TAG, "run: camera is null");
                    }

                }
            });
        }*/


    }

    private void prepare() {

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
        initCamera();
        mCamera.startPreview();
    }

    private void initCamera() {
        mCamera = getCameraInstance();
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mPreviewSizeList = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mPictureSizeList = parameters.getSupportedPictureSizes();
        List<Camera.Size> mThumbSizeList = parameters.getSupportedJpegThumbnailSizes();
        List<List<Camera.Size>> mList = new ArrayList<>();
        mList.add(mPreviewSizeList);
        mList.add(mPictureSizeList);
        mList.add(mThumbSizeList);
        for(List<Camera.Size> list:mList){
            for(Camera.Size size: list){
                Log.d(TAG, "initCamera: w = " + size.width + ",h = " + size.height);
            }
            Log.d(TAG, "\n");
        }
        parameters.setPreviewSize(1280,720);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
        mCameraSurfaceView = new CameraSurfaceView(MainActivity.this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.framelayput_preview);
        preview.addView(mCameraSurfaceView);
    }

    private void initRateData() {
        rateItemList = new ArrayList<>();
        RateItem rateItem0 = new RateItem(R.drawable.timelapse_1s,"1s");
        RateItem rateItem1 = new RateItem(R.drawable.timelapse_1_5s,"1.5s");
        RateItem rateItem2 = new RateItem(R.drawable.timelapse_2s,"2s");
        RateItem rateItem3 = new RateItem(R.drawable.timelapse_2_5s,"2.5s");
        RateItem rateItem4 = new RateItem(R.drawable.timelapse_3s,"3s");
        RateItem rateItem5 = new RateItem(R.drawable.timelapse_5s,"5s");
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
        /*if(mPermissionsChecker.lacksPermissions(permissions)){
            ActivityCompat.requestPermissions(this,permissions,1);
        }else {
            mCamera.startPreview();
        }*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Configuration cf= this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = cf.orientation ; //获取屏幕方向
        if(ori == cf.ORIENTATION_LANDSCAPE){
            Log.d(TAG, "onConfigurationChanged: landscape");
            //横屏
            mCamera.setDisplayOrientation(0);
        }else if(ori == cf.ORIENTATION_PORTRAIT){
            //竖屏
            mCamera.setDisplayOrientation(90);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1){
            for (int i = 0; i < grantResults.length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    finish();
                }
            }
            prepare();
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
        mTimeLapseIndicator.setOnClickListener(this);
        captureRateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                switch(position){
                    case 0:
                        CAPTURE_RATE = 10;
                        mTimeLapseIndicator.setImageResource(R.drawable.timelapse_1s);
                        break;
                    case 1:
                        CAPTURE_RATE = 8;
                        mTimeLapseIndicator.setImageResource(R.drawable.timelapse_1_5s);
                        break;
                    case 2:
                        CAPTURE_RATE = 6.6;
                        mTimeLapseIndicator.setImageResource(R.drawable.timelapse_2s);
                        break;
                    case 3:
                        CAPTURE_RATE = 5.7;
                        mTimeLapseIndicator.setImageResource(R.drawable.timelapse_2_5s);
                        break;
                    case 4:
                        CAPTURE_RATE = 5;
                        mTimeLapseIndicator.setImageResource(R.drawable.timelapse_3s);
                        break;
                    case 5:
                        CAPTURE_RATE = 3.3;
                        mTimeLapseIndicator.setImageResource(R.drawable.timelapse_5s);
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
        mTimeLapseIndicator = (ImageView) findViewById(R.id.time_lapse_indicator);
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
            c = Camera.open(1);

            usingCameraId = 1;
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
                Log.d(TAG, "onClick: uptimeMillis:" + SystemClock.uptimeMillis());
                Log.d(TAG, "onClick: currentThreadTimeMillis:" + SystemClock.currentThreadTimeMillis());
                Log.d(TAG, "onClick: currentTimeMillis:" + System.currentTimeMillis());

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
            case  R.id.time_lapse_indicator:
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
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA));
        //mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_HIGH));

        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        camcorderProfile.videoFrameWidth = 1280;
        camcorderProfile.videoFrameHeight = 720;
        camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
        camcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        mediaRecorder.setProfile(camcorderProfile);

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
