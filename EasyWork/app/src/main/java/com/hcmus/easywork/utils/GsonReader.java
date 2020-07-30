package com.hcmus.easywork.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hcmus.easywork.R;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class GsonReader<T> {
    private AssetManager assetManager;

    public GsonReader(Context context) {
        this.assetManager = context.getAssets();
    }

    public ArrayList<T> getFrom(Type type, String fileName) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(this.assetManager.open(fileName));
            //Type arrayType = new TypeToken<ArrayList<T>>() {}.getType();
            Type arrayType = TypeToken.getParameterized(ArrayList.class, type).getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat(R.string.format_full_time);
            return new Gson().fromJson(inputStreamReader, arrayType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<T>();
    }
}
