package com.hcmus.easywork.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.easywork.R;

/**
 * <p>Extended version of MaterialToolBar that supports back navigation with default navigation icon.</p>
 * <p><b>Do not</b> use this instead of default <code>MaterialToolBar</code> if fragment does not support navigating up</p>
 */
public class ExtendedMaterialToolBar extends MaterialToolbar {
    @DrawableRes
    private final int mIconNavigateBack = R.drawable.ic_toolbar_navigate_back;
    private boolean mSupportNavigateBack;
    private boolean mSupportNavigateForward;
    private OnNavigationForwardListener mOnNavigationForwardListener;

    public ExtendedMaterialToolBar(@NonNull Context context) {
        this(context, null);
    }

    public ExtendedMaterialToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.toolbarStyle);
    }

    public ExtendedMaterialToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExtendedMaterialToolBar);
        // TODO: revert to false
        this.mSupportNavigateBack = typedArray.getBoolean(R.styleable.ExtendedMaterialToolBar_support_navigate_back, true);
        this.mSupportNavigateForward = typedArray.getBoolean(R.styleable.ExtendedMaterialToolBar_support_navigate_forward, false);
        typedArray.recycle();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        int color = ContextCompat.getColor(this.getContext(), typedValue.resourceId);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(5f);
        canvas.drawLine(0f, getHeight(), getWidth(), getHeight(), paint);
        canvas.save();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setTitleTextAppearance(getContext(), R.style.Toolbar_TitleText);
        if (mSupportNavigateBack) {
            setNavigationIcon(this.mIconNavigateBack);
        }

        if (mSupportNavigateForward) {
            inflateMenu(R.menu.menu_navigation_forward);
            setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_navigate_forward && mOnNavigationForwardListener != null) {
                    mOnNavigationForwardListener.onNavigateForward();
                    return true;
                }
                return false;
            });
        }
    }

    public void setSupportNavigateBack(boolean support) {
        this.mSupportNavigateBack = support;
        if (this.mSupportNavigateBack) {
            setNavigationIcon(this.mIconNavigateBack);
        } else {
            setNavigationIcon(null);
        }
    }

    public void setSupportNavigateForward(boolean support) {
        this.mSupportNavigateForward = support;
    }


    public void setOnNavigationForwardListener(OnNavigationForwardListener onNavigationForwardListener) {
        this.mOnNavigationForwardListener = onNavigationForwardListener;
    }

    public interface OnNavigationForwardListener {
        void onNavigateForward();
    }
}
