package com.rba.arctouch;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rba.arctouch.commons.Constants;
import com.rba.arctouch.model.Movie;
import com.rba.arctouch.singleton.ConfigSingleton;

public class MovieDetailsActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private ImageView movieBackdrop;
    private TextView movieTitle;
    private TextView movieReleaseDate;
    private TextView movieGenres;
    private TextView movieOverview;
    private ConfigSingleton configSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        init();
    }

    private void init() {

        configSingleton = ConfigSingleton.getInstance(this);
        relativeLayout = findViewById(R.id.relative_details);
        progressBar = findViewById(R.id.progressbarDetails);
        movieBackdrop = findViewById(R.id.movie_details_poster_path);
        movieTitle = findViewById(R.id.movie_details_title);
        movieReleaseDate = findViewById(R.id.movie_details_release_date);
        movieGenres = findViewById(R.id.movie_details_genres);
        movieOverview = findViewById(R.id.movie_details_overview);

        getIntentFromMainActivity();
    }

    private void getIntentFromMainActivity() {
        Movie movie = new Movie();
        Intent it = getIntent();
        if (it.hasExtra(Constants.TAG_MOVIE)) {
            movie = it.getParcelableExtra(Constants.TAG_MOVIE);
        }
        setMovieDetails(movie);
    }

    private void setMovieDetails(Movie movie) {
        int width = getDisplayConfig();
        String imageUrl = Constants.BASE_IMAGES_URL + getCurrentBackdropSize(width) + movie.getBackdropPath();
        Glide.with(getApplicationContext()).load(imageUrl).into(movieBackdrop);
        movieTitle.setText(movie.getTitle());
        movieReleaseDate.setText(movie.getReleaseDate());
        movieGenres.setText(Movie.getGenresList(movie, getApplicationContext()));
        movieOverview.setText(movie.getOverview());
    }

    private String getCurrentBackdropSize(int width) {
        String size = "";
        if (width < 300 ||
                (width > 300 && width < 500)) {
            size = configSingleton.getBackdropSizes()[0];
        } else if (width > 500 && width < 780) {
            size = configSingleton.getBackdropSizes()[1];
        } else if (width > 780 && width < 1280 ||
                width > 1280) {
            size = configSingleton.getBackdropSizes()[2];
        }

        return size;
    }

    private int getDisplayConfig() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }


}
