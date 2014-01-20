package com.tatuas.android.cameraview;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;

public class Options {
    private boolean reStartPreviewAfterShutter = true;
    private int quality = 100;
    private Size pictureSize = new Size();
    private Type pictureType = Type.JPEG;
    private Config config = Config.RGB_565;
    private int calculateScale = 1;
    private boolean useCalculateScale = false;

    public void setPictureSize(Size size) {
        this.pictureSize = size;
    }

    public int getPictureWidth() {
        return this.pictureSize.width;
    }

    public int getPictureHeight() {
        return this.pictureSize.height;
    }

    public void setCalculateScale(int scale) {
        this.calculateScale = scale;
        this.useCalculateScale = true;
    }

    public int getCalculateScale() {
        return this.calculateScale;
    }

    public boolean useCalculateScale() {
        return this.useCalculateScale;
    }

    public void setPictureType(Type type) {
        this.pictureType = type;
        this.useCalculateScale = false;
    }

    public CompressFormat getPictureType() {
        return pictureType.toFormat();
    }

    public void setRestartPreviewAfterShutter(boolean restart) {
        this.reStartPreviewAfterShutter = restart;
    }

    public void setPictureConfig(Config config) {
        this.config = config;
    }

    public Config getPictureConfig() {
        return this.config;
    }

    public boolean isRestartPreviewAfterShutter() {
        return this.reStartPreviewAfterShutter;
    }

    public Type getType() {
        return this.pictureType;
    }

    public void setQuality(int val) {
        this.quality = val;
    }

    public int getQuality() {
        return this.quality;
    }
}
