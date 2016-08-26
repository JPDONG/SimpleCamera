package com.learn.mycamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by dongjiangpeng on 2016/8/26 0026.
 */
public class PermissionsChecker {
    private Context mContext;

    public PermissionsChecker(Context mContext) {
        this.mContext = mContext;
    }

    public boolean lacksPermissions(String... permissions){
        for(String permission:permissions){
            if(lacksPermission(permission)){
                return true;
            }
        }
        return false;
    }

    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_DENIED;
    }
}
