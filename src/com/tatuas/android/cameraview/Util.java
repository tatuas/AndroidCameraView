package com.tatuas.android.cameraview;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

public class Util {
    public static boolean isFroyo() {
        return Build.VERSION_CODES.FROYO == Build.VERSION.SDK_INT;
    }

    public static boolean isPortrait(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    public static boolean isLandscape(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    public static boolean is2012Nexus7(CameraType type) {
        if (Build.HARDWARE.equals("grouper")) {
            if (type.equals(CameraType.FRONT)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
