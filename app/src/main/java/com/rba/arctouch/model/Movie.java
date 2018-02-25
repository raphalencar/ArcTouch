package com.rba.arctouch.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.rba.arctouch.singleton.ConfigSingleton;

import java.util.ArrayList;

public class Movie implements Parcelable {
    private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("poster_path")
    private String posterPath;
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("genre_ids")
    private ArrayList<Integer> genreList;

    public Movie() {
        genreList = new ArrayList<>();
    }

    protected Movie(Parcel in) {
        title = in.readString();
        backdropPath = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        genreList = in.readArrayList(Integer.class.getClassLoader());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArrayList<Integer> getGenreList() {
        return genreList;
    }

    public void setGenreList(ArrayList<Integer> genreList) {
        this.genreList = genreList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeList(genreList);
    }

    public static String getGenresList(Movie movie, Context context) {
        String SEPARATOR = " / ";
        StringBuilder builder = new StringBuilder();
        ConfigSingleton config = ConfigSingleton.getInstance(context);

        for (int i = 0; i < movie.getGenreList().size(); i++) {
            for (Genre genre : config.getGenreList()) {
                if (genre.getId().intValue() == movie.getGenreList().get(i).intValue()) {
                    builder.append(genre.getName());
                    builder.append(SEPARATOR);
                }
            }
        }

        String result = builder.toString();
        if (result.length() > 0) {
            result = result.substring(0, result.length() - SEPARATOR.length());
        }

        return result;
    }

}
