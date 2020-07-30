package com.hcmus.easywork.ui.common.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;

public interface IFragment {
    /**
     * Get fragment layout
     *
     * @return Layout ID
     */
    @LayoutRes
    int getLayoutId();

    /**
     * Get NavController from fragment
     *
     * @return NavController
     */
    NavController getNavController();

    /**
     * Method called in onViewCreated, force all fragments to follow
     *
     * @param view               View
     * @param savedInstanceState Bundle
     */
    void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

    /**
     * Method called in onActivityCreated, force all fragments to follow
     *
     * @param savedInstanceState Bundle
     */
    void onBasedActivityCreated(@Nullable Bundle savedInstanceState);
}
