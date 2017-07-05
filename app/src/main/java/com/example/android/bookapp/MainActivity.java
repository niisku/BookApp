package com.example.android.bookapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<BookDetails>> {

    String restOfUrl = null;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static String BOOK_URL_BEGINNING = "https://www.googleapis.com/books/v1/volumes?q=";

    public String urlBeginning = "https://www.googleapis.com/books/v1/volumes?q=";

    //Constant value for the book loader, because we have only 1 loader (+ therefore 1 ID):
    public static final int LOADER_ID = 1;

    //In the loader's 'onPostExecute' method, we're updating the ListView. This is done via the
    //the BookAdapter. To make it accessible, it's made global here:
    private BookAdapter mAdapter;

    private TextView mEmptyTextView;

    private SearchView mSearchArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "Here the Main's onCreate() method called");
        super.onCreate(savedInstanceState);

        //Setting the view to be 'activity_main' xml:
        setContentView(R.layout.activity_main);

        //Setting the list.xml to be 'bookListView':
        final ListView bookListView = (ListView) findViewById(R.id.list);

        //Setting the option of empty textview to be in the list view:
        mEmptyTextView = (TextView) findViewById(R.id.empty_textview);
        bookListView.setEmptyView(mEmptyTextView);

        //Setting the adapter with empty book list to the listview:
        mAdapter = new BookAdapter(this, new ArrayList<BookDetails>());
        bookListView.setAdapter(mAdapter);

        mSearchArea = (SearchView) findViewById(R.id.type_search_textview);


        final View loadingIndicator = findViewById(R.id.loading_spinner);


        //Checking the state of the internet connection with ConnectivityManager:
        //1: Make a new Conn.Manager:
        final ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //2: Get information about current network:
        final NetworkInfo networkInfo = (NetworkInfo) connManager.getActiveNetworkInfo();
        //3: If there is network, fetch the data:
        if (networkInfo != null && networkInfo.isConnected()) {
            //4: Create LoaderManager (to interact with loaders):
            LoaderManager loaderManager = getLoaderManager();
            //5: Initialize the loader; Pass ID, null for Bundle, and this activity:
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            //6: Otherwise hide the loading indicator & then update the empty page with no internet-text:

            loadingIndicator.setVisibility(View.GONE);

            mEmptyTextView.setText(R.string.no_internet);
        }

        mSearchArea.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = (NetworkInfo) connManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    bookListView.setVisibility(View.INVISIBLE);
                    mEmptyTextView.setVisibility(View.GONE);

                    restOfUrl = mSearchArea.getQuery().toString();
                    restOfUrl = restOfUrl.replace(" ", "+");
                    Log.v(LOG_TAG, "Here created the query word; " + restOfUrl);
                    getLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                    mSearchArea.clearFocus();
                } else {
                    bookListView.setVisibility(View.INVISIBLE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mEmptyTextView.setText(R.string.no_internet);
                    loadingIndicator.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }


    //Here: LoaderCallbacks() interface + its 3 methods;
    // 1. onCreateLoader() = Create & return a new loader for the given ID
    // (= fetches the book data from web server)
    // 2. onLoadFinished() = Called when previously created loader has finished its loading
    // (= loader has finished downloading data on the background thread, so here updating
    // the UI with the list of books)
    // 3. onLoadReset() = Called when prev. created loader is being reset, making its data unavailable
    // (= E.g. If decided to download data from new URL (bc of new search term), current list
    // should be cleared)


    //So here: for when the LoaderManager has determined that the loader with our
    // specified ID isn't running, so we should create a new one.
    @Override
    public Loader<List<BookDetails>> onCreateLoader(int id, Bundle bundle) {

        Log.i(LOG_TAG, "Here the 'onCreateLoader()' is being called");
        if (restOfUrl == null) {
            return new BookLoader(this, BOOK_URL_BEGINNING);
        }

        String Parts[] = restOfUrl.split(" ");
        for (int j = 0; j < Parts.length; j++) {

            urlBeginning = urlBeginning + Parts[j];
        }

        Log.v(LOG_TAG, "Here is the created url; " + urlBeginning);
        return new BookLoader(this, urlBeginning);
    }


    //And here: Updating the dataset in the adapter to update the UI.
    @Override
    public void onLoadFinished(Loader<List<BookDetails>> loader, List<BookDetails> data) {

        urlBeginning = "https://www.googleapis.com/books/v1/volumes?q=";
        ProgressBar hideSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        hideSpinner.setVisibility(View.GONE);
        //Informing that there are no results.
        mEmptyTextView.setText(R.string.no_results);

        //Clearing the adapter.
        mAdapter.clear();


        // If there is a valid list of books, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    //And here: Clearing the adapter when its resetted.
    @Override
    public void onLoaderReset(Loader<List<BookDetails>> loader) {
        Log.i(LOG_TAG, "Here the 'onLoaderReset()' is called.");
        mAdapter.clear();
        urlBeginning = "https://www.googleapis.com/books/v1/volumes?q=";


    }
}
