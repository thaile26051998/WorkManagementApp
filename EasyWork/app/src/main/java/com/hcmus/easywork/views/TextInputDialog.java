package com.hcmus.easywork.views;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.databinding.TextInputDialogBinding;

public class TextInputDialog {
    private View connectedView;
    private OnTextSubmitted onTextSubmitted;

    public void bind() {
        connectedView.setOnClickListener(l -> new CustomDialogBuilder(connectedView)
                .setOnTextSubmitted(onTextSubmitted)
                    .setTitle("Type text here:")
//                    .setMessage("Message")
                .setPositiveButton("Ok", (dialog, which) -> {
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .show());
    }

    public static class Builder {
        private TextInputDialog dialog = new TextInputDialog();

        public Builder setConnectedView(View view) {
            dialog.connectedView = view;
            return this;
        }

        public TextInputDialog setOnTextSubmitted(OnTextSubmitted onTextSubmitted) {
            dialog.onTextSubmitted = onTextSubmitted;
            return dialog;
        }
    }

    public interface OnTextSubmitted {
        void onTextSubmitted(String text);
    }

    private class CustomDialogBuilder extends MaterialAlertDialogBuilder {
        private TextInputDialogBinding binding;
        private OnTextSubmitted onTextSubmitted;

        CustomDialogBuilder(View view) {
            super(view.getContext());
            Context context = view.getContext();
            binding = TextInputDialogBinding.inflate(LayoutInflater.from(context));
            setView(binding.getRoot());
            setCancelable(true);
            if (view instanceof TextView) {
                binding.txtField.setText(((TextView) view).getText());
            }
        }

        @NonNull
        @Override
        public MaterialAlertDialogBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            return super.setPositiveButton(text, (dialog, which) -> {
                String submittedText = binding.txtField.getEditableText().toString();
                onTextSubmitted.onTextSubmitted(submittedText);
                ((TextView) connectedView).setText(submittedText);
            });
        }

        MaterialAlertDialogBuilder setOnTextSubmitted(OnTextSubmitted onTextSubmitted) {
            this.onTextSubmitted = onTextSubmitted;
            return this;
        }
    }
}
