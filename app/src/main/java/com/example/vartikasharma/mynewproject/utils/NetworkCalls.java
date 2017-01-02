package com.example.vartikasharma.mynewproject.utils;

import android.util.Log;

import com.example.vartikasharma.mynewproject.MainWeatherClass;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


public class NetworkCalls {
    private OkHttpClient client;
    private String url;
    private Gson gson;

    public void getCurrentWhether(String cityName){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.openweathermap.org/data/2.5/weather?q="+cityName).newBuilder();
        urlBuilder.addQueryParameter("APPID", "ed34795f35c87eb45c31e75d6b56ea43");
        url = urlBuilder.build().toString();

        // loadContent();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                Log.d("Response handler", response.body().string());
                String responseData = response.body().string();
                MainWeatherClass mainWeatherClass = gson.fromJson(responseData, MainWeatherClass.class);
            }
        });

    }
}
