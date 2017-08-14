package com.yalin.materialdrawer.interpolators;

import android.animation.TimeInterpolator;

/**
 * @author jinyalin
 * @since 2017/8/14.
 */

public class QuintOut implements TimeInterpolator {
    @Override
    public float getInterpolation(float t) {
        return (t -= 1) * t * t * t * t + 1;
    }
}
