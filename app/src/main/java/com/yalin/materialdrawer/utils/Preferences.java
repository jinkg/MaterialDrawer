package com.yalin.materialdrawer.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * @author jinyalin
 * @since 2017/8/14.
 */

public class Preferences {
    public static void makeAppFullscreen(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(color);
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
