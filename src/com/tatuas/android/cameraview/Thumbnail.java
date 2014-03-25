package com.tatuas.android.cameraview;

import android.graphics.Bitmap.CompressFormat;

public class Thumbnail extends PictureMaker {
    private int thumbSizeScale = 1;
    private CompressFormat format = CompressFormat.JPEG;
    private int quality = 100;

    public Thumbnail(String path, int sizeScale, int rotation, CameraType cameraType) {
        super(path, rotation, cameraType.equals(CameraType.FRONT));
        this.thumbSizeScale = sizeScale;
    }

    public void setQuality(int scale) {
        this.quality = scale;
    }

    public void setFormat(CompressFormat format) {
        this.format = format;
    }

    public boolean make(byte[] data) {
        return make(data, thumbSizeScale, quality, format);
    }
}