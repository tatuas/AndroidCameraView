package com.tatuas.android.cameraview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.util.Log;

public class Shutter implements AutoFocusCallback, PictureCallback {
    private Context context;
    private String savePath;
    private Thumbnail thumb;
    private Options options;
    private BeforeShutterListener beforeShutterListener;
    private AfterShutterListener afterShutterListener;
    private CameraView cameraView;

    public Shutter(CameraView cameraView, Context context) {
        this.cameraView = cameraView;
        this.context = context;
        this.options = new Options();
    }

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
                if (options.isRestartPreviewAfterShutter()) {
                    camera.startPreview();
                }
            }
        } else {
            Log.e("sdfdfs", "sdfsdfsd");
        }
    }

    private boolean savePicture(byte[] data, Thumbnail thumb) {
        try {
            PictureMaker pm = new PictureMaker(savePath);
            BitmapFactory.Options bitmapOptions = createBitmapOptions(data, pm);
            bitmapOptions.inPreferredConfig = options.getPictureConfig();

            boolean picResult = pm.make(data, bitmapOptions, options);

            boolean thumbResult = true;
            if (thumb != null) {
                thumbResult = thumb.make(data);
            }

            return (picResult && thumbResult);
        } catch (Exception e) {
            return false;
        }
    }

    private BitmapFactory.Options createBitmapOptions(byte[] data,
            PictureMaker pm) {
        int width = options.getPictureWidth();
        int height = options.getPictureHeight();

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);

        if (width <= 0) {
            width = bitmapOptions.outWidth;
        }

        if (height <= 0) {
            height = bitmapOptions.outHeight;
        }

        if (options.useCalculateScale()) {
            bitmapOptions.inSampleSize = options.getCalculateScale();
        } else {
            bitmapOptions.inSampleSize = pm.calculateInSampleSize(
                    bitmapOptions, width, height);
        }

        bitmapOptions.inJustDecodeBounds = false;

        return bitmapOptions;
    }
}
