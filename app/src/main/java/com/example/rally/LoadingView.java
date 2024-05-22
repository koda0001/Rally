package com.example.rally;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadingView extends LinearLayout {

    private static final int FADE_TIME = 250;
    private static final int DELAY_TIME = 500;
    private static final float MAX_ALPHA = 0.3f;

    private View mImageView;
    private View mRootView;
    private TextView mTextView;
    private ObjectAnimator mAnimator;
    private TimeInterpolator mLinearInterpolator;

    private boolean mShowing;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.loading_view, this, true);

        mImageView = findViewById(R.id.imgProgress);
        mRootView = findViewById(R.id.root);
        mTextView = findViewById(R.id.progressTextView);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading);
        mImageView.startAnimation(animation);

        mLinearInterpolator = new LinearInterpolator();
        mRootView.setAlpha(0);
        setVisibility(GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mShowing;
    }

    public void show() {
        ((Activity) getContext()).runOnUiThread(() -> {
            if (!mShowing) {
                mShowing = true;
                if (mAnimator == null) {
                    mAnimator = ObjectAnimator.ofFloat(mRootView, "alpha", 0, MAX_ALPHA);
                    mAnimator.setDuration(FADE_TIME);
                } else {
                    float currentProgress = (float) mAnimator.getAnimatedValue();
                    mAnimator.removeAllListeners();
                    mAnimator.end();
                    mAnimator = ObjectAnimator.ofFloat(mRootView, "alpha", currentProgress, MAX_ALPHA);
                    mAnimator.setDuration((long) (FADE_TIME * (MAX_ALPHA - currentProgress) / MAX_ALPHA));
                }
                mAnimator.setInterpolator(mLinearInterpolator);
                mAnimator.setStartDelay(getVisibility() == GONE ? DELAY_TIME : 0);
                mAnimator.start();
            }
            setVisibility(VISIBLE);
        });
    }

    public void hide() {
        ((Activity) getContext()).runOnUiThread(() -> {
            if (mShowing) {
                mShowing = false;
                if (mAnimator == null) {
                    mAnimator = ObjectAnimator.ofFloat(mRootView, "alpha", MAX_ALPHA, 0);
                    mAnimator.setDuration(FADE_TIME);
                } else {
                    float currentProgress = (float) mAnimator.getAnimatedValue();
                    mAnimator.removeAllListeners();
                    mAnimator.end();
                    mAnimator = ObjectAnimator.ofFloat(mRootView, "alpha", currentProgress, 0);
                    long duration = (long) (FADE_TIME * currentProgress / MAX_ALPHA);
                    if (duration > 0 && duration < 200) {
                        duration = 200;
                    }
                    mAnimator.setDuration(duration);

                }
                mAnimator.setInterpolator(mLinearInterpolator);
                mAnimator.addListener(mHideAnimationListener);
                mAnimator.start();
            }
        });
    }
    ObjectAnimator.AnimatorListener mHideAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setVisibility(GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
}

