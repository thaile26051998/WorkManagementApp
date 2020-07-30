package com.hcmus.easywork.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewStubProxy;

public class UtilsView {
    private static final int animationTime = 300;

    /**
     * Change visibility of a view to GONE
     *
     * @param view View
     */
    public static void hide(@NonNull final View view) {
        view.setAlpha(1f);
        view.setTranslationY(0);
        view.animate()
                .setDuration(animationTime)
                .translationY(view.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0f)
                .start();
    }

    /**
     * Change visibility of a view to VISIBLE
     *
     * @param view View
     */
    public static void show(@NonNull View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        view.setTranslationY(view.getHeight());
        view.animate()
                .setDuration(animationTime)
                .translationY(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
    }

    /**
     * Switch visibility of a view between GONE and VISIBLE
     *
     * @param view View
     */
    public static void toggleVisibility(@NonNull View view) {
        if (view.getVisibility() == View.VISIBLE) {
            hide(view);
        } else
            show(view);
    }

    public static void inflateViewStub(ViewStubProxy proxy) {
        if (proxy.getViewStub() != null) {
            proxy.getViewStub().inflate();
            proxy.getRoot().setVisibility(View.VISIBLE);
        }
    }

    public static void hideViewStub(ViewStubProxy proxy) {
        if (proxy.isInflated()) {
            proxy.getRoot().setVisibility(View.GONE);
        }
    }
}
