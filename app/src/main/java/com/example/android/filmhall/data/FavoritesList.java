package com.example.android.filmhall.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jordanhaynes on 2/27/18.
 */

public final class FavoritesList {

    public static final String AUTHORITY = "com.example.android.filmhall.data.provider";

    public static final String BASE_PATH = "favoriteMovies";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.example.android.filmhall.data.provider.table";

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.example.android.filmhall.data.provider.table_item";

    public static class FavoritesListEntry implements BaseColumns {
        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_ID = "moviesDbId";
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";
        public static final String COLUMN_MOVIE_OVERVIEW = "movieOverview";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movieVoteAverage";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
    }
}
