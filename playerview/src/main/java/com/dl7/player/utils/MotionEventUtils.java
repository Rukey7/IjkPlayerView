package com.dl7.player.utils;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Rukey7 on 2016/11/21.
 */

public final class MotionEventUtils {

    private MotionEventUtils() {
        throw new AssertionError();
    }



    /**
     * Determine the space between the first two fingers
     */
    public static float calcSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    public static void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1) + event.getX(2);
        float y = event.getY(0) + event.getY(1) + event.getY(2);
        point.set(x / 3, y / 3);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    public static float rotation(MotionEvent event, PointF midPoint) {
//        double deltaX1 = event.getX(0) - midPoint.x;
//        double deltaX2 = event.getX(1) - midPoint.x;
//        double deltaX3 = event.getX(2) - midPoint.x;
//        double deltaY1 = event.getY(0) - midPoint.y;
//        double deltaY2 = event.getY(1) - midPoint.y;
//        double deltaY3 = event.getY(2) - midPoint.y;

        double deltaX1 = event.getX(0) - event.getX(2);
        double deltaX2 = event.getX(1) - event.getX(2);
        double deltaX3 = event.getX(2) - event.getX(2);
        double deltaY1 = event.getY(0) - midPoint.y;
        double deltaY2 = event.getY(1) - midPoint.y;
        double deltaY3 = event.getY(2) - midPoint.y;
        double radians1 = Math.atan2(deltaY1, deltaX1);
        double radians2 = Math.atan2(deltaY2, deltaX2);
        double radians3 = Math.atan2(deltaY3, deltaX3);
        float degree1 = (float) Math.toDegrees(radians1);
        float degree2 = (float) Math.toDegrees(radians2);
        float degree3 = (float) Math.toDegrees(radians3);
        Log.e("TTAG", degree1 + " - " + degree2 + " - " + degree3);
        return (float) Math.toDegrees(degree1 + degree2 + degree3);
    }
}
