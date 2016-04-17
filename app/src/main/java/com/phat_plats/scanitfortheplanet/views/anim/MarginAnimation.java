package com.phat_plats.scanitfortheplanet.views.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MarginAnimation extends Animation {
    private final float mStartMargin;
    private final float mDeltaMargin;
    private final View context;

    public MarginAnimation(View view, float startMargin, float endMargin) {
        mStartMargin = startMargin;
        this.context = view;
        mDeltaMargin = endMargin - startMargin;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)context.getLayoutParams();
        int newMargin = (int)(mStartMargin + (mDeltaMargin * interpolatedTime));
        params.leftMargin = newMargin;
        params.rightMargin = newMargin;
        context.setLayoutParams(params);
    }
}
