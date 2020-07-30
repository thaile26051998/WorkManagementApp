package com.hcmus.easywork.ui.chat.message;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.DialogChatMessageBinding;
import com.hcmus.easywork.ui.common.fragment.BaseBottomFragment;

public class DialogChatMessage extends BaseBottomFragment<DialogChatMessageBinding> {
    private OnMenuSelectedListener onMenuSelectedListener;

    @Override
    public int getLayoutId() {
        return R.layout.dialog_chat_message;
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.view.setNavigationItemSelectedListener(item -> {
            if (onMenuSelectedListener != null) {
                onMenuSelectedListener.onMenuSelected(item.getItemId());
            }
            dismiss();
            return true;
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {

    }

    public void setOnMenuSelectedListener(OnMenuSelectedListener onMenuSelectedListener) {
        this.onMenuSelectedListener = onMenuSelectedListener;
    }

    public interface OnMenuSelectedListener {
        void onMenuSelected(int menuId);
    }
}
