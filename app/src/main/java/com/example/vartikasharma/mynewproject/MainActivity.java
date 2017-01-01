package com.example.vartikasharma.mynewproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private OkHttpClient client;
    private String url;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        gson = new GsonBuilder().create();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.openweathermap.org/data/2.5/forecast/city?id=524901").newBuilder();
        urlBuilder.addQueryParameter("APPID", "ed34795f35c87eb45c31e75d6b56ea43");

        url = urlBuilder.build().toString();
        Log.i(LOG_TAG, "the url, " + url);

        loadContent();
    }
    private void loadContent() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String response = ApiCall.GET(client, url);
                    //Parse the response string here
                    Log.d("Response", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}

