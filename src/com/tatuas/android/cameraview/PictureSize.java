package com.tatuas.android.cameraview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;

public class PictureSize {
    public int width;
    public int height;

    public PictureSize() {
        this.width = 0;
        this.height = 0;
    }

    public PictureSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public enum AspectRatio {
        NORMAL, WIDE, OTHER
    }

    public static class MyComp implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            return Double.valueOf(rhs.width * rhs.height).compareTo(Double.valueOf(
                    lhs.width * lhs.height));
        }
    }

    public void setSize(Camera.Size size) {
        size.width = this.width;
        size.height = this.height;
    }

    public static PictureSize getMaxSizeByAspectRatio(List<Camera.Size> cameraSizes,
            AspectRatio aspect) {
        List<Camera.Size> markSizes = new ArrayList<Camera.Size>();
        for (Camera.Size s : cameraSizes) {
            if (aspect != null) {
                if (getAspectRatio(s.width, s.height).equals(aspect)) {
                    markSizes.add(s);
                }
            }
        }

        Collections.sort(markSizes, new MyComp());
        return new PictureSize(markSizes.get(0).width, markSizes.get(0).height);
    }

    public static AspectRatio getAspectRatio(int width, int height) {
        double area = Double.valueOf(width).doubleValue()
                / Double.valueOf(height).doubleValue();
        BigDecimal decimal = new BigDecimal(String.valueOf(area));
        double k1 = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

        if (k1 == 1.8) {
            return AspectRatio.WIDE;
        } else if (k1 == 1.3) {
            return AspectRatio.NORMAL;
        } else {
            return AspectRatio.OTHER;
        }
    }
}
