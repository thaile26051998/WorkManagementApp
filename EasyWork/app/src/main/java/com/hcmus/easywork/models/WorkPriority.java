package com.hcmus.easywork.models;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.hcmus.easywork.R;

public class WorkPriority {
    @StringRes
    private int priorityId;
    @ColorRes
    private int priorityColor;

    public WorkPriority(@StringRes int id, @ColorRes int color) {
        this.priorityId = id;
        this.priorityColor = color;
    }

    @StringRes
    public int getLabel() {
        return priorityId;
    }

    @ColorRes
    public int getColor() {
        return priorityColor;
    }

    private static final WorkPriority HIGH = new WorkPriority(R.string.priority_high, R.color.priority_high);
    private static final WorkPriority MEDIUM = new WorkPriority(R.string.priority_medium, R.color.priority_medium);
    private static final WorkPriority LOW = new WorkPriority(R.string.priority_low, R.color.priority_low);
    private static final WorkPriority UNDEFINED = new WorkPriority(R.string.priority_undefined, R.color.priority_undefined);

    public static class Converter {
        public static WorkPriority from(Task.Priority priority) {
            switch (priority) {
                case Low: {
                    return WorkPriority.LOW;
                }
                case Medium: {
                    return WorkPriority.MEDIUM;
                }
                case High: {
                    return WorkPriority.HIGH;
                }
            }
            return WorkPriority.UNDEFINED;
        }
    }
}
