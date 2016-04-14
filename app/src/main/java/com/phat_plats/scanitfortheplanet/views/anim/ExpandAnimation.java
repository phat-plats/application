package com.phat_plats.scanitfortheplanet.views.anim;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by Gareth Daunton on 4/13/2016.
 */
public class ExpandAnimation extends Animation {

    private final float mStartWeight;
    private final float mDeltaWeight;
    private final ViewGroup context;
    private boolean shrink;

    public ExpandAnimation(ViewGroup context, float startWeight, float endWeight) {
        mStartWeight = startWeight;
        this.context = context;
        mDeltaWeight = endWeight - startWeight;
        shrink = mDeltaWeight < 0.0;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) context.getLayoutParams();
        lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
        if (!shrink)
            context.setAlpha(0 + interpolatedTime);
        else
            context.setAlpha(1 - interpolatedTime);
        context.setLayoutParams(lp);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}