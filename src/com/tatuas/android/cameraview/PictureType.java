package com.tatuas.android.cameraview;

import android.graphics.Bitmap.CompressFormat;

public enum PictureType {
    JPEG, PNG;
    public CompressFormat toFormat() {
        switch (this) {
            case JPEG:
                return CompressFormat.JPEG;
            case PNG:
                return CompressFormat.PNG;
            default:
                break;
        }
        return null;
    }
}
