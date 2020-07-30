package com.hcmus.easywork.ui.common;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    protected Context mContext;
    protected FragmentTab[] tabs;

    public FragmentPagerAdapter(Context context, @NonNull FragmentManager fm, FragmentTab[] tabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mContext = context;
        this.tabs = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return this.tabs[position].fragment;
    }

    @Override
    public int getCount() {
        return this.tabs.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.mContext.getString(this.tabs[position].tabTitle);
    }

    public static class FragmentTab {
        @StringRes
        public final int tabTitle;
        public final Fragment fragment;

        public FragmentTab(@StringRes int tabTitle, @NonNull Fragment fragment) {
            this.tabTitle = tabTitle;
            this.fragment = fragment;
        }
    }
}
