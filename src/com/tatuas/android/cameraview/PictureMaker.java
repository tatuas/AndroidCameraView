package com.tatuas.android.cameraview;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class PictureMaker {
    protected String path;
    protected float rotation = 0;

    public PictureMaker(String path, int rotation, boolean isFront) {
        this.path = path;
        this.rotation = (float) rotation;
        if (isFront) {
            this.rotation = Util.addDegreesToRotation((int)this.rotation, 180);
        }
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

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
            rotatedBitmap.compress(format, quality, fos);
            fos.close();

            if (bitmap != null) {
                bitmap.recycle();
            }

            if (rotatedBitmap != null) {
                rotatedBitmap.recycle();
            }

            return isPictureMaked();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean make(byte[] data, BitmapFactory.Options bitmapOptions,
            Options options) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    bitmapOptions);
            FileOutputStream fos = new FileOutputStream(path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                    matrix, true);
            rotatedBitmap.compress(options.getPictureType(),
                    options.getQuality(), fos);
            fos.close();

            if (bitmap != null) {
                bitmap.recycle();
            }

            if (rotatedBitmap != null) {
                rotatedBitmap.recycle();
            }
        } catch (Exception e) {
            return false;
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
