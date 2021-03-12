package cse340.undo.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;

import cse340.undo.R;

/**
 * This is a subclass of AbstractColorPickerView, that is, this View implements a ColorPicker.
 *
 * There are several class fields, enums, callback classes, and helper functions which have
 * been implemented for you.
 *
 * PLEASE READ AbstractColorPickerView.java to learn about these.
 */
// Documentation:
// Color: https://developer.android.com/reference/android/graphics/Color
public class CircleColorPickerView extends cse340.undo.app.ColorPickerView {

    /**
     * Update the local model (color) for this colorpicker view
     *
     * @param x The x location that the user selected
     * @param y The y location that the user selected
     */
    protected void updateModel(float x, float y) {
        // TODO implement this
        // hint: we give you a very helpful function to call
        float angle = getTouchAngle(x, y);
        setColor(getColorFromAngle(angle));
    }

    /* ********************************************************************************************** *
     *                               <End of model declarations />
     * ********************************************************************************************** */

    /* ********************************************************************************************** *
     * You may create any constants you wish here.                                                     *
     * You may also create any fields you want, that are not necessary for the state but allow       *
     * for better optimized or cleaner code                                                           *
     * ********************************************************************************************** */
    /** Helper fields for keeping track of view geometry. */
    protected float mCenterX, mCenterY, mRadius;

    /** Ratio between radius of the thumb handle and mRadius, the radius of the wheel. */
    protected static final float RADIUS_TO_THUMB_RATIO = 0.085f;

    private float mCenterCircleRadius, mThumbRadius;

    private final Paint mThumbPaint;
    private final Paint mCenterPaint;

    /* ********************************************************************************************** *
     *                               <End of other fields and constants declarations />
     * ********************************************************************************************** */

    /**
     * Constructor of the ColorPicker View
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view. This value may be null.
     */
    public CircleColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.drawable.color_wheel);

        // TODO: Initialize variables as necessary (such as state)
        // Caching the paint objects is good style and avoids unnecessary computation.
        mState = State.START;
        mThumbPaint = new Paint();
        mThumbPaint.setColor(Color.WHITE);
        mCenterPaint = new Paint();
    }

    /**
     * Draw the ColorPicker on the Canvas
     * @param canvas the canvas that is drawn upon
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: draw the thumb and center circle
        mCenterPaint.setColor(mCurrentColor);
        canvas.drawCircle(mCenterX, mCenterY, mCenterCircleRadius, mCenterPaint);

        float[] thumbPosition = getAnglePosition(getAngleFromColor(mCurrentColor));
        int alpha = mState == State.INSIDE ? (int) (0.5f * 255) : 255;
        mThumbPaint.setAlpha(alpha);
        canvas.drawCircle(thumbPosition[0], thumbPosition[1], mThumbRadius, mThumbPaint);
    }

    /**
     * Called when this view should assign a size and position to all of its children.
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     */
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // TODO: calculate mRadius, mCenterX, and mCenterY based View dimensions
        // Hint: the ColorPicker view is not a square, base it off the min of the width and height
        mRadius = Math.min(bottom - top, right - left) / 2f;
        mCenterX = (right - left) / 2f;
        mCenterY = (bottom - top) / 2f;

        mThumbRadius = RADIUS_TO_THUMB_RATIO * mRadius;
        mCenterCircleRadius = mRadius - mThumbRadius * 2;
    }

    /**
     * Calculate the essential geometry given an event.
     *
     * @param event Motion event to compute geometry for, most likely a touch.
     * @return EssentialGeometry value.
     */
    @Override
    protected EssentialGeometry essentialGeometry(MotionEvent event) {
        // TODO: compute the geometry for the given event
        float dist = (float) Math.sqrt(Math.pow(mCenterX - event.getX(), 2) + Math.pow(mCenterY - event.getY(), 2));
        return dist <= mRadius ? EssentialGeometry.INSIDE : EssentialGeometry.OUTSIDE;
    }

    /* ********************************************************************************************** *
     *                               <Helper Functions />
     * ********************************************************************************************** */

    /**
     * Converts from a color to angle on the wheel.
     *
     * @param color RGB color as integer.
     * @return Position of this color on the wheel in radians.
     * @see #getTouchAngle(float, float)
     */
    public static float getAngleFromColor(int color) {
        float[] HSL = new float[3];
        ColorUtils.colorToHSL(color, HSL);
        return (float) Math.toRadians(HSL[0] - 90f);
    }

    /**
     * Converts from an angle to a color on the wheel.
     *
     * @param angle position on the wheel in radians
     * @return color at this position on the wheel.
     * @see #getTouchAngle(float, float)
     */
    public static @ColorInt int getColorFromAngle(double angle) {
        float degrees = (float) (Math.toDegrees(angle)) + 90f;
        if (degrees < 0f) {
            degrees += 360f;
        }
        float[] HSV = {degrees, 1f, 1f};
        return Color.HSVToColor(HSV);
    }

    /***
     * Calculate the angle of the selection on color wheel given a touch.
     *
     * @param touchX Horizontal position of the touch event.
     * @param touchY Vertical position of the touch event.
     * @return Angle of the touch, in radians.
     */
    protected float getTouchAngle(float touchX, float touchY) {
        // NOTE: This function REQUIRES that you properly use mCenterX, mCenterY, etc.

        // Assumes (for cardinal directions on the color wheel):
        // [ E => 0, South => Pi/2, W => -Pi, N => -Pi/2 ]

        // However, you can override this function in CircleColorPickerView
        // with your own angle mappings if you desire.
        return (float) Math.atan2(touchY - mCenterY, touchX - mCenterX);
    }

    /***
     * Calculate the thumb location based on the angle of selection on the color wheel
     *
     * @param angle position on the wheel in radians
     * @return horizontal and vertical positions of the thumb [touchX, touchY]
     */
    protected float[] getAnglePosition(double angle) {
        float hyp = mRadius - mThumbRadius;
        float dx = (float) (Math.cos(angle)) * hyp;
        float dy = (float) (Math.sin(angle)) * hyp;
        return new float[] {mCenterY + dx, mCenterY + dy};
    }
}
