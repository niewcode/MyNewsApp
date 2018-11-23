package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;

import javax.xml.soap.Text;

import static com.example.android.newsapp.R.string.alert_not_available;

/**
 * Displays information about a fire.
 */
public class MainActivity extends AppCompatActivity
    implements LoaderCallbacks<List<News>>

        {
            /**
             * List empty_state TextView to display if there are no events to display
             */
            private TextView mEmptyStateTextView;


            /** Tag the String for the log messages */
    public static final String LOG_TAG = MainActivity.class.getName();

            /**
             * URL Constant for data retrieval from The Guardian dataset
             */

    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?";

            /**
             * Constant value for NewsLoader.java. The id Can be any integer that's used for >1 loader
             * logs will bear loader number 42 as practice for logging multiple loaders
             */
            private static final int NEWS_LOADER_ID = 42;

            /**
             * Adapter for list
             */

            private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find reference to {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        //Find and display TextView for empty list space if no events found
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        //Create new {@Link ArrayAdapter} of events taking as input, an empty list of events
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        //Set adapter on ListView to populate list to user interface
        newsListView.setAdapter(mAdapter);

        //Set onItemClickListener on ListView, which sends intent to browser
        //to view more details about the selected event from the JSON URL provided in response.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find most currently selected event
                News currentNews = mAdapter.getItem(position);
                //Convert string URL into URI Object (to pass Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getSegmentUrl());
                //Create new intent to view the News URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                //Send intent to launch new browser activity
                startActivity(websiteIntent);
            }
        });

        //Prepare NewsLoader, either via reconnecting to an existing one or start a new one.
        getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        Log.i(LOG_TAG, "TEST: Loader42 INITIALIZED");

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        // Log message for debugging
        Log.i(LOG_TAG, "TEST: Loader42 onCreateLoader() method CALLED");

        // Create a new Loader for the URL
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // take the user's preference stored in receive variable to use as "receive" parameter
        String receive = sharedPreferences.getString(
                getString(R.string.settings_pages_key),
                getString(R.string.settings_receive_default));

        // get user preferences from PAGE_SIZE constant variable to use as "page-size" parameter
        String minNews = sharedPreferences.getString(getString(R.string.settings_pages_key),
                getString(R.string.settings_receive_default));

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append Query param & it's respective value.
        uriBuilder.appendQueryParameter("q", receive);
        uriBuilder.appendQueryParameter("show-fields", "all");
        uriBuilder.appendQueryParameter("page-size", minNews);
        uriBuilder.appendQueryParameter("api-key", "" ); //TODO:<<<<<<ADD TESTERS API-KEY inside the empty quotes

        // Create a new loader and return completed built URI:
        return new NewsLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        // Purge adapter of News data from before
        mAdapter.clear();

        // Hide loading indicator as data has been loaded
        View progressBar = findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.GONE);

        // Check for Internet Connection
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Purge adapter of News data from before
        mAdapter.clear();

        if (networkInfo == null) {

            // state No Internet Connection
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        } else if (networkInfo != null && networkInfo.isConnected()) {

            // There is interenet, List empty. state - No Events Found
            mEmptyStateTextView.setText(R.string.no_news);

        }

        // If a valid list of {@link News} exists, add them to adapter's dataset,
        // it will trigger the ListView to update
        if (news != null && !news.isEmpty()) {

        // Add News elements to List
        mAdapter.addAll(news);
        Log.i(LOG_TAG, "Test: Loader42 onLoadFinished() method CALLED");

        }

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader RESET to purge data
        mAdapter.clear();
        Log.i(LOG_TAG, "TEST: Loader42 onLoaderReset() method CALLED");
    }

    @Override
    // This method initializes the contents of the options menu of the Activity
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflater for the options menu in the main.xml layout
        getMenuInflater().inflate(R.menu.main, menu);

        // Return boolean "true"
        return true;

    }

    @Override
    // pass the selected Menuitem
    public boolean onOptionsItemSelected(MenuItem item) {

        // returns unique id for the menu item defined by @id/ in the menu resource
        // determines which item was selected and what action to take
        int id = item.getItemId();

        // menu has one item, @id/action_settings,
        // match id against known menu items to perform appropriate action
        if (id == R.id.action_settings) {

            // open SettingsActivity via an intent.
            Intent settingsIntent = new Intent(this, com.example.android.newsapp.SettingsActivity.class);
            startActivity(settingsIntent);

            // return boolean "true"
            return true;

        }

        // Return the selected item
        return super.onOptionsItemSelected(item);

    }

    }




