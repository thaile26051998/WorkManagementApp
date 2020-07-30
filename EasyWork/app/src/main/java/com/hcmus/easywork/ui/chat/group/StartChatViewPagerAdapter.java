package com.hcmus.easywork.ui.chat.group;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.hcmus.easywork.R;
import com.hcmus.easywork.ui.common.FragmentPagerAdapter;

public class StartChatViewPagerAdapter extends FragmentPagerAdapter {
    public StartChatViewPagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(context, fm, new FragmentTab[]{
                new FragmentTab(R.string.tab_direct_message, new FragmentStartDirectMessage()),
                new FragmentTab(R.string.tab_group_chat, new FragmentCreateGroupChat())
        });
    }
}
