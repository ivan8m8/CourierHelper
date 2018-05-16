package ru.courierhelper.behaviours;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

public class FloatingActionButtonAnimationBehaviour extends FloatingActionButton.Behavior {

    /**
     * In this project the animation is not that smooth.
     * It can hang up a bit in the very bottom while scrolling.
     * I have successfully found a mistake. (3.03 12:20 AM)
     * The mistake is NOT LATEST LIBRARIES in the gradle file!
     * The animation doesn't hang up no more with the 27 build tools!
     * The 27th build tools were tested in 'My Test App'
     */

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    public FloatingActionButtonAnimationBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            int fabBottomMargin = layoutParams.bottomMargin;
            child.animate().translationY(child.getHeight() + fabBottomMargin).setInterpolator(INTERPOLATOR).start();
        } else if (dyConsumed < 0) {
            child.animate().translationY(0).setInterpolator(INTERPOLATOR).start();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}
