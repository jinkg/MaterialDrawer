package com.yalin.materialdrawer.interpolators;

import android.animation.TimeInterpolator;

/**
 * @author jinyalin
 * @since 2017/8/14.
 */

public class QuadInOut implements TimeInterpolator {
    @Override
    public float getInterpolation(float t) {
        if ((t *= 2) < 1) return 0.5f * t * t;
        return -0.5f * ((--t) * (t - 2) - 1);
    }
}
