package com.example.android.bookapp;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Niina on 20.6.2017.
 */


//Loader file to fetch the data only once (vs. without this, requests start from scratch
//always when e.g. phone is rotated)

public class BookLoader extends AsyncTaskLoader<List<BookDetails>> {

    //Tag for log messages:
    private static final String LOG_TAG = BookLoader.class.getName();
    //Query url:
    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "Here 'onStartLoading' is called..");
        forceLoad();
    }

    @Override
    //This runs in the background thread; Making the network request, parsing the response, and
    //extract & return the list of books:
    public List<BookDetails> loadInBackground() {

        Log.i(LOG_TAG, "Here 'loadInBackground' is called..");
        //Checking that the url has value, if not then exit:
        if (mUrl == null) {
            return null;
        }

        List<BookDetails> books = QueryUtils.fetchTheData(mUrl);
        return books;


    }
}
