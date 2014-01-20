package com.tatuas.android.cameraview;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class PictureMaker {
    private String path;

    public PictureMaker(String path) {
        this.path = path;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidthPx, int reqHeightPx) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeightPx || width > reqWidthPx) {
            final int calcHeight = height;
            final int calcWidth = width;

            while ((calcHeight / inSampleSize) > reqHeightPx
                    || (calcWidth / inSampleSize) > reqWidthPx) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public boolean make(byte[] data, int picSizeScale, int quality,
            CompressFormat format) {
        final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = picSizeScale;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    bitmapOptions);
            bitmap.compress(format, quality, fos);
            fos.close();

            if (bitmap != null) {
                bitmap.recycle();
            }

            return isPictureMaked();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean make(byte[] data, BitmapFactory.Options bitmapOptions,
            Options options) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                bitmapOptions);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(options.getPictureType(),
                    options.getQuality(), fos);
            fos.close();
        } catch (Exception e) {
            return false;
        }

        if (bitmap != null) {
            bitmap.recycle();
        }

        return isPictureMaked();
    }

    public boolean simpleMake(byte[] data) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            fos.write(data);
            fos.close();
            return isPictureMaked();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPictureMaked() {
        if (path == null) {
            return false;
        }

        return new File(path).exists();
    }
}
