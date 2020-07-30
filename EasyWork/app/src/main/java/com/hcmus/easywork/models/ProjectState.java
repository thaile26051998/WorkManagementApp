package com.hcmus.easywork.models;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.hcmus.easywork.R;

public class ProjectState {
    @StringRes
    private int stateLabelId;
    @ColorRes
    private int stateColor;

    private ProjectState(@StringRes int labelId, @ColorRes int color) {
        this.stateLabelId = labelId;
        this.stateColor = color;
    }

    @StringRes
    public int getLabel() {
        return stateLabelId;
    }

    @ColorRes
    public int getColor() {
        return stateColor;
    }

    private static final ProjectState ACTIVE = new ProjectState(R.string.state_active, R.color.state_active);
    private static final ProjectState COMPLETED = new ProjectState(R.string.state_completed, R.color.state_overdue);

    public static class Converter {
        public static ProjectState from(Project.State state) {
            switch (state) {
                case Active:
                    return ProjectState.ACTIVE;
                case Completed:
                    return ProjectState.COMPLETED;
            }
            return ProjectState.COMPLETED;
        }
    }
}
