package com.yalin.materialdrawer.interpolators;

import android.animation.TimeInterpolator;

/**
 * @author jinyalin
 * @since 2017/8/14.
 */

public class ExpoIn implements TimeInterpolator {
    @Override
    public float getInterpolation(float t) {
        return (t == 0) ? 0 : (float) Math.pow(2, 10 * (t - 1));
    }
}
