package com.hcmus.easywork.views;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.AppCompatEditText;

public class PinCodeEditText extends AppCompatEditText {
    public PinCodeEditText(Context context) {
        super(context);
        customize();
    }

    public PinCodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        customize();
    }

    public PinCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        customize();
    }
    private void customize(){
        setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_NORMAL);
        setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
    }
}
