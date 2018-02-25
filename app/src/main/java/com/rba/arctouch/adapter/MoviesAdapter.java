package com.rba.arctouch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rba.arctouch.R;
import com.rba.arctouch.commons.Constants;
import com.rba.arctouch.model.Genre;
import com.rba.arctouch.model.Movie;
import com.rba.arctouch.singleton.ConfigSingleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private Context mContext;
    private List<Movie> moviesList;

    public MoviesAdapter(Context mContext, List<Movie> moviesList) {
        this.mContext = mContext;
        this.moviesList = moviesList;
    }

    @Override
    public int getItemViewType(int position) {
        return moviesList.get(position) != null ? 1 : 0;
    }

    public List<Movie> getList() {
        return moviesList;
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder {

        private ImageView movieBackdropPath;
        private TextView title;
        private TextView genres;
        private TextView releaseDate;

        private MoviesViewHolder(View itemView) {
            super(itemView);
            movieBackdropPath = itemView.findViewById(R.id.movie_backdrop_path);
            title = itemView.findViewById(R.id.movie_title);
            genres = itemView.findViewById(R.id.movie_genres);
            releaseDate = itemView.findViewById(R.id.movie_release_date);
        }
    }

    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new MoviesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MoviesViewHolder holder, int position) {

        Movie movie = moviesList.get(position);
        String imageUrl = Constants.BASE_IMAGES_URL + Constants.W342 + moviesList.get(position).getPosterPath();
        if (movie != null) {
            Glide.with(mContext).load(imageUrl).into(holder.movieBackdropPath);
            holder.title.setText(movie.getTitle());
            holder.releaseDate.setText(movie.getReleaseDate());
            holder.genres.setText(Movie.getGenresList(movie, mContext));
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
