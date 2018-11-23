package com.example.android.newsapp;

/**
 * {@News} represents news about fires.
 */
public class News {

    public String getTitle() {
        return title;
    }

    /** Title of the news about fires. */
    public final String title;


    public String getNumOfPeople() {
        return date;
    }

    /** Number of people who were affected by the California fires. */
    public final String date;


    public String getAuthor() {
        return author;
    }

    public final String author;

    /**
     * Constructs a new {@link News}.
     *
     * @param eventTitle is the title of the fire event
     * @param eventDate is the number of people who were affected by the California fires
     * @param eventAuthor is the number of people who were affected by the California fires
     */
    public News(String eventTitle, String eventDate, String evenAuthor) {
        title = eventTitle;
        date = eventDate;
        author = eventAuthor;
    }
}
