package com.tatuas.android.cameraview;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

public class BitmapView extends ImageView {
    private Bitmap bitmap;
    private Bitmap.Config config = Bitmap.Config.ARGB_8888;
    private final String NAMESPACE = "http://tatuas.com/android/BitmapView";
    private File file;
    private String path;

    public BitmapView(Context context) {
        super(context);
    }

    public BitmapView(Context context, String path) {
        super(context);
        this.path = path;
        createFile(path);
    }

    public BitmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFile(attrs.getAttributeValue(NAMESPACE, "pictureFilePath"));
    }

    public BitmapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createFile(attrs.getAttributeValue(NAMESPACE, "pictureFilePath"));
    }

    public void setImageQuality(Bitmap.Config config) {
        this.config = config;
    }

    private void createFile(String path) {
        if (path != null) {
            File f = new File(path);
            file = f;
        }
    }

    public void updateImageFromFile(String path) {
        setImageBitmap(null);
        refreshDrawableState();
        bitmap.recycle();
        createFile(path);
        setImageFromFile(file, getWidth(), getHeight());
    }

    public void setImageFromFile(File file, int widthDp, int heightDp) {
        if (file == null) {
            return;
        }

        if (!file.exists()) {
            return;
        }

        String path = file.getAbsolutePath();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, widthDp, heightDp);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;

        bitmap = BitmapFactory.decodeFile(path, options);

        setImageBitmap(bitmap);

        refreshDrawableState();
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public String getPicturePath() {
        return this.path;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        reqWidth = convertDpToPx(reqWidth);
        reqHeight = convertDpToPx(reqHeight);

        if (height > reqHeight || width > reqWidth) {
            final int calcHeight = height;
            final int calcWidth = width;

            while ((calcHeight / inSampleSize) > reqHeight
                    || (calcWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public int convertPxToDp(int value) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return (int) (value / dm.density);
    }

    public int convertDpToPx(int value) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return (int) (value * dm.density);
    }

    @Override
    protected void onDetachedFromWindow() {
        setImageDrawable(null);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (file != null) {
            if (getWidth() > 0 && getHeight() > 0) {
                setImageFromFile(file, getWidth(), getHeight());
                file = null;
            }
        }
    }
}
