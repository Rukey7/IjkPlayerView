package com.dl7.playerview.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by long on 2016/11/4.
 */

public final class AnimHelper {

    private AnimHelper() {
        throw new AssertionError();
    }


    public static void doSlideRightIn(View view, int startX, int endX, int duration) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        set.playTogether(translationX, alpha);
        set.start();
    }
}
