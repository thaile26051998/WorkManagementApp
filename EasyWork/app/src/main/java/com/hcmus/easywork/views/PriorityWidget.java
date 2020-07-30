package com.hcmus.easywork.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.LayoutPriorityWidgetBinding;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.models.WorkPriority;

public class PriorityWidget extends MaterialCardView {
    private Context mContext;
    private LayoutPriorityWidgetBinding binding;
    private int mIntPriority;
    private Task.Priority mPriority;

    public PriorityWidget(Context context) {
        this(context, null);
    }

    public PriorityWidget(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialCardViewStyle);
    }

    public PriorityWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PriorityWidget);
        this.mIntPriority = typedArray.getInt(R.styleable.PriorityWidget_value, 0);
        typedArray.recycle();

        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LayoutPriorityWidgetBinding.inflate(inflater, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setRadius(mContext.getResources().getDimensionPixelSize(R.dimen.margin_medium));
        setStrokeWidth(7);
        setClickable(true);
        setFocusable(true);
        setValue(Task.Priority.values()[this.mIntPriority]);
    }

    public Task.Priority getValue() {
        return this.mPriority;
    }

    public void setPriority(WorkPriority workPriority) {
        binding.title.setText(workPriority.getLabel());
        int value = ContextCompat.getColor(mContext, workPriority.getColor());
        binding.title.setTextColor(value);
        setStrokeColor(value);
    }

    // Annotated method for attribute 'value'
    public void setValue(Task.Priority priority) {
        this.mPriority = priority;
        setPriority(WorkPriority.Converter.from(priority));
    }
}
