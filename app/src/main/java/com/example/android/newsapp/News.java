package com.example.android.newsapp;

/**
 * {@News} represents news about fires.
 */
public class News {

    private String title, section, url, date, author;

    public News (String title, String section, String url, String date,
                     String author){
        this.title = title;
        this.section = section;
        this.url = url;
        this.date = date;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

}

