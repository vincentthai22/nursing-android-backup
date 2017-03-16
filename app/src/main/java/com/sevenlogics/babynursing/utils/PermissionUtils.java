package com.sevenlogics.babynursing.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by pravin on 8/20/16.
 */
public class PermissionUtils {

    static final String TAG = PermissionUtils.class.getSimpleName();

    static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    static final String PERMISSION_READ_DATA = Manifest.permission.READ_EXTERNAL_STORAGE;
    static final String PERMISSION_WRITE_DATA = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    static final String PERMISSION_LOCATION_GPS = Manifest.permission.ACCESS_FINE_LOCATION;

    private static boolean checkPermission(Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestPermission(Activity activity, Fragment fragment, String[] permissionArray, int requestCode) {
        if (null != activity)
            ActivityCompat.requestPermissions(activity, permissionArray, requestCode);
        else if (null != fragment)
            fragment.requestPermissions(permissionArray, requestCode);
    }

    /**
     * Camera
     */
    public static boolean checkCameraPermission(Context context) {
        return checkPermission(context, PERMISSION_CAMERA) && checkReadDataPermission(context) && checkWriteDataPermission(context);
    }

    public static void requestCameraPermission(Activity activity, int requestCode) {
        requestPermission(activity, null, new String[]{PERMISSION_CAMERA, PERMISSION_WRITE_DATA, PERMISSION_READ_DATA}, requestCode);
    }

    public static void requestCameraPermission(Fragment fragment, int requestCode) {
        requestPermission(null, fragment, new String[]{PERMISSION_CAMERA, PERMISSION_WRITE_DATA, PERMISSION_READ_DATA}, requestCode);
    }

    /**
     * Read Data
     */
    public static boolean checkReadDataPermission(Context context) {
        return checkPermission(context, PERMISSION_READ_DATA);
    }

    public static void requestReadDataPermission(Activity activity, int requestCode) {
        requestPermission(activity, null, new String[]{PERMISSION_READ_DATA}, requestCode);
    }

    public static void requestReadDataPermission(Fragment fragment, int requestCode) {
        requestPermission(null, fragment, new String[]{PERMISSION_READ_DATA}, requestCode);
    }

    /**
     * Write Data
     */
    public static boolean checkWriteDataPermission(Context context) {
        return checkPermission(context, PERMISSION_WRITE_DATA);
    }

    public static void requestWriteDataPermission(Activity activity, int requestCode) {
        requestPermission(activity, null, new String[]{PERMISSION_WRITE_DATA}, requestCode);
    }

    public static void requestWriteDataPermission(Fragment fragment, int requestCode) {
        requestPermission(null, fragment, new String[]{PERMISSION_WRITE_DATA}, requestCode);
    }


    /**
     * Normal Location
     */
    public static boolean checkNormalLocationPermission(Context context) {
        return checkPermission(context, PERMISSION_LOCATION);
    }

    public static void requestNormalLocationPermission(Activity activity, int requestCode) {
        requestPermission(activity, null, new String[]{PERMISSION_LOCATION}, requestCode);
    }

    public static void requestNormalLocationPermission(Fragment fragment, int requestCode) {
        requestPermission(null, fragment, new String[]{PERMISSION_LOCATION}, requestCode);
    }

    /**
     * Fine Location
     */
    public static boolean checkFineLocationPermission(Context context) {
        return checkPermission(context, PERMISSION_LOCATION_GPS);
    }

    public static void requestFineLocationPermission(Activity activity, int requestCode) {
        requestPermission(activity, null, new String[]{PERMISSION_LOCATION_GPS}, requestCode);
    }

    public static void requestFineLocationPermission(Fragment fragment, int requestCode) {
        requestPermission(null, fragment, new String[]{PERMISSION_LOCATION_GPS}, requestCode);
    }
}