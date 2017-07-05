package com.example.android.bookapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Niina on 20.6.2017.
 */

//This is a customised adapter to put the correct information to correct fields in the xml's list

public class BookAdapter extends ArrayAdapter<BookDetails> {

    //Here, we initialize the ArrayAdapter's internal storage for the context and the list:
    public BookAdapter(Activity context, ArrayList<BookDetails> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //ConvertView = Existing view that can be reused; but when 'null' = there's no existing
        //view yet -> inflate it with certain layout
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        BookDetails currentBook = getItem(position);

        //Here matching the xml textview + its correct values:
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.result_author_textview);
        authorTextView.setText(currentBook.getmAuthor());

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.result_title_textview);
        titleTextView.setText(currentBook.getmTitle());

        TextView languageTextView = (TextView) listItemView.findViewById(R.id.result_language_textview);
        languageTextView.setText(currentBook.getmLanguage());

        TextView snippetTextView = (TextView) listItemView.findViewById(R.id.result_snippet_textview);
        snippetTextView.setText(Html.fromHtml(currentBook.getmSnippet()));

        //Here returning the view with correct information on it:
        return listItemView;

    }
}
