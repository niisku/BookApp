package com.example.android.bookapp;

/**
 * Created by Niina on 14.6.2017.
 */

public class BookDetails {

    public String mAuthor;
    public String mTitle;
    public String mLanguage;
    public String mSnippet;



    public BookDetails (String author, String title, String language, String snippet) {

        mAuthor = author;
        mTitle = title;
        mLanguage = language;
        mSnippet = snippet;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmLanguage() {
        return mLanguage;
    }

    public String getmSnippet() {
        return mSnippet;
    }

}
