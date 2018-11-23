package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class with methods to help perform the HTTP request and
 * parse the response.
 */
public final class NewsUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = NewsUtils.class.getSimpleName();

    /**
     * Create a private constructor because nobody should ever create a {@link NewsUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils isn't needed)
     */

    private NewsUtils(){
    }

    /** Query Guardian dataset and return list of {@link News} Objects*/
    public static List<News> fetchNewsEventData(String requestUrl) {

        //Sleep thread for 3/4's of a second to show progress indicator
        try{
            Thread.sleep(750);
        }catch(InterruptedException ie) {
            Log.e(LOG_TAG,"QueryUtils fetchNewsData: INTERRUPTED...",ie);
            ie.printStackTrace();
        }

        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive JSON response back
        String jsonResponse=null;
        try{
            jsonResponse = makeHttpRequest(url);
            Log.i(LOG_TAG,"QueryUtils fetchNewsData makeHttpRequest CALLED");
        }catch(IOException ioe) {
            Log.e(LOG_TAG,"QueryUtils fetchNewsEventData: Problem making http request", ioe);
        }

        //Extract relevant fields from JSON response and create list of {@link NewsEvent}s
        List<News> news = extractFeatureFromJson(jsonResponse);
        //Return list of News
        return news;
    }

    /** Returns new URL object from given String URL */
    private static URL createUrl(String stringUrl){
        URL url=null;
        try{
            url=new URL(stringUrl);
        }catch (MalformedURLException mue){
            Log.e(LOG_TAG,"QueryUtils createUrl: Problem building URL",mue);
        }
        return url;
    }

    /** Makes an HTTP Request to given URL and returns jsonResponse string */
    private static String makeHttpRequest(URL url)throws IOException{
        String jsonResponse="";

        //If jsonResponse is null return early
        if (url==null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        try{
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds*/);
            urlConnection.setConnectTimeout(15000 /* milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If connection is successful (response code 200) Read input stream and parse response
            if (urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                inputStream=urlConnection.getInputStream();
                jsonResponse=readFromStream(inputStream);
                Log.i(LOG_TAG,
                        "QueryUtils makeHttpRequest:" +
                                " SUCCESSFULLY CONNECTED JSON RESPONSE RETRIEVED from:"+ url);
            }else{
                Log.e(LOG_TAG,"QueryUtils makeHttpRequest:" +
                        " ERROR!!! RESPONSE CODE:" + urlConnection.getResponseCode());
            }
        }catch(IOException e){
            Log.e(LOG_TAG,"QueryUtils makeHttpRequest:" +
                    " PROBLEM RETRIEVING JSON RESULTS.",e);
        }finally{
            if (urlConnection!=null){
                urlConnection.disconnect();
            }
            if (inputStream!=null){
                //Closing inputStream could throw IOException, so makeHttpRequest(URL url) specifies
                //an IOException could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**Converts the {@link InputStream} into a String, named output, that contains entire JSON
     * response from the server.*/
    private static String readFromStream (InputStream inputStream) throws IOException {
        StringBuilder output=new StringBuilder();
        if (inputStream!=null){
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader=new BufferedReader(inputStreamReader);
            String line=reader.readLine();
            while(line!=null){
                output.append(line);
                line=reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News) objects that has been built up from parsing
     * features from JSON response
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {

        //If JSON string is empty or null, return early
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        //Create an empty ArrayList that event parsed list segments can be added to
        List<News> news = new ArrayList<>();

        //Try to parse the response string. If there's a problem with the way JSON
        //is formatted, a JSONException exception object will be thrown.
        //Catch exception so app doesn't crash, and print the error message to the logs.
        try {
            //build a JSON object of News features with the corresponding data
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            //extract JSONObject with key named "response" (a news event list of results)
            JSONObject responseResult = baseJsonResponse.getJSONObject("response");

            //For a given event, extract JSONObject with key named "results"
            //which represents a list of article data
            JSONArray currentArticles = responseResult.getJSONArray("results");

            //For each News in newsArray, create an {@link News} object.
            //while index i is less than array length, increment i..
            for (int i = 0; i < currentArticles.length(); i++) {
                //extract single newsEvent @ index position (i) from events list
                JSONObject currentNews = currentArticles.getJSONObject(i);

                //extract string for key named "webTitle"
                String webTitle = currentNews.getString("webTitle");
                //extract string for key named "sectionName"
                String webSection = currentNews.getString("webSection");
                //extract string for key named "webUrl"
                String webUrl = currentNews.getString("webUrl");
                //extract string for key named "webPublicationDate"
                String webDate = currentNews.getString
                        ("webDate");
                //Get JSON object with key named "fields"
                JSONObject jsonObjectFields = currentNews.getJSONObject("fields");

                //extract (contributor) string for key named "byline"
                String webAuthor = jsonObjectFields.optString("webAuthor");

                //Create new {@link News} object with webSection, webTitle,
                //webUrl, webPublicationDate, and author (author) from
                //JSON response
                News news = new News(
                        webTitle, webSection, webUrl, webDate, webAuthor);
                //Add newly created {@link news} to list of events
                news.add(news);
            }

            //If an error is thrown when executing any of the above statements in the "try"
            // block catch exception here, so the app doesn't crash. Print a log message
            //with the message from the exception
        } catch(JSONException je){
            Log.e("QU extractFeatFromJson",
                    "Problem parsing JSON News results", je);
        }

        //Return list of News
        return news;
    }
}
