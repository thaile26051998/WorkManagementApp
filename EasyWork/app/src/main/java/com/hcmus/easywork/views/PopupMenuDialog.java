package com.hcmus.easywork.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;

@SuppressLint("RestrictedApi")
public class PopupMenuDialog {
    private PopupMenu popupMenu;
    private MenuPopupHelper menuPopupHelper;

    private PopupMenuDialog() {
        // Prevent nullable initialization
    }

    public PopupMenuDialog(@NonNull Context context, @MenuRes int menuRes, @NonNull View anchorView) {
        this.popupMenu = new PopupMenu(context, anchorView);
        this.popupMenu.inflate(menuRes);
        this.menuPopupHelper = new MenuPopupHelper(context, (MenuBuilder) popupMenu.getMenu(), anchorView);
    }

    public void setViewGravity(int gravity) {
        this.menuPopupHelper.setForceShowIcon(true);
        this.menuPopupHelper.setGravity(gravity);
    }

    public void setOnMenuItemClickListener(@Nullable PopupMenu.OnMenuItemClickListener listener) {
        this.popupMenu.setOnMenuItemClickListener(listener);
    }

    public void show() {
        this.menuPopupHelper.show();
    }
}
