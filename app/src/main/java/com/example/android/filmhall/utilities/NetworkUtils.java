package com.example.android.filmhall.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.filmhall.MovieDbApiKey;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by jordanhaynes on 2/5/18.
 */

public class NetworkUtils {

    private final static String TAG = "NetworkUtils";

    public static URL buildUrl(int sortBy) {
        Uri builtUri;

        switch (sortBy) {
            case 1:
                Log.d(TAG, "Build URI for POPULAR");
                String popularQueryUrl = "http://api.themoviedb.org/3/movie/popular?api_key=" + MovieDbApiKey.key;
                builtUri = Uri.parse(popularQueryUrl).buildUpon().build();
                return setUrl(builtUri);

            case 2:
                Log.d(TAG, "Build URI for TOP_RATED");
                String topRatedQueryUrl = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + MovieDbApiKey.key;
                builtUri = Uri.parse(topRatedQueryUrl).buildUpon().build();
                return setUrl(builtUri);

            default:
                return null;
        }
    }

    public static URL movieTrailerUrl(String movieTrailerUrl) {
        Uri builtUri;
        Log.d(TAG, "Playing trailer for " + movieTrailerUrl);
        builtUri = Uri.parse(movieTrailerUrl).buildUpon().build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static URL setUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

}
