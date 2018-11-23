package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;


public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(@NonNull Context context, @NonNull ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //store convertView as variable of View type named listEventView
        View listView = convertView;

        //check if existing view is in use else inflate view
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        //Get event data at this position in index
        News currentNews = getItem(position);

        //Get the date for formatting"
        String formattedDate = formatDate(currentNews.getDate());

        //Bind TextView with ID of "date"
        TextView dateView = listView.findViewById(R.id.date);
        //Get view from adapter and set view with date
        dateView.setText(formattedDate);

        //Bind to TextView with ID of "segment_title"
        TextView titleView = listView.findViewById(R.id.title);
        //Get view from adapter and set view with segment_title
        titleView.setText(currentNews.getTitle());

        //Bind to TextView with ID of "section_name"
        TextView sectionView = listView.findViewById(R.id.section);
        //Get view from adapter and set view with section_name
        sectionView.setText(currentNews.getSection());

        //Bind to TextView with ID of "author"
        TextView authorView = listView.findViewById(R.id.author);
        //Get view from adapter and set view with author
        authorView.setText(currentNews.getAuthor());

        //Return View
        return listView;
    }

    //Return formatted date string i.e "May 1, 1978" from a {@link chronObject}
    public String formatDate(String date) {

        //Create SimpleDateFormat object with pattern in response
        final SimpleDateFormat dateFormatter=
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                        Locale.getDefault());
        //Set date_out to null, attempt to parse date or catch ParseException
        Date date_out = null;
        try {
            date_out = dateFormatter.parse(date);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        //Format date into abbreviated date pattern
        final SimpleDateFormat outputFormatter=
                new SimpleDateFormat("MMM dd ''yy", Locale.US);
        //Return Formatted Date
        return outputFormatter.format(date_out);
    }
}
