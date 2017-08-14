package com.yalin.materialdrawer.weight;

import android.animation.TypeEvaluator;
import android.graphics.RectF;

/**
 * @author jinyalin
 * @since 2017/8/14.
 */

public class RectFEvaluator implements TypeEvaluator<RectF> {

    @Override
    public RectF evaluate(float fraction, RectF startValue, RectF endValue) {

        return new RectF(startValue.left + (int) ((endValue.left - startValue.left) * fraction),
                startValue.top + (int) ((endValue.top - startValue.top) * fraction),
                startValue.right + (int) ((endValue.right - startValue.right) * fraction),
                startValue.bottom + (int) ((endValue.bottom - startValue.bottom) * fraction));
    }

}