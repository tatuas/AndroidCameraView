package com.tatuas.android.cameraview;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

public enum CameraType {
    FRONT, BACK;
    @SuppressLint("InlinedApi")
    public int getCameraId() {
        if (!Util.isFroyo()) {
            switch (this) {
                case FRONT:
                    return getCameraIdByInfo(Camera.CameraInfo.CAMERA_FACING_FRONT);
                case BACK:
                    return getCameraIdByInfo(Camera.CameraInfo.CAMERA_FACING_BACK);
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    @SuppressLint("NewApi")
    private int getCameraIdByInfo(int info) {
        int defaultCameraId = 0;
        if (!Util.isFroyo()) {
            CameraInfo cameraInfo = new CameraInfo();
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == info) {
                    defaultCameraId = i;
                }
            }
        }
        return defaultCameraId;
    }
}
