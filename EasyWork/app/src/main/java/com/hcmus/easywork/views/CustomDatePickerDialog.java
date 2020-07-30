package com.hcmus.easywork.views;

import android.app.DatePickerDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <p>The CustomDatePickerDialog class uses a <code>DatePickerDialog</code> to remove boilerplate code.</p>
 * <p>Cannot directly initialize an instance of this class, should use class {@link Builder} instead.</p>
 */
public class CustomDatePickerDialog {
    private Context context;
    private OnDatePickedListener onDatePickedListener;
    private Calendar minDate, maxDate;

    private CustomDatePickerDialog(@NonNull Context context) {
        this.context = context;
    }

    private void show() {
        // Set default day, month, year
        int currentDay, currentMonth, currentYear;
        if (minDate != null) {
            currentDay = minDate.get(Calendar.DAY_OF_MONTH);
            currentMonth = minDate.get(Calendar.MONTH);
            currentYear = minDate.get(Calendar.YEAR);
        } else {
            Calendar c = Calendar.getInstance();
            currentDay = c.get(Calendar.DAY_OF_MONTH);
            currentMonth = c.get(Calendar.MONTH);
            currentYear = c.get(Calendar.YEAR);
        }
        // Create new DatePickerDialog with provided context, listener and default date
        DatePickerDialog dpDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            // Invoke picked day, month, year
            onDatePickedListener.onDatePicked(dayOfMonth, month, year);
            Calendar pickedDate = Calendar.getInstance(Locale.getDefault());
            pickedDate.set(year, month, dayOfMonth);
            // Invoke picked date as Date
            onDatePickedListener.onDatePicked(pickedDate.getTime());
        }, currentYear, currentMonth, currentDay);
        // Set min date
        if (minDate != null) {
            dpDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }
        if (maxDate != null) {
            dpDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        }
        // Show dialog
        dpDialog.show();
    }

    public interface OnDatePickedListener {
        void onDatePicked(int day, int month, int year);

        void onDatePicked(Date pickedDate);
    }

    /**
     * <p>The Builder class supports the <code>CustomDatePickerDialog</code> class.</p>
     * <p>First, initialize Builder class with an app context using constructor {@link #Builder(Context)}.</p>
     * <p>Respectively, call {@link #setMinDate(Calendar)} to set min date,
     * {@link #setOnDatePickedListener(OnDatePickedListener)} to set listener,
     * and finally call {@link #show()} to show dialog.</p>
     */
    public static class Builder {
        private CustomDatePickerDialog customDpDialog;

        public Builder(Context context) {
            customDpDialog = new CustomDatePickerDialog(context);
        }

        public Builder setOnDatePickedListener(OnDatePickedListener onDatePickedListener) {
            customDpDialog.onDatePickedListener = onDatePickedListener;
            return this;
        }

        public Builder setMinDate(Calendar minDate) {
            customDpDialog.minDate = minDate;
            return this;
        }

        public Builder setMaxDate(Calendar maxDate) {
            customDpDialog.maxDate = maxDate;
            return this;
        }

        public void show() {
            customDpDialog.show();
        }
    }
}
