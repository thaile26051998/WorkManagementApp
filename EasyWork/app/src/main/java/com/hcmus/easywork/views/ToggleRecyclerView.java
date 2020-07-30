package com.hcmus.easywork.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.LayoutToggleRecyclerViewBinding;

public class ToggleRecyclerView extends LinearLayout {
    private LayoutToggleRecyclerViewBinding binding;
    private Context mContext;
    private String mTitle;

    public ToggleRecyclerView(Context context) {
        this(context, null);
    }

    public ToggleRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleRecyclerView);
        this.mTitle = typedArray.getString(R.styleable.ToggleRecyclerView_recycler_view_title);
        typedArray.recycle();

        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LayoutToggleRecyclerViewBinding.inflate(inflater, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation(VERTICAL);
        binding.btnToggle.setText(mTitle);
        binding.btnToggle.setOnCheckedChangeListener((buttonView, isChecked) ->
                binding.recyclerView.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        getRecyclerView().setAdapter(adapter);
    }

    public RecyclerView getRecyclerView() {
        return this.binding.recyclerView;
    }

    public void expand() {
        binding.btnToggle.setChecked(true);
    }
}
