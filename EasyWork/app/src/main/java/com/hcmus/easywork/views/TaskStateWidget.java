package com.hcmus.easywork.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.LayoutStateWidgetBinding;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.models.WorkState;

public class TaskStateWidget extends MaterialCardView {
    private Context mContext;
    private LayoutStateWidgetBinding binding;
    private int mIntState;
    private Task.State mState;

    public TaskStateWidget(Context context) {
        this(context, null);
    }

    public TaskStateWidget(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialCardViewStyle);
    }

    public TaskStateWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TaskStateWidget);
        this.mIntState = typedArray.getInt(R.styleable.TaskStateWidget_state, 0);
        typedArray.recycle();

        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LayoutStateWidgetBinding.inflate(inflater, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setRadius(mContext.getResources().getDimensionPixelSize(R.dimen.margin_medium));
        setStrokeWidth(7);
        setClickable(true);
        setFocusable(true);
        setState(Task.State.values()[this.mIntState]);
    }

    public Task.State getState() {
        return this.mState;
    }

    public void setState(WorkState workState) {
        binding.title.setText(workState.getLabel());
        int value = ContextCompat.getColor(mContext, workState.getColor());
        binding.title.setTextColor(value);
        setStrokeColor(value);
    }

    public void setState(Task.State state) {
        this.mState = state;
        setState(WorkState.Converter.from(state));
    }
}
