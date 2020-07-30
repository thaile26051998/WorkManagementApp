package com.hcmus.easywork.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.LayoutDateWidgetBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateWidget extends ConstraintLayout {
    private LayoutDateWidgetBinding binding;
    private Context mContext;
    private OnDatePickedListener onDatePickedListener;

    private String mTitle;
    private SimpleDateFormat mDateFormat;
    private Calendar mMinCalendar, mCurrentCalendar;

    public DateWidget(Context context) {
        this(context, null);
    }

    public DateWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // TODO: import value from attrs
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DateWidget);
        mTitle = typedArray.getString(R.styleable.DateWidget_date_title);
        typedArray.recycle();

        // Initialize with context
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LayoutDateWidgetBinding.inflate(inflater, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // Set widget title
        binding.txtTitle.setText(mTitle);

        // SimpleDateFormat
        this.mDateFormat = new SimpleDateFormat(mContext.getString(R.string.format_standard_date), Locale.getDefault());

        // By default, min date is today
        this.mMinCalendar = Calendar.getInstance();

        // Set on click listener
        binding.icDate.setOnClickListener(v -> {
            // Display date by default, min date or selected date
            final int selectedDay, selectedMonth, selectedYear;
            if (mCurrentCalendar != null) {
                selectedDay = mCurrentCalendar.get(Calendar.DAY_OF_MONTH);
                selectedMonth = mCurrentCalendar.get(Calendar.MONTH);
                selectedYear = mCurrentCalendar.get(Calendar.YEAR);
            } else {
                selectedDay = mMinCalendar.get(Calendar.DAY_OF_MONTH);
                selectedMonth = mMinCalendar.get(Calendar.MONTH);
                selectedYear = mMinCalendar.get(Calendar.YEAR);
            }

            DatePickerDialog dpDialog = new DatePickerDialog(mContext, (view, year, month, dayOfMonth) -> {
                // Result picked by user
                Calendar pickedDate = Calendar.getInstance(Locale.getDefault());
                pickedDate.set(year, month, dayOfMonth);
                // Check for listener
                if (onDatePickedListener != null) {
                    boolean accepted = onDatePickedListener.onDatePicked(pickedDate.getTime());
                    setCurrentDate(accepted ? pickedDate : null);
                } else {
                    // Listener is not initialized or null, picked date will be displayed by default
                    setCurrentDate(pickedDate);
                }
            }, selectedYear, selectedMonth, selectedDay);
            dpDialog.getDatePicker().setMinDate(mMinCalendar.getTimeInMillis());
            dpDialog.show();
        });
    }

    public void setCurrentDate(Date date) {
        if (date != null) {
            this.mCurrentCalendar = Calendar.getInstance();
            this.mCurrentCalendar.setTime(date);
            String dateString = this.mDateFormat.format(date.getTime());
            binding.txtDate.setText(dateString);
        } else {
            this.mCurrentCalendar = null;
            binding.txtDate.setText(null);
        }
    }

    public void setCurrentDate(Calendar calendar) {
        if (calendar != null) {
            setCurrentDate(calendar.getTime());
        } else {
            setCurrentDate((Date) null);
        }
    }

    public Date getCurrentDate() {
        if (this.mCurrentCalendar == null)
            return null;
        return this.mCurrentCalendar.getTime();
    }

    public String getCurrentDateString(Date date) {
        if (this.mCurrentCalendar == null)
            return null;
        return this.mDateFormat.format(date);
    }

    public void setOnDatePickedListener(OnDatePickedListener onDatePickedListener) {
        this.onDatePickedListener = onDatePickedListener;
    }

    public interface OnDatePickedListener {
        /**
         * Determine if picked date is accepted or not
         *
         * @param pickedDate Date
         * @return boolean. If true, value will be set. Otherwise, value will be ignored
         */
        boolean onDatePicked(Date pickedDate);
    }
}
