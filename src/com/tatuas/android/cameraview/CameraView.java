package com.tatuas.android.cameraview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tatuas.android.cameraview.PictureSize.AspectRatio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String NAMESPACE = "http://tatuas.com/android/AndroidCameraView";
    private Camera camera;
    private Parameters cameraParams;
    private SurfaceHolder holder;
    private OpenCameraFailedListener cameraFailedListener;
    private CameraType cameraType = CameraType.BACK;

    public CameraView(Context context) {
        super(context);
        setHolder();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCameraTypeFromAttr(attrs.getAttributeValue(NAMESPACE, "camera"));
        setHolder();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCameraTypeFromAttr(attrs.getAttributeValue(NAMESPACE, "camera"));
        setHolder();
    }

    private void setCameraTypeFromAttr(String typeString) {
        CameraType type = CameraType.BACK;

        if (typeString == null) {
            return;
        }

        switch (typeString) {
            case "front":
                type = CameraType.FRONT;
                break;
            case "back":
                type = CameraType.BACK;
                break;
            default:
                break;
        }

        this.cameraType = type;
    }

    @SuppressLint("NewApi")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasCamera()) {
            camera = null;
            if (cameraFailedListener != null) {
                this.cameraFailedListener
                        .onFailed("This device has any camera");
            }
            return;
        }

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            if (cameraFailedListener != null) {
                this.cameraFailedListener.onFailed("Something Error");
            }
            return;
        }

        try {
            if (Util.isFroyo()) {
                camera = Camera.open();
            } else {
                camera = Camera.open(cameraType.getCameraId());
            }
            cameraParams = camera.getParameters();
        } catch (Exception e) {
            if (cameraFailedListener != null) {
                this.cameraFailedListener.onFailed(e.toString());
            }
            camera = null;
        }

        if (camera == null) {
            if (cameraFailedListener != null) {
                this.cameraFailedListener.onFailed("Something Error");
            }
            return;
        }

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            camera.release();
            if (cameraFailedListener != null) {
                this.cameraFailedListener.onFailed(e.toString());
            }
            camera = null;
            return;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
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

        if (cameraParams != null) {
            setCameraPreviewSize(PictureSize.AspectRatio.NORMAL, width, height);
            setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            setMaxPictureSize(AspectRatio.NORMAL);

            int displayRotation = getRotationValue();
            int pictureRotation = getRotationValue();

            if (cameraType.equals(CameraType.FRONT)) {
                if (Util.isPortrait(getContext())) {
                    pictureRotation = addDegreesToRotation(pictureRotation, 180);
                }
            }

            setCameraDisplayOrientation(displayRotation);
            cameraParams.setRotation(pictureRotation);
            camera.setParameters(cameraParams);
        }

        camera.startPreview();
    }

    public void setCameraType(CameraType type) {
        this.cameraType = type;
    }

    public int getRotationValue() {
        int result = 0;
        int rotation = ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay().getRotation();

        if (rotation == Surface.ROTATION_0) {
            if (Util.isPortrait(getContext())) {
                result = 90;
            } else {
                result = 0;
            }
        } else if (rotation == Surface.ROTATION_90) {
            if (Util.isPortrait(getContext())) {
                result = 270;
            } else {
                result = 0;
            }
        } else if (rotation == Surface.ROTATION_180) {
            if (Util.isPortrait(getContext())) {
                result = 180;
            } else {
                result = 270;
            }
        } else if (rotation == Surface.ROTATION_270) {
            if (Util.isPortrait(getContext())) {
                result = 90;
            } else {
                result = 180;
            }
        }

        return result;
    }

    private int addDegreesToRotation(int baseParam, int param) {
        int rotation = baseParam;
        rotation = (Math.abs(rotation + param));
        int absRotation = rotation % 360;
        if (absRotation == 0) {
            rotation = 0;
        } else {
            rotation = absRotation;
        }
        return rotation;
    }

    public Camera getCamera() {
        return camera;
    }

    public Camera.Parameters getCameraParams() {
        return camera.getParameters();
    }

    public void setCameraFailedListener(OpenCameraFailedListener listener) {
        this.cameraFailedListener = listener;
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

    private boolean isSupportedAutoFocus() {
        return getContext().getPackageManager().hasSystemFeature(
                "android.hardware.camera.autofocus");
    }

    private boolean hasCamera() {
        boolean flag = false;
        flag = getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);

        if (Util.is2012Nexus7(cameraType)) {
            flag = true;
        }

        return flag;
    }

    private void setCameraDisplayOrientation(int rotation) {
        camera.setDisplayOrientation(rotation);
    }

    private void setCameraPreviewSize(PictureSize.AspectRatio aspect,
            int width, int height) {
        int baseLength = 0;
        int otherLength = 0;

        if (height <= 0 || width <= 0) {
            return;
        }

        if (Util.isPortrait(getContext())) {
            baseLength = width;
        } else {
            baseLength = height;
        }

        if (aspect == (PictureSize.AspectRatio.NORMAL)) {
            otherLength = (baseLength / 3) * 4;
        } else if (aspect == (PictureSize.AspectRatio.WIDE)) {
            otherLength = (baseLength / 9) * 16;
        } else {
            return;
        }

        LayoutParams l = getLayoutParams();
        if (l == null) {
            return;
        }

        if (Util.isPortrait(getContext())) {
            l.width = baseLength;
            l.height = otherLength;
        } else {
            l.height = baseLength;
            l.width = otherLength;
        }

        List<android.hardware.Camera.Size> previewSizes = cameraParams
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
        if (aspect == PictureSize.AspectRatio.NORMAL) {
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

        cameraParams.setPreviewSize(w, h);
        setLayoutParams(l);
    }

    private void setFocusMode(String mode) {
        if (cameraParams == null) {
            return;
        }

        List<String> supported = cameraParams.getSupportedFocusModes();
        if (supported.contains(mode)) {
            cameraParams.setFocusMode(mode);
        }
    }

    private void setMaxPictureSize(PictureSize.AspectRatio aspect) {
        if (cameraParams == null) {
            return;
        }

        if (aspect == null) {
            return;
        }

        List<Camera.Size> pictureSizes = cameraParams
                .getSupportedPictureSizes();
        PictureSize size = PictureSize.getMaxSizeByAspectRatio(pictureSizes,
                aspect);
        cameraParams.setPictureSize(size.width, size.height);
        cameraParams.setRotation(getRotationValue());
    }
}
