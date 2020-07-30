package com.hcmus.easywork.ui.profile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentAboutBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;

public class FragmentAbout extends BaseFragment<FragmentAboutBinding> {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(l -> getNavController().navigateUp());
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {

    }
}
