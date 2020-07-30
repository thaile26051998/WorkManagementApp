package com.hcmus.easywork.ui.task;

import android.content.Context;
import android.util.AttributeSet;

import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.views.DropDownListView;

public class DropDownListPriority extends DropDownListView<Task.Priority> {
    public DropDownListPriority(Context context) {
        super(context);
    }

    public DropDownListPriority(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropDownListPriority(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
