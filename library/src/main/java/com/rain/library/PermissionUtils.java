package com.rain.library;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2019, by your 爱空间, All rights reserved.
 * -----------------------------------------------------------------
 *
 * File: PermissionUtils.java
 * Author: rxy
 * Version: V100R001C01
 * Create: 2019-09-05 14:33
 *
 * Changes (from 2019-09-05)
 * -----------------------------------------------------------------
 * 2019-09-05 : Create PermissionUtils.java (rxy);
 * -----------------------------------------------------------------
 */
public class PermissionUtils {

    /**
     * 是否显示显示申请权限的dialog
     *
     * @return true 表示勾选了不再提示 false 显示
     */
    public static boolean shouldShowPermissionDialog(Activity context, String[] permissions) {
        boolean shouldShow = true;
        for (String permission : permissions) {
            //false的时候表示用户已经不需要解释了，true表示解释后还可以继续弹出请求
            if (!shouldShowRequestPermissionRationale(context, permission)) {
                shouldShow = false;
                break;
            }
        }
        return shouldShow;
    }

    /**
     * 是否有必要告诉用户我们需要这个权限的原因
     *
     * @return true 是
     * false 否
     */
    private static boolean shouldShowRequestPermissionRationale(Activity context, String permissions) {
        return ActivityCompat.shouldShowRequestPermissionRationale(context, permissions);
    }

    /**
     * 判断用户是否具有某些权限
     *
     * @param permissions 权限组
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String per : permissions) {
                result = hasPermission(context, per);
                if (!result)
                    break;
            }
        }
        return result;
    }

    /**
     * 判断用户是否具有某些权限
     *
     * @param permission 权限
     */
    private static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 从一组权限中
     */
    public static String[] deniedPermissions(Context context, String[] permissions) {
        ArrayList<String> deniedResult = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                deniedResult.add(permission);
            }
        }
        String[] result = new String[deniedResult.size()];
        deniedResult.toArray(result);
        return result;
    }

    /**
     * 从一组权限中
     */
    public static ArrayList<String> deniedPermissions(Context context, List<String> permissions) {
        ArrayList<String> deniedResult = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                deniedResult.add(permission);
            }
        }
        return deniedResult;
    }

}
