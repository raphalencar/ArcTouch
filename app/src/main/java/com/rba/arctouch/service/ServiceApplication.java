package com.rba.arctouch.service;

import android.app.Application;

import com.rba.arctouch.commons.Constants;

public class ServiceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Service.getInstance().init(this);
    }

    public static String getMoviesUrl(int page) {
        return Constants.MOVIES_URL + page;
    }

    public static String getSearchMovieUrl(int page, String query) {
        StringBuilder builder = new StringBuilder();
        builder.append(query);
        builder.append(Constants.PAGE);
        builder.append(page);
        return Constants.SEARCH_URL + builder;
    }

    public static String getBackdropSizes() {
        return Constants.CONFIG_URL;
    }

    public static String getGenres() {
        return Constants.GENRES_URL;
    }
}
