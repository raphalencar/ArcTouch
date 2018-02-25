package com.rba.arctouch;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rba.arctouch.commons.Constants;
import com.rba.arctouch.listener.SnackbarListener;
import com.rba.arctouch.model.Genre;
import com.rba.arctouch.model.Movie;
import com.rba.arctouch.service.Service;
import com.rba.arctouch.service.ServiceRequestListener;
import com.rba.arctouch.singleton.ConfigSingleton;
import com.rba.arctouch.utils.ConnectionUtils;
import com.rba.arctouch.utils.SnackbarUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {

    private ConfigSingleton configSingleton;
    private LinearLayout linearLayout;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
    }

    private void init() {
        configSingleton = ConfigSingleton.getInstance(this);
        linearLayout = findViewById(R.id.linear_layout_splash);
        initGson();
        loadBackdropSizes();
    }

    private void initGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    private void loadBackdropSizes() {
        if (ConnectionUtils.isOnline(this)) {
            Service.getBackdropSizes(new ServiceRequestListener() {
                @Override
                public void onResponseObject(JSONObject response) {
                    if (response != null) {
                        try {
                            JSONArray backdropSizes = (JSONArray) response.getJSONObject(Constants.TAG_IMAGES)
                                    .get(Constants.TAG_BACKDROP_SIZES);
                            configSingleton = ConfigSingleton.getInstance(getApplicationContext());
                            configSingleton.setBackdropSizes(toStringArray(backdropSizes));
                            loadGenres();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    SnackbarUtils.showSnackBar(linearLayout, error);
                }
            });
        } else {
            SnackbarUtils.showNoConnectionSnackBar(linearLayout, getResources().getString(R.string.no_connection),
                    new SnackbarListener() {
                        @Override
                        public void onResponse(boolean retry) {
                            if (retry) {
                                loadBackdropSizes();
                            }
                        }
                    });
        }
    }

    private void loadGenres() {
        if (ConnectionUtils.isOnline(this)) {
            Service.getGenres(new ServiceRequestListener() {
                @Override
                public void onResponseObject(JSONObject response) {
                    if (response != null) {
                        JSONArray jsonArray = response.optJSONArray(Constants.TAG_GENRES);
                        if (jsonArray != null) {
                            List<Genre> genres = Arrays.asList(gson.fromJson(jsonArray.toString(), Genre[].class));
                            configSingleton.getGenreList().addAll(genres);
                            callMainActivity();
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    SnackbarUtils.showSnackBar(linearLayout, error);
                }
            });
        } else {
            SnackbarUtils.showNoConnectionSnackBar(linearLayout, getResources().getString(R.string.no_connection),
                    new SnackbarListener() {
                        @Override
                        public void onResponse(boolean retry) {
                            if (retry) {
                                loadGenres();
                            }
                        }
                    });
        }
    }

    private void callMainActivity() {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        finish();
    }

    public static String[] toStringArray(JSONArray array) {
        if (array == null)
            return null;

        String[] arr = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.optString(i);
        }
        return arr;
    }
}
