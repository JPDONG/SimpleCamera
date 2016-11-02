package com.learn.mycamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dongjiangpeng on 2016/7/5 0005.
 */
public class SaveMediaFile {
    public static int MEDIA_TYPE_IMAGE = 1;
    public static int MEDIA_TYPE_VIDEO = 2;
    public static String TAG = "SaveMediaFile";

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    public static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Camera");
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdir()){
                Log.d(TAG, "getOutputMediaFile: failed to create new file");
                return null;
            }
        }
        /**
         * create file name
         */
        String timeStamp = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        }else if(type == MEDIA_TYPE_VIDEO){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        }else{
            return null;
        }
        Log.d(TAG, "getOutputMediaFile: mediafile:" + mediaFile);
        return mediaFile;
    }

    public static Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bm0 = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix m = new Matrix();
            m.setRotate(90,(float) bm0.getWidth() / 2, (float) bm0.getHeight() / 2);
            final Bitmap bm = Bitmap.createBitmap(bm0, 0, 0, bm0.getWidth(), bm0.getHeight(), m, true);
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if(pictureFile == null){
                Log.d(TAG, "onPictureTaken: failed to create pictureFile");
                return;
            }
            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                //fos.write(data);
                fos.close();
            }catch (FileNotFoundException e){
                Log.d(TAG, "onPictureTaken: file not found" + e.getMessage());
            }catch(IOException e){
                Log.d(TAG, "onPictureTaken: error access file" + e.getMessage());
            }
        }
    };
}
