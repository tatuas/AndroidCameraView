package com.tatuas.android.cameraview;

import java.io.File;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.tatuas.android.bitmapview.BitmapView;

public class CameraLayout extends FrameLayout {
    private CameraView cameraView;
    private BitmapView bitmapView;
    private View black;

    public CameraLayout(Context context) {
        super(context);
        setBlackBackground(context);

        cameraView = new CameraView(context);
        bitmapView = new BitmapView(context);
        setCameraGravity();

        addView(cameraView);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
    }

    public CameraLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBlackBackground(context);

        cameraView = new CameraView(context, attrs);
        bitmapView = new BitmapView(context, attrs);
        setCameraGravity();

        addView(cameraView);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
    }

    public CameraLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBlackBackground(context);

        cameraView = new CameraView(context, attrs, defStyle);
        bitmapView = new BitmapView(context, attrs, defStyle);
        setCameraGravity();

        addView(cameraView);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
    }

    public void setBlackBackground(Context context) {
        black = new View(context);
        black.setBackgroundResource(android.R.color.black);
    }

    public CameraView getCameraView() {
        return cameraView;
    }

    public void setCameraGravity() {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        cameraView.setLayoutParams(lp);
    }

    public void showPreview(String path) {
        if (path == null) {
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        int width = getDisplayWidth();
        int height = getDisplayHeight();

        bitmapView.setImageFromFilePath(file.getAbsolutePath(), width, height);
        addView(bitmapView, 1);
    }

    public void removePreview() {
        bitmapView.setImageDrawable(null);
        removeAllViews();
        addView(cameraView);
    }

    private int getDisplayWidth() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    private int getDisplayHeight() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    @Override
    protected void onDetachedFromWindow() {
        removePreview();
        super.onDetachedFromWindow();
    }
}
