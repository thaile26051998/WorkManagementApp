package com.hcmus.easywork.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * This class wraps Glide library for shorter code and easier maintenance
 */
public class ImageLoadingLibrary {
    public static LoadingInstance useContext(Context context) {
        LoadingInstance loadingInstance = new LoadingInstance();
        loadingInstance.useContext(context);
        return loadingInstance;
    }

    public static Bitmap convertToBitMap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static class LoadingInstance {
        private Context mContext;
        @SuppressWarnings("rawtypes")
        private SourceStrategy sourceStrategy;

        void useContext(Context context) {
            this.mContext = context;
        }

        public LoadingInstance load(Uri uri) {
            this.sourceStrategy = new SourceStrategy<>(uri);
            return this;
        }

        public LoadingInstance load(String url) {
            this.sourceStrategy = new SourceStrategy<>(url);
            return this;
        }

        public LoadingInstance load(Bitmap bitmap) {
            this.sourceStrategy = new SourceStrategy<>(bitmap);
            return this;
        }

        public LoadingInstance load(byte[] data) {
            this.sourceStrategy = new SourceStrategy<>(ImageLoadingLibrary.convertToBitMap(data));
            return this;
        }

        public void into(ImageView view) {
            Glide.with(mContext)
                    .load(sourceStrategy.get())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().placeholder(null).fitCenter())
                    .into(view);
        }
    }

    public static class SourceStrategy<T> {
        private T mSource;

        public SourceStrategy(T source) {
            this.mSource = source;
        }

        public T get() {
            return this.mSource;
        }
    }
}
