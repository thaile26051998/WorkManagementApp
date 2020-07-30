package com.hcmus.easywork.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;
import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.LayoutDropDownListViewBinding;

import java.util.ArrayList;
import java.util.List;

public class DropDownListView<T> extends MaterialCardView {
    private LayoutDropDownListViewBinding binding;
    private Context mContext;
    private AdapterSpinner adapterSpinner;
    private OnItemSelectedListener<T> onItemSelectedListener;
    private List<T> items = new ArrayList<>();

    public DropDownListView(Context context) {
        this(context, null);
    }

    public DropDownListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.materialCardViewStyle);
    }

    public DropDownListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LayoutDropDownListViewBinding.inflate(inflater, this, true);
        adapterSpinner = new AdapterSpinner(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        binding.spinner.setAdapter(adapterSpinner);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(items.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSourceData(List<T> list, DisplayItem<? super T> displayItem) {
        items.clear();
        items.addAll(list);
        List<String> strings = new ArrayList<>();
        for (T t : list) {
            strings.add(displayItem.getDisplayName(t));
        }
        adapterSpinner.clear();
        adapterSpinner.addAll(strings);
        adapterSpinner.notifyDataSetChanged();
    }

    @Nullable
    public T getSelectedItem() {
        int position = binding.spinner.getSelectedItemPosition();
        int maxSize = items.size();
        if (position < 0 || position >= maxSize) return null;
        return items.get(position);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<T> listener) {
        this.onItemSelectedListener = listener;
    }

    static class AdapterSpinner extends ArrayAdapter<CharSequence> {
        AdapterSpinner(@NonNull Context context) {
            super(context, android.R.layout.simple_spinner_item);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
    }

    public interface DisplayItem<T> {
        String getDisplayName(T displayItem);
    }

    public interface OnItemSelectedListener<T> {
        void onItemSelected(T item);
    }
}
