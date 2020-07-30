package com.hcmus.easywork.utils;

import android.os.Build;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class UtilsTextView {
    /**
     * Set HTML encoded string for TextView
     *
     * @param textView TextView
     * @param text     Content of the string
     */
    public static void setText(@NonNull TextView textView, String text) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }
    }
}
