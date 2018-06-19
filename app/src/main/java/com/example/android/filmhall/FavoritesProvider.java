package com.example.android.filmhall;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.filmhall.data.FavoritesList;

import static com.example.android.filmhall.data.FavoritesList.FavoritesListEntry.TABLE_NAME;

/**
 * Created by jordanhaynes on 3/10/18.
 */

public class FavoritesProvider extends ContentProvider {

    private static final int FAVORITES_TABLE = 1;
    private static final int FAVORITES_TABLE_ITEM = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(FavoritesList.AUTHORITY, FavoritesList.BASE_PATH, FAVORITES_TABLE);
        uriMatcher.addURI(FavoritesList.AUTHORITY, FavoritesList.BASE_PATH + "/#", FAVORITES_TABLE_ITEM);
    }

    private final static String TAG = "FavoritesProvider";

    private static FavoriteMoviesDbHelper dbHelper = null;

    public boolean onCreate() {
        if (dbHelper == null) {
            dbHelper = new FavoriteMoviesDbHelper(getContext());
        }

        Log.d(TAG, "Creating FilmHall database content provider");
        return true;
    }

    public Uri insert(Uri uri, ContentValues values) {

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case FAVORITES_TABLE:
                Log.d(TAG, "Inserting into favorites table.");
                break;

            case FAVORITES_TABLE_ITEM:
                throw new IllegalArgumentException("Unknown URI: " + uri);

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.insert(TABLE_NAME, null, values);

        return uri;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case FAVORITES_TABLE:
                Log.d(TAG, "Querying favorites table.");
                break;

            case FAVORITES_TABLE_ITEM:
                Log.d(TAG, "Querying row of favorites table.");
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }


        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "favoriteMovies",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }

    // Return the number of rows updated
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case FAVORITES_TABLE:
                throw new IllegalArgumentException("Unknown URI: " + uri);

            case FAVORITES_TABLE_ITEM:
                Log.d(TAG, "Querying favorites table.");
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int deletedRows = db.delete(TABLE_NAME, selection, selectionArgs);
        Log.d(TAG, "Deleted " + deletedRows + " rows via ContentProvider");
        return deletedRows;
    }

    public String getType(Uri uri) {
        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case FAVORITES_TABLE:
                return FavoritesList.CONTENT_TYPE;

            case FAVORITES_TABLE_ITEM:
                return FavoritesList.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

}
