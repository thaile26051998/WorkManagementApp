package com.hcmus.easywork.ui.common.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.R;

public abstract class BaseBottomFragment<T extends ViewDataBinding> extends BottomSheetDialogFragment
        implements IBaseBottomFragment {
    protected AppCompatActivity activity;
    protected T binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            this.activity = ((AppCompatActivity) context);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onBasedViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.onBasedActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    public NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    public void makeToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public void makeToast(@StringRes int resId) {
        makeToast(activity.getString(resId));
    }

    public void makePopup(String message) {
        new MaterialAlertDialogBuilder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.popup_action_ok, null)
                .show();
    }

    public void makePopup(@StringRes int resId) {
        makePopup(activity.getString(resId));
    }

    public <V extends ViewModel> V createViewModel(Class<V> modelClass) {
        return new ViewModelProvider(activity).get(modelClass);
    }
}
