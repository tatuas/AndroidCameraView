package com.tatuas.android.cameraview;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;

public class Options {
    private int quality = 100;
    private PictureSize pictureSize = new PictureSize();
    private PictureType pictureType = PictureType.JPEG;
    private Config config = Config.RGB_565;
    private int calculateScale = 1;
    private boolean useCalculateScale = false;
    private boolean reStartPreviewAfterShutter = true;
    private boolean execAutoFocusWhenShutter = false;

    public void setPictureSize(PictureSize size) {
        this.pictureSize = size;
    }

    public int getPictureWidth() {
        return this.pictureSize.width;
    }

    public int getPictureHeight() {
        return this.pictureSize.height;
    }

    public int getCalculateScale() {
        return this.calculateScale;
    }

    public void setPictureType(PictureType type) {
        this.pictureType = type;
        this.useCalculateScale = false;
    }

    public CompressFormat getPictureType() {
        return pictureType.toFormat();
    }

    public void setPictureConfig(Config config) {
        this.config = config;
    }

    public Config getPictureConfig() {
        return this.config;
    }

    public PictureType getType() {
        return this.pictureType;
    }

    public void setQuality(int val) {
        this.quality = val;
    }

    public int getQuality() {
        return this.quality;
    }

    public void setCalculateScale(int scale) {
        this.calculateScale = scale;
        this.useCalculateScale = true;
    }

    public boolean isUseCalculateScale() {
        return this.useCalculateScale;
    }

    public void setRestartPreviewAfterShutter(boolean restart) {
        this.reStartPreviewAfterShutter = restart;
    }

    public boolean isRestartPreviewAfterShutter() {
        return this.reStartPreviewAfterShutter;
    }

    public void setExecAutoFocusWhenShutter(boolean shutter) {
        this.execAutoFocusWhenShutter = shutter;
    }

    public boolean isExecAutoFocusWhenShutter() {
        return this.execAutoFocusWhenShutter;
    }
}
