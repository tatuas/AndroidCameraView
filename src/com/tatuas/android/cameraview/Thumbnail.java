package com.tatuas.android.cameraview;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class Thumbnail {
    public String path;
    public int thumbSizeScale;

    public Thumbnail(String path, int sizeScale) {
        this.path = path;
        this.thumbSizeScale = sizeScale;
    }

    public String make(byte[] data) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = thumbSizeScale;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length, options);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.close();
            if (bitmap != null) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}