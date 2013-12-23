package com.tatuas.android.cameraview;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;

public class Shutter implements AutoFocusCallback, PictureCallback {
    private Context context;
    private String savePath;
    private Thumbnail thumb;
    private Options options;
    private BeforeShutterListener beforeShutterListener;
    private AfterShutterListener afterShutterListener;
    private CameraView cameraView;

    public Shutter(CameraView cameraView, Context context, Options options) {
        this.cameraView = cameraView;
        this.context = context;
        this.options = options;
    }

    public Context getContext() {
        return this.context;
    }

    public void exec(String savePath) {
        this.savePath = savePath;

        if (cameraView.getCamera() != null) {
            cameraView.getCamera().autoFocus(this);
        }
    }

    public void exec(String savePath, Thumbnail thumb) {
        this.savePath = savePath;
        this.thumb = thumb;

        if (cameraView.getCamera() != null) {
            cameraView.getCamera().autoFocus(this);
        }
    }

    public void setBeforeShutterListener(BeforeShutterListener listener) {
        this.beforeShutterListener = listener;
    }

    public void setAfterShutterListener(AfterShutterListener listener) {
        this.afterShutterListener = listener;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (beforeShutterListener != null) {
            beforeShutterListener.beforeShutter();
        }
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (camera == null) {
            return;
        }

        camera.stopPreview();
        if (savePicture(data, thumb)) {
            if (afterShutterListener != null) {
                afterShutterListener.afterShutter();
            }

            if (options != null) {
                if (options.reStartPreviewAfterShutter) {
                    camera.startPreview();
                }
            }
        }
    }

    public boolean savePicture(byte[] data, Thumbnail thumb) {
        File pictureFile = new File(savePath);
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            if (thumb != null) {
                thumb.make(data);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
