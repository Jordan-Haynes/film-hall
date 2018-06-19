package com.example.android.filmhall;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jordanhaynes on 2/8/18.
 */

public class MovieDetails implements Parcelable {

    public MovieDetails(String poster, String overview, String release_date, String title, long vote_average, long movie_id) {
        this.poster = poster;
        this.overview = overview;
        this.release_date = release_date;
        this.title = title;
        this.vote_average = vote_average;
        this.movie_id = movie_id;
    }

    public String toString() {
        return title + " " + release_date + " " + poster + " " + vote_average + " " + movie_id;
    }

    // Using default package access
    final String poster;
    final String overview;
    final String release_date;
    final String title;
    final long vote_average;
    final long movie_id;

    private MovieDetails(Parcel in) {
        poster = in.readString();
        overview = in.readString();
        release_date = in.readString();
        title = in.readString();
        vote_average = in.readLong();
        movie_id = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(title);
        dest.writeLong(vote_average);
        dest.writeLong(movie_id);
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>() {
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    public int describeContents() {
        return 0;
    }
}
