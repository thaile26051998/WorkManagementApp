package com.hcmus.easywork.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.LayoutStateWidgetBinding;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.ProjectState;

public class ProjectStateWidget extends MaterialCardView {
    private Context mContext;
    private LayoutStateWidgetBinding binding;
    private int mIntState;
    private Project.State mState;

    public ProjectStateWidget(Context context) {
        this(context, null);
    }

    public ProjectStateWidget(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialCardViewStyle);
    }

    public ProjectStateWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProjectStateWidget);
        this.mIntState = typedArray.getInt(R.styleable.ProjectStateWidget_project_state, 0);
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
        setState(Project.State.values()[this.mIntState]);
    }

    public Project.State getState() {
        return this.mState;
    }

    public void setState(ProjectState projectState) {
        binding.title.setText(projectState.getLabel());
        int value = ContextCompat.getColor(mContext, projectState.getColor());
        binding.title.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        binding.title.setBackgroundColor(value);
        setStrokeColor(value);
    }

    public void setState(Project.State state) {
        this.mState = state;
        setState(ProjectState.Converter.from(state));
    }
}
