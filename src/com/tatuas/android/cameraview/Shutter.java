package com.tatuas.android.cameraview;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
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
    private boolean isFront;
    private ShutterFailedListener shutterFailedListener;

    public Shutter(CameraView cameraView, Context context, Options options) {
        this.cameraView = cameraView;
        this.context = context;
        this.options = options;
        this.isFront = false;
    }

    public void exec(String savePath) {
        this.savePath = savePath;
        shot();
    }

    public void exec(String savePath, Thumbnail thumb) {
        this.savePath = savePath;
        this.thumb = thumb;
        shot();
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
        }
    }

    public Context getContext() {
        return this.context;
    }

    public void setBeforeShutterListener(BeforeShutterListener listener) {
        this.beforeShutterListener = listener;
    }

    public void setAfterShutterListener(AfterShutterListener listener) {
        this.afterShutterListener = listener;
    }

    public void setShutterFailedListener(ShutterFailedListener listener) {
        this.shutterFailedListener = listener;
    }

    private void shot() {
        if (cameraView.getCamera() != null) {
            if (options.isExecAutoFocusWhenShutter()) {
                cameraView.getCamera().autoFocus(this);
            } else {
                if (cameraView.getCameraType().equals(CameraType.FRONT)) {
                    isFront = true;
                }
                cameraView.getCamera().takePicture(null, null, this);
            }
        }
    }

    private boolean savePicture(byte[] data, Thumbnail thumb) {
        try {
            PictureMaker pm = new PictureMaker(savePath,
                    Util.getDisplayRotationValue((Activity) context), isFront);
            BitmapFactory.Options bitmapOptions = createBitmapOptions(data, pm);
            bitmapOptions.inPreferredConfig = options.getPictureConfig();

            boolean picResult = pm.make(data, bitmapOptions, options);

            boolean thumbResult = true;
            if (thumb != null) {
                thumbResult = thumb.make(data);
            }

            return (picResult && thumbResult);
        } catch (Exception e) {
            this.shutterFailedListener.onFailed(e.toString());
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

        if (options.isUseCalculateScale()) {
            bitmapOptions.inSampleSize = options.getCalculateScale();
        } else {
            bitmapOptions.inSampleSize = pm.calculateInSampleSize(
                    bitmapOptions, width, height);
        }

        bitmapOptions.inJustDecodeBounds = false;

        return bitmapOptions;
    }
}
