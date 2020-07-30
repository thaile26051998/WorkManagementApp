package com.hcmus.easywork.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

public class TextPlaceholder {
    /**
     * Global instance object
     */
    private static TextPlaceholder instance = new TextPlaceholder();
    /**
     * Resources to get value from string, color
     */
    private static Resources res;
    /**
     * Default format of placeholder
     */
    private final String format = "<![CDATA[%s: <b><font color=%s> %s</font></b>]]>";
    //Title, color, value as parameters
    private String title, color, value;

    /**
     * Default constructor is set to private, no implementation
     */
    private TextPlaceholder() {
        // No implementation
    }

    /**
     * Use Android context to initialize Resources
     *
     * @param context Context
     * @return TextPlaceholder instance
     */
    public static TextPlaceholder with(Context context) {
        if (res == null) {
            res = context.getResources();
        }
        return instance;
    }

    /**
     * Set title of placeholder from string resource
     *
     * @param resId @StringRes id of string
     * @return TextPlaceholder instance
     */
    public TextPlaceholder setTitle(@StringRes int resId) {
        instance.title = res.getString(resId);
        return instance;
    }

    /**
     * Set color of value from hex-string
     *
     * @param color Color as hex-string
     * @return TextPlaceholder instance
     */
    public TextPlaceholder setColor(String color) {
        instance.color = color;
        return instance;
    }

    /**
     * Set color of value from @ColorRes integer
     *
     * @param colorId @ColorRes id of color
     * @return TextPlaceholder instance
     */
    public TextPlaceholder setColor(@ColorRes int colorId) {
        int value = res.getColor(colorId);
        instance.color = String.format("#%06X", 0xFFFFFF & value);
        return instance;
    }

    /**
     * Set value from @StringRes id, used for state and priority
     *
     * @param resId @StringRes id
     * @return TextPlaceholder instance
     */
    public TextPlaceholder setValue(@StringRes int resId) {
        instance.value = res.getString(resId);
        return instance;
    }

    /**
     * Set value from string, used for approval
     *
     * @param value Value
     * @return TextPlaceholder instance
     */
    public TextPlaceholder setValue(String value) {
        instance.value = value;
        return instance;
    }

    /**
     * Apply text placeholder to TextView
     *
     * @param textView TextView
     */
    public void applyTo(TextView textView) {
        String result = String.format(instance.format, instance.title, instance.color, instance.value);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String finalResult = Html.fromHtml(result, Html.FROM_HTML_MODE_LEGACY).toString();
            textView.setText(Html.fromHtml(finalResult, Html.FROM_HTML_MODE_LEGACY));
        } else {
            String finalResult = Html.fromHtml(result).toString();
            textView.setText(Html.fromHtml(finalResult));
        }
    }
}
