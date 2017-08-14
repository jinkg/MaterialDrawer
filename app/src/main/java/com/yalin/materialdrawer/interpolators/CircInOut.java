package com.yalin.materialdrawer.interpolators;

import android.animation.TimeInterpolator;

/**
 * @author jinyalin
 * @since 2017/8/14.
 */

public class CircInOut implements TimeInterpolator {

    @Override
    public float getInterpolation(float t) {
        if ((t *= 2) < 1) return -0.5f * ((float) Math.sqrt(1 - t * t) - 1);
        return 0.5f * ((float) Math.sqrt(1 - (t -= 2) * t) + 1);
    }
}