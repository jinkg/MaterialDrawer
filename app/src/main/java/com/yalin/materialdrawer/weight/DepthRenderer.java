package com.yalin.materialdrawer.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.yalin.materialdrawer.R;

/**
 * @author jinyalin
 * @since 2017/8/14.
 */

public class DepthRenderer extends RelativeLayout {

    private Paint shadowPaint = new Paint();
    private NinePatchDrawable softShadow;
    private Drawable roundSoftShadow;

    private Path edgePath = new Path();

    public DepthRenderer(Context context) {
        super(context);
        setup();

    }

    public DepthRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public DepthRenderer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public float getTopEdgeLength(DepthLayout dl) {
        return getDistance(dl.getTopLeftBack(), dl.getTopRightBack());
    }

    public float getTopEdgeLength(DepthFAB dl) {
        return getDistance(dl.getTopLeftBack(), dl.getTopRightBack());
    }

    float getDistance(PointF p1, PointF p2) {
        return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    public float getShadowAlpha() {
        return shadowAlpha;
    }

    public void setShadowAlpha(float shadowAlpha) {
        this.shadowAlpha = Math.min(1f, Math.max(0, shadowAlpha));
    }

    private float shadowAlpha = 0.3f;

    void setup() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child instanceof DepthLayout) {
                        boolean hasChangedBounds = ((DepthLayout) child).calculateBounds();
                        if (hasChangedBounds)
                            invalidate();
                    }
                    if (child instanceof DepthFAB) {
                        boolean hasChangeBounds = ((DepthFAB) child).calculateBounds();
                        if (hasChangeBounds)
                            invalidate();
                    }
                }
                return true;
            }
        });

        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAntiAlias(true);
        softShadow = (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shadow);
        roundSoftShadow = ContextCompat.getDrawable(getContext(), R.drawable.round_soft_shadow);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child instanceof DepthLayout && !isInEditMode()) {
            DepthLayout dl = (DepthLayout) child;

            float[] src = new float[]{0, 0, dl.getWidth(), 0, dl.getWidth(), dl.getHeight(), 0, dl.getHeight()};
            if (dl.isCircle()) {
                dl.getCustomShadow().drawShadow(canvas, dl, roundSoftShadow);
                if (Math.abs(dl.getRotationX()) > 1 || Math.abs(dl.getRotationY()) > 1)
                    drawCornerBaseShape(dl, canvas, src);
            } else {
                dl.getCustomShadow().drawShadow(canvas, dl, softShadow);
                if (dl.getRotationX() != 0 || dl.getRotationY() != 0) {
                    if (getLongestHorizontalEdge(dl) > getLongestVerticalEdge(dl))
                        drawVerticalFirst(dl, canvas, src);
                    else
                        drawHorizontalFist(dl, canvas, src);
                }
            }
        }

        if (child instanceof DepthFAB && !isInEditMode()) {
            DepthFAB dl = (DepthFAB) child;

            float[] src = new float[]{0, 0, dl.getWidth(), 0, dl.getWidth(), dl.getHeight(), 0, dl.getHeight()};
            if (dl.isCircle()) {
                dl.getCustomShadow().drawShadow(canvas, dl, roundSoftShadow);
                if (Math.abs(dl.getRotationX()) > 1 || Math.abs(dl.getRotationY()) > 1)
                    drawCornerBaseShape(dl, canvas, src);
            } else {
                dl.getCustomShadow().drawShadow(canvas, dl, softShadow);
                if (dl.getRotationX() != 0 || dl.getRotationY() != 0) {
                    if (getLongestHorizontalEdge(dl) > getLongestVerticalEdge(dl))
                        drawVerticalFirst(dl, canvas, src);
                    else
                        drawHorizontalFist(dl, canvas, src);
                }
            }
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    private void drawCornerBaseShape(DepthLayout dl, Canvas canvas, float[] src) {
        float[] dst = new float[]{dl.getTopLeftBack().x, dl.getTopLeftBack().y,
                dl.getTopRightBack().x, dl.getTopRightBack().y, dl.getBottomRightBack().x,
                dl.getBottomRightBack().y, dl.getBottomLeftBack().x, dl.getBottomLeftBack().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        canvas.concat(matrix);
        edgePath.reset();

        if (Build.VERSION.SDK_INT >= 21) {
            edgePath.addRoundRect(0, 0, dl.getWidth(),
                    dl.getHeight(), dl.getWidth() / 2f, dl.getHeight() / 2f, Path.Direction.CCW);
        }

        canvas.drawPath(edgePath, dl.getEdgePaint());
        shadowPaint.setAlpha((int) (shadowAlpha * 0.5f * 255));
        canvas.drawPath(edgePath, shadowPaint);

        canvas.restoreToCount(count);
    }

    private void drawCornerBaseShape(DepthFAB dl, Canvas canvas, float[] src) {
        float[] dst = new float[]{dl.getTopLeftBack().x, dl.getTopLeftBack().y,
                dl.getTopRightBack().x, dl.getTopRightBack().y, dl.getBottomRightBack().x,
                dl.getBottomRightBack().y, dl.getBottomLeftBack().x, dl.getBottomLeftBack().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        canvas.concat(matrix);
        edgePath.reset();
        if (Build.VERSION.SDK_INT >= 21) {
            edgePath.addRoundRect(0, 0, dl.getWidth(), dl.getHeight(),
                    dl.getWidth() / 2f, dl.getHeight() / 2f, Path.Direction.CCW);
        }
        canvas.drawPath(edgePath, dl.getEdgePaint());
        shadowPaint.setAlpha((int) (shadowAlpha * 0.5f * 255));
        canvas.drawPath(edgePath, shadowPaint);

        canvas.restoreToCount(count);
    }


    private void drawHorizontalFist(DepthLayout dl, Canvas canvas, float[] src) {
        if (getLeftEdgeLength(dl) <= getRightEdgeLength(dl)) {
            drawLeftEdge(dl, canvas, src);
        } else {
            drawRightEdge(dl, canvas, src);
        }

        drawTopEdge(dl, canvas, src);
        drawBottomEdge(dl, canvas, src);

        if (getLeftEdgeLength(dl) >= getRightEdgeLength(dl)) {
            drawLeftEdge(dl, canvas, src);
        } else {
            drawRightEdge(dl, canvas, src);
        }
    }

    private void drawHorizontalFist(DepthFAB dl, Canvas canvas, float[] src) {
        if (getLeftEdgeLength(dl) <= getRightEdgeLength(dl)) {
            drawLeftEdge(dl, canvas, src);
        } else {
            drawRightEdge(dl, canvas, src);
        }

        drawTopEdge(dl, canvas, src);
        drawBottomEdge(dl, canvas, src);

        if (getLeftEdgeLength(dl) >= getRightEdgeLength(dl)) {
            drawLeftEdge(dl, canvas, src);
        } else {
            drawRightEdge(dl, canvas, src);
        }
    }

    private void drawVerticalFirst(DepthLayout dl, Canvas canvas, float[] src) {

        if (getTopEdgeLength(dl) <= getBottomEdgeLength(dl)) {
            drawTopEdge(dl, canvas, src);
        } else {
            drawBottomEdge(dl, canvas, src);
        }

        drawLeftEdge(dl, canvas, src);
        drawRightEdge(dl, canvas, src);


        if (getTopEdgeLength(dl) >= getBottomEdgeLength(dl)) {
            drawTopEdge(dl, canvas, src);
        } else {
            drawBottomEdge(dl, canvas, src);
        }

    }

    private void drawVerticalFirst(DepthFAB dl, Canvas canvas, float[] src) {

        if (getTopEdgeLength(dl) <= getBottomEdgeLength(dl)) {
            drawTopEdge(dl, canvas, src);
        } else {
            drawBottomEdge(dl, canvas, src);
        }

        drawLeftEdge(dl, canvas, src);
        drawRightEdge(dl, canvas, src);


        if (getTopEdgeLength(dl) >= getBottomEdgeLength(dl)) {
            drawTopEdge(dl, canvas, src);
        } else {
            drawBottomEdge(dl, canvas, src);
        }
    }

    float getLongestHorizontalEdge(DepthLayout dl) {
        float topEdgeLength = getTopEdgeLength(dl);
        float bottomEdgeLength = getBottomEdgeLength(dl);
        if (topEdgeLength > bottomEdgeLength) {
            return topEdgeLength;
        } else {
            return bottomEdgeLength;
        }
    }

    float getLongestHorizontalEdge(DepthFAB dl) {
        float topEdgeLength = getTopEdgeLength(dl);
        float bottomEdgeLength = getBottomEdgeLength(dl);
        if (topEdgeLength > bottomEdgeLength) {
            return topEdgeLength;
        } else {
            return bottomEdgeLength;
        }
    }

    float getLongestVerticalEdge(DepthLayout dl) {
        float leftEdgeLength = getLeftEdgeLength(dl);
        float rightEdgeLength = getRightEdgeLength(dl);
        if (leftEdgeLength > rightEdgeLength) {
            return leftEdgeLength;
        } else {
            return rightEdgeLength;
        }
    }

    float getLongestVerticalEdge(DepthFAB dl) {
        float leftEdgeLength = getLeftEdgeLength(dl);
        float rightEdgeLength = getRightEdgeLength(dl);
        if (leftEdgeLength > rightEdgeLength) {
            return leftEdgeLength;
        } else {
            return rightEdgeLength;
        }
    }

    private float getRightEdgeLength(DepthLayout dl) {
        return getDistance(dl.getTopRightBack(), dl.getBottomRightBack());
    }

    private float getLeftEdgeLength(DepthLayout dl) {
        return getDistance(dl.getTopLeftBack(), dl.getBottomLeftBack());
    }


    private float getBottomEdgeLength(DepthLayout dl) {
        return getDistance(dl.getBottomLeftBack(), dl.getBottomRightBack());
    }

    private float getRightEdgeLength(DepthFAB dl) {
        return getDistance(dl.getTopRightBack(), dl.getBottomRightBack());
    }

    private float getLeftEdgeLength(DepthFAB dl) {
        return getDistance(dl.getTopLeftBack(), dl.getBottomLeftBack());
    }


    private float getBottomEdgeLength(DepthFAB dl) {
        return getDistance(dl.getBottomLeftBack(), dl.getBottomRightBack());
    }


    void drawShadow(PointF point1, PointF point2, float correctionValue, Canvas canvas, DepthLayout dl) {
        float angle = Math.abs(Math.abs(getAngle(point1, point2)) + correctionValue);
        float alpha = angle / 180f;
        shadowPaint.setAlpha((int) (alpha * 255f * shadowAlpha));

        canvas.drawRect(0, 0, dl.getWidth(), dl.getHeight(), shadowPaint);
    }

    void drawShadow(PointF point1, PointF point2, float correctionValue, Canvas canvas, DepthFAB dl) {
        float angle = Math.abs(Math.abs(getAngle(point1, point2)) + correctionValue);
        float alpha = angle / 180f;
        shadowPaint.setAlpha((int) (alpha * 255f * shadowAlpha));

        canvas.drawRect(0, 0, dl.getWidth(), dl.getHeight(), shadowPaint);
    }


    private void drawRectancle(DepthLayout dl, Canvas canvas) {
        canvas.drawRect(0, 0, dl.getWidth(), dl.getHeight(), dl.getEdgePaint());
    }

    private void drawRectancle(DepthFAB dl, Canvas canvas) {
        canvas.drawRect(0, 0, dl.getWidth(), dl.getHeight(), dl.getEdgePaint());
    }

    public float getAngle(PointF point1, PointF point2) {
        float angle = (float) Math.toDegrees(Math.atan2(point1.y - point2.y, point1.x - point2.x));

        return angle;
    }

    private void drawLeftEdge(DepthLayout dl, Canvas canvas, float[] src) {
        float[] dst = new float[]{dl.getTopLeft().x, dl.getTopLeft().y, dl.getTopLeftBack().x, dl.getTopLeftBack().y, dl.getBottomLeftBack().x, dl.getBottomLeftBack().y, dl.getBottomLeft().x, dl.getBottomLeft().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        canvas.concat(matrix);
        drawRectancle(dl, canvas);
        drawShadow(dl.getTopLeft(), dl.getBottomLeft(), 0, canvas, dl);

        canvas.restoreToCount(count);

    }

    private void drawLeftEdge(DepthFAB dl, Canvas canvas, float[] src) {
        float[] dst = new float[]{dl.getTopLeft().x, dl.getTopLeft().y, dl.getTopLeftBack().x, dl.getTopLeftBack().y, dl.getBottomLeftBack().x, dl.getBottomLeftBack().y, dl.getBottomLeft().x, dl.getBottomLeft().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        canvas.concat(matrix);
        drawRectancle(dl, canvas);
        drawShadow(dl.getTopLeft(), dl.getBottomLeft(), 0, canvas, dl);

        canvas.restoreToCount(count);

    }

    private void drawRightEdge(DepthLayout dl, Canvas canvas, float[] src) {
        float[] dst = new float[]{dl.getTopRight().x, dl.getTopRight().y, dl.getTopRightBack().x, dl.getTopRightBack().y, dl.getBottomRightBack().x, dl.getBottomRightBack().y, dl.getBottomRight().x, dl.getBottomRight().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        canvas.concat(matrix);
        drawRectancle(dl, canvas);
        drawShadow(dl.getTopRight(), dl.getBottomRight(), -180f, canvas, dl);
        canvas.restoreToCount(count);
    }

    private void drawRightEdge(DepthFAB dl, Canvas canvas, float[] src) {
        float[] dst = new float[]{dl.getTopRight().x, dl.getTopRight().y, dl.getTopRightBack().x, dl.getTopRightBack().y, dl.getBottomRightBack().x, dl.getBottomRightBack().y, dl.getBottomRight().x, dl.getBottomRight().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        canvas.concat(matrix);
        drawRectancle(dl, canvas);
        drawShadow(dl.getTopRight(), dl.getBottomRight(), -180f, canvas, dl);
        canvas.restoreToCount(count);
    }

    android.graphics.Matrix matrix = new android.graphics.Matrix();

    private void drawTopEdge(DepthLayout dl, Canvas canvas, float[] src) {

        float[] dst = new float[]{dl.getTopLeft().x, dl.getTopLeft().y, dl.getTopRight().x, dl.getTopRight().y, dl.getTopRightBack().x, dl.getTopRightBack().y, dl.getTopLeftBack().x, dl.getTopLeftBack().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        canvas.concat(matrix);
        drawRectancle(dl, canvas);
        drawShadow(dl.getTopLeft(), dl.getTopRight(), -180f, canvas, dl);
        canvas.restoreToCount(count);
    }

    private void drawTopEdge(DepthFAB dl, Canvas canvas, float[] src) {

        float[] dst = new float[]{dl.getTopLeft().x, dl.getTopLeft().y, dl.getTopRight().x, dl.getTopRight().y, dl.getTopRightBack().x, dl.getTopRightBack().y, dl.getTopLeftBack().x, dl.getTopLeftBack().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        canvas.concat(matrix);
        drawRectancle(dl, canvas);
        drawShadow(dl.getTopLeft(), dl.getTopRight(), -180f, canvas, dl);
        canvas.restoreToCount(count);
    }

    private void drawBottomEdge(DepthLayout dl, Canvas canvas, float[] src) {

        float[] dst = new float[]{dl.getBottomLeft().x, dl.getBottomLeft().y, dl.getBottomRight().x, dl.getBottomRight().y, dl.getBottomRightBack().x, dl.getBottomRightBack().y, dl.getBottomLeftBack().x, dl.getBottomLeftBack().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, dst.length >> 1);
        canvas.concat(matrix);
        drawRectancle(dl, canvas);
        drawShadow(dl.getBottomLeft(), dl.getBottomRight(), 0, canvas, dl);
        canvas.restoreToCount(count);
    }

    private void drawBottomEdge(DepthFAB dl, Canvas canvas, float[] src) {

        float[] dst = new float[]{dl.getBottomLeft().x, dl.getBottomLeft().y, dl.getBottomRight().x, dl.getBottomRight().y, dl.getBottomRightBack().x, dl.getBottomRightBack().y, dl.getBottomLeftBack().x, dl.getBottomLeftBack().y};
        int count = canvas.save();
        matrix.setPolyToPoly(src, 0, dst, 0, dst.length >> 1);
        canvas.concat(matrix);
        drawRectancle(dl, canvas);
        drawShadow(dl.getBottomLeft(), dl.getBottomRight(), 0, canvas, dl);
        canvas.restoreToCount(count);
    }
}