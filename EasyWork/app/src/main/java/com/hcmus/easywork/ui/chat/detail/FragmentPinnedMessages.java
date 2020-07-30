package com.hcmus.easywork.ui.chat.detail;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentPinnedMessagesBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;

public class FragmentPinnedMessages extends BaseFragment<FragmentPinnedMessagesBinding> {
    @Override
    public int getLayoutId() {
        return R.layout.fragment_pinned_messages;
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {

    }
}
