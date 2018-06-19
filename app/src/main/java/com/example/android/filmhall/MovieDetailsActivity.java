package com.example.android.filmhall;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.filmhall.data.FavoritesList;
import com.example.android.filmhall.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {

    private final static String TAG = "Movie Details Activity";

    private String selectedMovieTitle;
    private long selectedMovieId;
    private String selectedMoviePoster;
    private String selectedMovieOverview;
    private long selectedMovieVoteAverage;
    private String selectedMovieDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);

        final MovieDetails movieDetails = getIntent().getParcelableExtra(MainActivity.MOVIE_DETAILS);
        Log.d(TAG, movieDetails.toString());

        ImageView posterWidget = findViewById(R.id.movie_poster);
        String posterPath = movieDetails.poster;
        Picasso.with(this).load("http://image.tmdb.org/t/p/w500" + posterPath).into(posterWidget);

        TextView titleWidget = findViewById(R.id.movie_original_title);
        titleWidget.setText(movieDetails.title);

        TextView dateWidget = findViewById(R.id.movie_release_date);
        dateWidget.setText(movieDetails.release_date);

        TextView ratingWidget = findViewById(R.id.movie_rating);
        ratingWidget.setText(getString(R.string.voter_rating, movieDetails.vote_average));

        TextView synopsisWidget = findViewById(R.id.movie_overview);
        synopsisWidget.setText(movieDetails.overview);

        final String movieTrailerDbUrl = getString(R.string.movie_trailer, movieDetails.movie_id, MovieDbApiKey.key);

        selectedMovieTitle = movieDetails.title;
        selectedMovieId = movieDetails.movie_id;
        selectedMoviePoster = movieDetails.poster;
        selectedMovieOverview = movieDetails.overview;
        selectedMovieVoteAverage = movieDetails.vote_average;
        selectedMovieDate = movieDetails.release_date;

        // Button action to open movie trailer
        Button button1 = findViewById(R.id.button_trailer);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Clicked trailer button!");
                new MovieTrailerLink().execute(NetworkUtils.movieTrailerUrl(movieTrailerDbUrl));
            }
        });

        // Button action to open movie trailer
        Button button2 = findViewById(R.id.button_reviews);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Clicked reviews button!");
                String idSeekReviews = getString(R.string.movie_reviews, movieDetails.movie_id);
                openMovieReviews(idSeekReviews);
            }
        });

        // Checkbox selection to favorite current movie details
        CheckBox checkBox = findViewById(R.id.favorites_checkbox);

        String[] projection = { "movieName" };
        String selection = FavoritesList.FavoritesListEntry.COLUMN_MOVIE_NAME + " LIKE ?";
        String[] arguments = {selectedMovieTitle};

        Uri contentUri = Uri.parse("content://" + FavoritesList.AUTHORITY + "/" + FavoritesList.BASE_PATH + "/" + selectedMovieId);

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentUri, projection, selection, arguments, null);
            if (cursor != null) {
                Log.d(TAG, "Queried Favorites for Movie: " + selectedMovieId + " cursor is " + cursor.getCount());
                checkBox.setChecked(cursor.getCount() > 0);
            } else {
                checkBox.setChecked(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error found: " + e);
        } finally {
            cursor.close();
        }
    }

    // Method to open web page for movie trailer when passed the URL as a string
    private void openMovieTrailer(StringBuffer filmKey) {
        Uri movieTrailer = Uri.parse("https://www.youtube.com/watch?v=" + filmKey);
        Log.d(TAG, "About to pass " + movieTrailer.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, movieTrailer);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // Method to open activity that displays the movie's reviews
    private void openMovieReviews(String s) {
        Uri movieReviews = Uri.parse(s);
        Log.d(TAG, "About to pass " + movieReviews.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, movieReviews);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.favorites_checkbox:
                if (checked) {
                    Log.d(TAG, "Checked " + selectedMovieTitle + " to be added to Favorites.");

                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();

                    values.put(FavoritesList.FavoritesListEntry.COLUMN_MOVIE_NAME, selectedMovieTitle);
                    values.put(FavoritesList.FavoritesListEntry.COLUMN_MOVIE_ID, selectedMovieId);
                    values.put(FavoritesList.FavoritesListEntry.COLUMN_MOVIE_POSTER, selectedMoviePoster);
                    values.put(FavoritesList.FavoritesListEntry.COLUMN_MOVIE_OVERVIEW, selectedMovieOverview);
                    values.put(FavoritesList.FavoritesListEntry.COLUMN_MOVIE_VOTE_AVERAGE, selectedMovieVoteAverage);
                    values.put(FavoritesList.FavoritesListEntry.COLUMN_MOVIE_RELEASE_DATE, selectedMovieDate);

                    // Insert a new row with values
                    Uri contentUri = Uri.parse("content://" + FavoritesList.AUTHORITY + "/" + FavoritesList.BASE_PATH);

                    Uri returnedUri = getContentResolver().insert(contentUri, values);
                    Log.d(TAG, "Finished insert for " + returnedUri);
                } else {
                    String selection = FavoritesList.FavoritesListEntry.COLUMN_MOVIE_NAME + " LIKE ?";
                    String[] arguments = {selectedMovieTitle};

                    // Delete row via ContentProvider
                    Uri contentUri = Uri.parse("content://" + FavoritesList.AUTHORITY + "/"
                            + FavoritesList.BASE_PATH + "/" + selectedMovieId);

                    int deletedRow = getContentResolver().delete(contentUri, selection, arguments);

                    Log.d(TAG, "Deleted " + deletedRow + " rows from database.");
                }
                break;
        }
    }

    public class MovieTrailerLink extends AsyncTask<URL, Void, String> {

        public StringBuffer[] filmKey;

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String movieTrailerLink = null;
            try {
                movieTrailerLink = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieTrailerLink;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                if (parseString(s)) {
                    openMovieTrailer(filmKey[0]);
                }
            }
        }


        public boolean parseString(String s) {
            boolean foundKey = false;
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray movies = jsonObject.getJSONArray("results");
                filmKey = new StringBuffer[movies.length()];
                for (int i=0; i < movies.length(); i++) {
                    JSONObject obj = movies.getJSONObject(i);

                    String firstKey = obj.getString("key");
                    Log.d(TAG, firstKey);
                    filmKey[i] = new StringBuffer(firstKey);

                    Log.d(TAG, "MovieUrl id " + obj.getString("key"));
                    Log.d(TAG, "MovieUrl id " + obj.getString("name"));

                    foundKey = true;
                }
            } catch (JSONException exception) {
                Log.e(TAG, exception.toString());
            }

            return foundKey;
        }
    }
}
