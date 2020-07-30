package com.hcmus.easywork.ui.chat.group;

import android.content.Context;
import android.util.AttributeSet;

import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.views.DropDownListView;

public class DropDownListProject extends DropDownListView<Project> {
    public DropDownListProject(Context context) {
        super(context);
    }

    public DropDownListProject(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropDownListProject(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
