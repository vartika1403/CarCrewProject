package com.example.vartikasharma.mynewproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private OkHttpClient client;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather?").newBuilder();
        urlBuilder.addQueryParameter("q", "London");
        url = urlBuilder.build().toString();

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

