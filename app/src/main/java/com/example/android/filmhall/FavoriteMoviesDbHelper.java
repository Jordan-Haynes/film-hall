package com.example.android.filmhall;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.filmhall.data.FavoritesList;

/**
 * Created by jordanhaynes on 3/4/18.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "FavoriteMoviesDbHelper";

    private static final String DATABASE_NAME = "likedmovies.db";

    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: SQLiteDatabase");
        final String SQL_CREATE_FAVEMOVIES_TABLE = "CREATE TABLE " + FavoritesList.FavoritesListEntry.TABLE_NAME +
                " (" + FavoritesList.FavoritesListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesList.FavoritesListEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                FavoritesList.FavoritesListEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                FavoritesList.FavoritesListEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL," +
                FavoritesList.FavoritesListEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL," +
                FavoritesList.FavoritesListEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                FavoritesList.FavoritesListEntry.COLUMN_MOVIE_VOTE_AVERAGE + " INTEGER NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVEMOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesList.FavoritesListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
