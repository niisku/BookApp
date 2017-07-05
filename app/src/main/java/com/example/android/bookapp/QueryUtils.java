package com.example.android.bookapp;

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
 * Created by Niina on 16.6.2017.
 */

//This class is to keep all the query related methods in a one, same place, vs. putting all this to Main

public class QueryUtils {

    private static String LOG_TAG = QueryUtils.class.getSimpleName();

    //This is here just to make sure no-one else builds QueryUtils object:
    private QueryUtils() {

    }

    private static String authorValue;
    private static String titleValue;
    private static String languageValue;
    private static String snippetValue;

    private static List<BookDetails> extractFeaturesFromJson(String bookJson) {

        //Checking whether the string going to be extracted is null
        if (TextUtils.isEmpty(bookJson)) {
            return null;
        }

        //Create a new empty ArrayList where the extracted information is being put:
        List<BookDetails> jsonBookList = new ArrayList<>();

        //Try/Catch of JSON response:
        //First 'trying' to parse the needed elements, but if error occurs, it's catched + put to log

        try {

            //Naming a jsonObject
            JSONObject jsonBookObject = new JSONObject(bookJson);

            //Checking that the object has the 'items' branch:
            if (jsonBookObject.has("items")) {
                //Naming the going-to-be-opened ArrayList branch of 'items'
                JSONArray itemsArray = jsonBookObject.getJSONArray("items");

                //'Looping' as long as there are numbers (items) left in the list
                for (int i = 0; 1 < itemsArray.length(); i++) {

                    //Finding + naming the current book in question:
                    JSONObject currentBookObject = itemsArray.getJSONObject(i);

                    //..And to the next branch, 'volumeInfo';
                    JSONObject volumeInfo = currentBookObject.getJSONObject("volumeInfo");

                    //Looked Title can be found here:
                    titleValue = volumeInfo.getString("title");


                    if (volumeInfo.has("authors")) {
                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        authorValue = authors.getString(0);
                    } else {
                        authorValue = "Author information unavailable";
                    }


                    if (currentBookObject.has("searchInfo")) {
                        JSONObject searchInfo = currentBookObject.getJSONObject("searchInfo");
                        if (searchInfo.has("textSnippet")) {
                            snippetValue = searchInfo.getString("textSnippet");
                        }
                    } else {
                        snippetValue = "Text snippet unavailable";
                    }


                    //To get the language value +  if the Language doesn't exist set placeholder text:
                    if (volumeInfo.has("language")) {
                        languageValue = volumeInfo.getString("language");
                    } else {
                        languageValue = "N/A";
                    }

                    //Create a new Book object from those collected values:
                    BookDetails book = new BookDetails(authorValue, titleValue, languageValue, snippetValue);

                    //Aaaand adding the created book to the created list:
                    jsonBookList.add(book);

                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem with parsing the JSON data", e);
        }

        //And finally returning the new list:
        return jsonBookList;
    }

    //This makes a new URL object called 'url' (btw its value will be the one in 'stringURL'):

    private static URL createUrl(String stringURL) {

        //First it will be 'null':
        URL url = null;
        //Then try/catch to create the new url:
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error when creating the new url");
        }
        return url;
    }

    //When this code is called, it makes the HTTP request to the given url.
    //The return value is String
    private static String makeHTTPRequest(URL url) throws IOException {

        //In the beginning jsonResponse is an empty string:
        String jsonResponse = " ";

        //Checks whether the url is empty:
        if (url == null) {
            return jsonResponse;
        }

        //Now to the actual connection request:
        //Used variables are null in the beginning:
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        //Now trying to make the connection (With try - catch - finally):
        try {
            //given url + openConnecion = new value of the 'urlConnection':
            urlConnection = (HttpURLConnection) url.openConnection();
            //Making the following actions to the 'urlConnection:
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            //If things are ok, start reading the input stream (= stream of bytes)
            if (urlConnection.getResponseCode() == 200) {
                //'inputStream' = given url + connection + its input stream bytes:
                inputStream = urlConnection.getInputStream();
                //'jsonResponse' = given url + connection + its input stream bytes turned intoString:
                //(bytes -> string in 'readFromStream' method below)
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error in reading the stream, response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with retrieving the book results " + e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        //'jsonResponse' = the url turned into string:
        return jsonResponse;
    }

    //Here created an InputStreamReader, that reads the 'inputStream' stream bytes + turns them into string.
    //It has also BufferedStreamReader to help:

    private static String readFromStream(InputStream inputStream) throws IOException {
        //First we create a StringBuilder = String that can be modified
        StringBuilder stringBuilderOutput = new StringBuilder();

        //If 'inputStream' has something:
        if (inputStream != null) {
            //Turns the data into chars:
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //Making the reading of chars faster:
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //'line' = reading the chars + with bufferedReader
            String line = bufferedReader.readLine();

            //As long as the buffredReader's read line has something:
            while (line != null) {
                //The value of 'line' is being updated + read again:
                stringBuilderOutput.append(line);
                line = bufferedReader.readLine();
            }

            //...And when the 'line' is null, the output is put to String form
        }
        return stringBuilderOutput.toString();
    }

//The 'final' method, that combines everything above to get the data:

    public static final List<BookDetails> fetchTheData(String requestUrl) {

        URL url = createUrl(requestUrl);

        //Make the network connection + get JSON response
        //First, 'jsonResponse' is null
        String jsonResponse = null;
        try {
            //jsonResponse's value is the network request to the created url:
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with HTTP request. " + e);
        }

        //Here: List 'books' 's value is the network request + the url + extract from that:
        List<BookDetails> books = extractFeaturesFromJson(jsonResponse);

        //Returning the above:
        return books;
    }

}


