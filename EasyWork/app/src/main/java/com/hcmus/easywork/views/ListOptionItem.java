package com.hcmus.easywork.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.google.android.material.card.MaterialCardView;
import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.LayoutListOptionItemBinding;

public class ListOptionItem extends MaterialCardView {
    private Context mContext;
    private LayoutListOptionItemBinding binding;
    private String mTitle;
    @DrawableRes
    private int mStartIcon;

    public ListOptionItem(Context context) {
        this(context, null);
    }

    public ListOptionItem(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialCardViewStyle);
    }

    public ListOptionItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ListOptionItem);
        this.mTitle = typedArray.getString(R.styleable.ListOptionItem_title);
        this.mStartIcon = typedArray.getResourceId(R.styleable.ListOptionItem_startIcon, 0);
        typedArray.recycle();

        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LayoutListOptionItemBinding.inflate(inflater, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setRadius(0);
        setCardElevation(0);
        setClickable(true);
        setFocusable(true);
        binding.title.setText(this.mTitle);
        binding.icon.setImageResource(this.mStartIcon);
    }

    public void setTitle(@StringRes int title) {
        setTitle(mContext.getString(title));
    }

    public void setTitle(String title) {
        this.mTitle = title;
        binding.title.setText(mTitle);
    }
}
