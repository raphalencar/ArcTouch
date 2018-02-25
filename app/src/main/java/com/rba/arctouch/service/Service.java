package com.rba.arctouch.service;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Service {

    private static RequestQueue mRequestQueue;
    private static Service mInstance;
    private static Context mApplicationContext;

    private Service() {
        super();
    }

    public static Service getInstance() {
        if (mInstance == null) {
            mInstance = new Service();
        }
        return mInstance;
    }

    public void init(Application application) {
        if (mApplicationContext == null) {
            mApplicationContext = application.getApplicationContext();
            mRequestQueue = Volley.newRequestQueue(mApplicationContext);
        }
    }

    public static void getMovies(int page, final ServiceRequestListener listener) {
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET,
                        getMoviesUrl(page),
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                listener.onResponseObject(response);
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
                    }
                });
        mRequestQueue.add(request);
    }

    public static void getBackdropSizes(final ServiceRequestListener listener) {
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET,
                        getBackdropSizes(),
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                listener.onResponseObject(response);
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
                    }
                });
        mRequestQueue.add(request);
    }

    public static void getSearchMovieUrl(int page, String query, final ServiceRequestListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getSearchMovieUrl(page, query),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponseObject(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });
        mRequestQueue.add(request);
    }

    public static void getGenres(final ServiceRequestListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getGenresUrl(),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponseObject(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });
        mRequestQueue.add(request);
    }

    private static String getMoviesUrl(int page) {
        return ServiceApplication.getMoviesUrl(page);
    }

    private static String getBackdropSizes() {
        return ServiceApplication.getBackdropSizes();
    }

    private static String getSearchMovieUrl(int page, String query) {
        return ServiceApplication.getSearchMovieUrl(page, query);
    }

    private static String getGenresUrl() {
        return ServiceApplication.getGenres();
    }
}
