package com.rba.arctouch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rba.arctouch.adapter.MoviesAdapter;
import com.rba.arctouch.commons.Constants;
import com.rba.arctouch.listener.EndlessRecyclerViewScrollListener;
import com.rba.arctouch.listener.RecyclerItemClickListener;
import com.rba.arctouch.listener.SnackbarListener;
import com.rba.arctouch.model.Movie;
import com.rba.arctouch.service.Service;
import com.rba.arctouch.service.ServiceRequestListener;
import com.rba.arctouch.utils.ConnectionUtils;
import com.rba.arctouch.utils.RecyclerUtils;
import com.rba.arctouch.utils.SnackbarUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private RecyclerView moviesRecycleView;
    private ProgressBar progressBar;
    private MoviesAdapter mAdapter;
    private ArrayList<Movie> moviesList = new ArrayList<>();
    private Gson gson;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Parcelable listState;
    private GridLayoutManager mLayoutManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        relativeLayout = findViewById(R.id.relative_layout_movies);
        progressBar = findViewById(R.id.progressbar);
        moviesRecycleView = findViewById(R.id.recycler_view_movies);
        initGson();
        setRecycleView();
        if (savedInstanceState != null) {
            restoreRecycleViewState();
        } else {
            loadMovies(1);
        }
    }

    private void initGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    private void setRecycleView() {
        mLayoutManager = new GridLayoutManager(this, 2);
        moviesRecycleView.setLayoutManager(mLayoutManager);
        moviesRecycleView.addItemDecoration(new RecyclerUtils.GridSpacingItemDecoration(2,
                RecyclerUtils.dpToPx(getApplicationContext(), 10),
                true));
        moviesRecycleView.setItemAnimator(new DefaultItemAnimator());
        moviesRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                callMovieDetailsActivity(moviesList.get(position));
            }
        }));
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMovies(page);
            }
        };
        moviesRecycleView.addOnScrollListener(scrollListener);
    }

    private void callMovieDetailsActivity(Movie movie) {
        Intent it = new Intent(this, MovieDetailsActivity.class);
        it.putExtra(Constants.TAG_MOVIE, movie);
        startActivity(it);
    }

    private void restoreRecycleViewState() {
        moviesList = getArrayList(Constants.MOVIES_LIST);
        scrollListener.setCurrentPage(getPage(Constants.CURRENT_PAGE));
        setRecycleViewAdapter();
        progressBar.setVisibility(View.GONE);
    }

    private void loadMovies(final int page) {
        progressBar.setVisibility(View.VISIBLE);
        if (ConnectionUtils.isOnline(this)) {
            Service.getMovies(page, new ServiceRequestListener() {
                @Override
                public void onResponseObject(JSONObject response) {
                    if (response != null) {
                        JSONArray jsonArray = response.optJSONArray(Constants.TAG_RESULTS);
                        if (jsonArray != null) {
                            List<Movie> movies = Arrays.asList(gson.fromJson(jsonArray.toString(), Movie[].class));
                            moviesList.addAll(movies);
                            setRecycleViewAdapter();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {
                    progressBar.setVisibility(View.GONE);
                    SnackbarUtils.showSnackBar(relativeLayout, error);
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            SnackbarUtils.showNoConnectionSnackBar(relativeLayout, getResources().getString(R.string.no_connection),
                    new SnackbarListener() {
                        @Override
                        public void onResponse(boolean retry) {
                            if (retry) {
                                loadMovies(page);
                            }
                        }
                    });
        }
    }

    private void setRecycleViewAdapter() {
        if (mAdapter == null) {
            mAdapter = new MoviesAdapter(getApplicationContext(), moviesList);
            moviesRecycleView.setAdapter(mAdapter);
        } else {
            final int curSize = mAdapter.getItemCount();
            moviesRecycleView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemRangeInserted(curSize,
                            moviesList.size() - 1);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        listState = mLayoutManager.onSaveInstanceState();
        state.putParcelable(Constants.LIST_STATE, listState);
        saveArrayListAndPage(moviesList, Constants.MOVIES_LIST,
                scrollListener.getCurrentPage(), Constants.CURRENT_PAGE);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state != null) {
            listState = state.getParcelable(Constants.LIST_STATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listState != null) {
            mLayoutManager.onRestoreInstanceState(listState);
        }
    }

    public void saveArrayListAndPage(ArrayList<Movie> list, String key, int page, String pageKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.putInt(pageKey, page);
        editor.apply();
    }

    public ArrayList<Movie> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Movie>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public int getPage(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getInt(key, 1);
    }

    @Override
    protected void onStop() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent it = new Intent(getApplicationContext(), FindMovieActivity.class);
                it.putExtra(Constants.QUERY, query);
                startActivity(it);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
