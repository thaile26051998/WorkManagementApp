package com.hcmus.easywork.ui.common.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.hcmus.easywork.R;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment
        implements IBaseFragment {
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

    @Override
    public NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    public void makeToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public void makeToast(@StringRes int resId) {
        makeToast(activity.getString(resId));
    }


    public void makePopup(String message, DialogInterface.OnClickListener listener) {
        new MaterialAlertDialogBuilder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.popup_action_ok, listener)
                .show();
    }

    public void makePopup(String message) {
        makePopup(message, null);
    }

    public void makePopup(@StringRes int resId) {
        makePopup(activity.getString(resId), null);
    }

    public void makePopup(@StringRes int resId, DialogInterface.OnClickListener listener) {
        makePopup(activity.getString(resId), listener);
    }

    public void makeSnack(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setAction(R.string.snack_bar_dismiss, v1 -> {
                }).show();
    }

    public void makeSnack(@StringRes int resId) {
        makeSnack(getString(resId));
    }

    public AlertDialog createOkCancelDialog(@StringRes int contentId, @NonNull DialogInterface.OnClickListener okListener,
                                            @Nullable DialogInterface.OnClickListener cancelListener) {
        return createOkCancelDialog(getString(contentId), okListener, cancelListener);
    }

    public AlertDialog createOkCancelDialog(String content, @NonNull DialogInterface.OnClickListener okListener,
                                            @Nullable DialogInterface.OnClickListener cancelListener) {
        return new MaterialAlertDialogBuilder(activity)
                .setTitle(content)
                .setPositiveButton(R.string.popup_action_ok, okListener)
                .setNegativeButton(R.string.popup_action_cancel, cancelListener)
                .create();
    }

    public <V extends ViewModel> V createViewModel(Class<V> modelClass) {
        return new ViewModelProvider(activity).get(modelClass);
    }
}
