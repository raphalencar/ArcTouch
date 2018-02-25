package com.rba.arctouch.singleton;

import android.content.Context;

import com.rba.arctouch.model.Genre;

import java.util.ArrayList;

public class ConfigSingleton {

    private static ConfigSingleton mInstance;
    private static Context mContext;
    private String[] backdropSizes;
    private ArrayList<Genre> genreList = new ArrayList<>();

    public ConfigSingleton(Context context) {
        this.mContext = context;
    }

    public static synchronized ConfigSingleton getInstance(Context context){
        if(mInstance == null){
            mInstance = new ConfigSingleton(context);
        }

        return mInstance;
    }

    public String[] getBackdropSizes() {
        return backdropSizes;
    }

    public void setBackdropSizes(String[] backdropSizes) {
        this.backdropSizes = backdropSizes;
    }

    public ArrayList<Genre> getGenreList() {
        return genreList;
    }

    public void setGenreList(ArrayList<Genre> genreList) {
        this.genreList = genreList;
    }
}
