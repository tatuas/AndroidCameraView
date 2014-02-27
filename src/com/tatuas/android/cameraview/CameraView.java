package com.tatuas.android.cameraview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tatuas.android.cameraview.Size.AspectRatio;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera;
    private SurfaceHolder holder;
    private Camera.Parameters params;

    public CameraView(Context context) {
        super(context);
        setHolder();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHolder();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHolder();
    }

    @SuppressWarnings("deprecation")
    private void setHolder() {
        holder = getHolder();

        if (holder == null) {
            return;
        }

        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasCamera()) {
            return;
        }

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            return;
        }
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
        }

        if (camera == null) {
            return;
        }

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {

        if (holder.getSurface() == null) {
            return;
        }

        if (camera == null) {
            return;
        }

        camera.stopPreview();

        params = camera.getParameters();

        if (params != null) {
            setCameraPreviewSize(Size.AspectRatio.NORMAL, width, height);
            setAntiBanding(Camera.Parameters.ANTIBANDING_OFF);
            setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            setMaxPictureSize(AspectRatio.NORMAL);
            setCameraDisplayOrientation(getRotationValue());
            params.setRotation(getRotationValue());
            camera.setParameters(params);
        }

        camera.startPreview();
    }

    public void setCameraDisplayOrientation(int rotation) {
        camera.setDisplayOrientation(rotation);
    }

    public void setCameraPreviewSize(Size.AspectRatio aspect, int width,
            int height) {
        int baseLength = 0;
        int otherLength = 0;

        if (height <= 0 || width <= 0) {
            return;
        }

        if (isPortrait()) {
            baseLength = width;
        } else {
            baseLength = height;
        }

        if (aspect == (Size.AspectRatio.NORMAL)) {
            otherLength = (baseLength / 3) * 4;
        } else if (aspect == (Size.AspectRatio.WIDE)) {
            otherLength = (baseLength / 9) * 16;
        } else {
            return;
        }

        LayoutParams l = getLayoutParams();
        if (l == null) {
            return;
        }

        if (isPortrait()) {
            l.width = baseLength;
            l.height = otherLength;
        } else {
            l.height = baseLength;
            l.width = otherLength;
        }

        List<android.hardware.Camera.Size> previewSizes = params
                .getSupportedPreviewSizes();
        List<Integer> widths = new ArrayList<Integer>();
        List<Integer> heights = new ArrayList<Integer>();

        for (Camera.Size size : previewSizes) {
            widths.add(size.width);
        }

        for (Camera.Size size : previewSizes) {
            heights.add(size.height);
        }

        Collections.sort(widths);
        Collections.sort(heights);

        int w = 0;
        int h = 0;
        Double d1;
        Double d2;
        if (aspect == Size.AspectRatio.NORMAL) {
            d2 = Double.valueOf((double) 4.0 / (double) 3.0);
        } else {
            d2 = Double.valueOf((double) 16 / (double) 9);
        }

        for (int i = 0; i < previewSizes.size(); i++) {
            d1 = Double.valueOf((double) widths.get(i)
                    / (double) heights.get(i));
            if (d1.equals(d2)) {
                w = widths.get(i);
                h = heights.get(i);
            }
        }

        params.setPreviewSize(w, h);
        setLayoutParams(l);
    }

    private boolean hasCamera() {
        if (getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSupportedAutoFocus() {
        return getContext().getPackageManager().hasSystemFeature(
                "android.hardware.camera.autofocus");
    }

    public void setAntiBanding(String mode) {
        List<String> supported = params.getSupportedAntibanding();
        if (supported == null) {
            return;
        }
        if (supported.contains(mode)) {
            params.setAntibanding(mode);
        }
    }

    public void setFocusMode(String mode) {
        if (params == null) {
            return;
        }

        List<String> supported = params.getSupportedFocusModes();
        if (supported.contains(mode)) {
            params.setFocusMode(mode);
        }
    }

    public void setPictureRotation() {

    }

    public void setMaxPictureSize(Size.AspectRatio aspect) {
        if (params == null) {
            return;
        }

        if (aspect == null) {
            return;
        }

        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        Size size = Size.getMaxSizeByAspectRatio(pictureSizes, aspect);
        params.setPictureSize(size.width, size.height);
        params.setRotation(getRotationValue());
    }

    public boolean isPortrait() {
        return (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    public boolean isLandscape() {
        return (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    public int getRotationValue() {
        Display display = ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay();
        int rotation = display.getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                if (isLandscape()) {
                    return 0;
                } else {
                    return 90;
                }
            case Surface.ROTATION_90:
                if (isPortrait()) {
                    return 270;
                } else {
                    return 0;
                }
            case Surface.ROTATION_180:
                if (isPortrait()) {
                    return 180;
                } else {
                    return 270;
                }
            case Surface.ROTATION_270:
                if (isPortrait()) {
                    return 90;
                } else {
                    return 180;
                }
        }
        return 0;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public Camera.Parameters getCameraParams() {
        return camera.getParameters();
    }
}
