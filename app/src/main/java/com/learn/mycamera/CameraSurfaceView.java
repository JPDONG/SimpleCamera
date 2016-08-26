package com.learn.mycamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by dongjiangpeng on 2016/7/4 0004.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public static String TAG = "CameraSurfaceView";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    public CameraSurfaceView(Context context,Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            Log.d(TAG, "surfaceCreated: "+ holder);
            //mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch (Exception e){
            Log.d(TAG, "surfaceCreated: " + e.getMessage());
        }
       /* Canvas canvas = holder.lockCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Rect rect = new Rect(0,0,30,30);
        canvas.drawRect(rect,paint);
        holder.unlockCanvasAndPost(canvas);*/
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mHolder.getSurface() == null){
            return;
        }
        try{
            mCamera.stopPreview();
        }catch(Exception e){

        }
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }catch(Exception e){
            Log.d(TAG, "surfaceChanged: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
