package com.example.android.filmhall;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.filmhall.data.FavoritesList;
import com.example.android.filmhall.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String MOVIE_DETAILS = "Movie Details";

    private static final String ON_SAVE_SORT_STATE = "onSaveSortState";
    private static final String ON_SAVE_GRID_STATE = "onSaveGridState";


    private ArrayList<MovieDetails> movieDetails =  new ArrayList<>();
    private GridView gridView;
    private int sortBy = 1;

    private Parcelable gridState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.movie_gridview);

        Log.d(TAG, "savedInstanceState is " + (savedInstanceState != null ? "SAVED" : "WAS NOT SAVED"));


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ON_SAVE_SORT_STATE)) {
                gridState = savedInstanceState.getParcelable(ON_SAVE_GRID_STATE);
                movieDetails = savedInstanceState.getParcelableArrayList(ON_SAVE_SORT_STATE);
                Log.d(TAG, "Retrieved state of Sort State " + sortBy);
                Log.d(TAG, "gridState is " + (gridState != null ? "SAVED" : "WAS NOT SAVED"));
                gridView.onRestoreInstanceState(gridState);
                gridView.setAdapter(new TextAdapter(MainActivity.this));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Log.d(TAG, "Clicked on " + position + ". " + movieDetails.get(position).title);

                        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                        intent.putExtra(MOVIE_DETAILS, movieDetails.get(position));
                        startActivity(intent);
                    }
                });
                Log.d(TAG, movieDetails.toString());
            }
        } else {

            new MovieDbTask().execute(NetworkUtils.buildUrl(sortBy));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Log.d(TAG, "Clicked on " + position + ". " + movieDetails.get(position).title);

                    Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                    intent.putExtra(MOVIE_DETAILS, movieDetails.get(position));
                    startActivity(intent);
                }
            });
        }

    }

    public class MovieDbTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String movieDatabase = null;
            try {
                Log.d(TAG, "searchUrl is: " + searchUrl);
                movieDatabase = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, movieDatabase);
            return movieDatabase;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                parseString(s);
                gridView.setAdapter(new TextAdapter(MainActivity.this));
            }
        }

        public void parseString(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray movies = jsonObject.getJSONArray("results");
                for (int i=0; i < movies.length(); i++) {
                    JSONObject obj = movies.getJSONObject(i);

                    String title = obj.getString("original_title");
                    String poster = obj.getString("poster_path");
                    String overview = obj.getString("overview");
                    String release_date = obj.getString("release_date");
                    long vote_average = obj.getLong("vote_average");
                    long movie_id = obj.getLong("id");

                    Log.d(TAG, "Movie " + i + " " + title);

                    movieDetails.add(new MovieDetails(poster,overview,release_date,title,vote_average,movie_id));
                }
            } catch (JSONException exception) {
                Log.e(TAG, exception.toString());
            }
        }
    }

    public class TextAdapter extends BaseAdapter {

        private final Context mContext;

        public TextAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return movieDetails.size();
        }

        public Object getItem(int position) {
            return movieDetails.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }

            String posterPath = movieDetails.get(position).poster;
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w500" + posterPath).into(imageView);
            return imageView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popular:
                sortBy = 1;
                Log.d(TAG, "Sort method is Sort by Popular");
                movieDetails.clear();
                new MovieDbTask().execute(NetworkUtils.buildUrl(sortBy));
                item.setChecked(true);
                return true;

            case R.id.sort_by_top_rated:
                sortBy = 2;
                Log.d(TAG, "Sort method is Sort by Top Rated");
                movieDetails.clear();
                new MovieDbTask().execute(NetworkUtils.buildUrl(sortBy));
                item.setChecked(true);
                return true;

            case R.id.sort_by_favorites:
                sortBy = 3;
                Log.d(TAG, "Sort method is Sort by Favorites");
                movieDetails.clear();
                retrieveFavorites();
                gridView.setAdapter(new TextAdapter(MainActivity.this));

                item.setChecked(true);

                return true;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        gridState = gridView.onSaveInstanceState();
        savedInstanceState.putParcelable(ON_SAVE_GRID_STATE, gridState);
        savedInstanceState.putParcelableArrayList(ON_SAVE_SORT_STATE, movieDetails);
    }


    private void retrieveFavorites() {

        String[] projection = { "movieName", "moviesDbId", "moviePoster", "movieOverview", "movieVoteAverage", "movieReleaseDate" };

        Uri contentUri = Uri.parse("content://" + FavoritesList.AUTHORITY + "/" + FavoritesList.BASE_PATH);

        Cursor cursor = null;
        try {

            cursor = getContentResolver().query(contentUri, projection, null, null, null);

            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("movieName"));
                long movie_id = cursor.getLong(cursor.getColumnIndexOrThrow("moviesDbId"));
                String poster = cursor.getString(cursor.getColumnIndexOrThrow("moviePoster"));
                String overview = cursor.getString(cursor.getColumnIndexOrThrow("movieOverview"));
                long vote_average = cursor.getLong(cursor.getColumnIndexOrThrow("movieVoteAverage"));
                String release_date = cursor.getString(cursor.getColumnIndexOrThrow("movieReleaseDate"));

                movieDetails.add(new MovieDetails(poster, overview, release_date, title, vote_average, movie_id));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error found: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(TAG, "The query is as follows: " + movieDetails);
    }
}
