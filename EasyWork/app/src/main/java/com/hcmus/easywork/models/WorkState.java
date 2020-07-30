package com.hcmus.easywork.models;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.hcmus.easywork.R;

public class WorkState {
    @StringRes
    private int stateLabelId;
    @ColorRes
    private int stateColor;

    private WorkState(@StringRes int labelId, @ColorRes int color) {
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

    private static final WorkState NEW = new WorkState(R.string.state_new, R.color.state_new);
    private static final WorkState ACTIVE = new WorkState(R.string.state_active, R.color.state_active);
    private static final WorkState REVIEWING = new WorkState(R.string.state_reviewing, R.color.state_reviewing);
    private static final WorkState RESOLVED = new WorkState(R.string.state_resolved, R.color.state_resolved);
    private static final WorkState CLOSED = new WorkState(R.string.state_closed, R.color.state_closed);
    private static final WorkState OVERDUE = new WorkState(R.string.state_overdue, R.color.state_overdue);

    public static class Converter {
        public static WorkState from(Task.State state) {
            switch (state) {
                case New:
                    return WorkState.NEW;
                case Active:
                    return WorkState.ACTIVE;
                case Reviewing:
                    return WorkState.REVIEWING;
                case Resolved:
                    return WorkState.RESOLVED;
                case Closed:
                    return WorkState.CLOSED;
                case Overdue:
                    return WorkState.OVERDUE;
            }
            return WorkState.OVERDUE; // TODO: replace with OVERDUE
        }
    }
}
