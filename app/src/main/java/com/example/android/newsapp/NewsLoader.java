package com.example.android.newsapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    //Tag for Log Messages
    private static final String LOG_TAG = NewsLoader.class.getName();

    //QueryUrl
    private String mUrl;

    /** Constructs new {@link NewsLoader}
     *@param context of the activity
     *
     * @param url to load data from
     */

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.i(LOG_TAG,"NewsLoader: onStart method CALLED");
    }

    @Override
    public List<News> loadInBackground(){
        if (mUrl == null){
            Log.i(LOG_TAG, "NewsLoader: loadInBackground method STARTED");
            return null;

            //Perform network Request, parse response, and extract list of News where word is your events word
            List<News>news = NewsUtils.fetchNewsData(mUrl);
            return news;
        }
    }
}
