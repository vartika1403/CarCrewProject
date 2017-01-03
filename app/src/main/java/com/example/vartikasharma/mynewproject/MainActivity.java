package com.example.vartikasharma.mynewproject;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private OkHttpClient client;
    private String url;
    private Gson gson;
    private Double temp;
    private FragmentEnterCityName fragmentEnterCityName;
    private ProgressDialog progress;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        client = new OkHttpClient();
        gson = new GsonBuilder().create();

        fragmentEnterCityName = new FragmentEnterCityName();
        openFragmentEnterCityName();
    }

    private void openFragmentEnterCityName() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentEnterCityName);
        fragmentTransaction.commit();
    }

    public void openFragmentWeatherDisplay(String cityName) {
        getCurrentWhether(cityName);
    }

    public void getCurrentWhether(final String cityName){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.openweathermap.org/data/2.5/weather?q="+cityName).newBuilder();
        urlBuilder.addQueryParameter("APPID", "ed34795f35c87eb45c31e75d6b56ea43");
        url = urlBuilder.build().toString();

        progress=new ProgressDialog(this);
        progress.setMessage("Getting whether data");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setProgress(0);
//        progress.show();

        // loadContent();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
//                progress.hide();
//                Toast.makeText(this, "Can't fetch data", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.d("Response handler", response.body().string());
                Log.d("Response, ",  response.message());
                HttpHandler httpHandler = new HttpHandler();

                String jsonStr = httpHandler.makeServiceCall(url);
                Log.e(LOG_TAG, "Response from url: " + jsonStr);

//                progress.hide();
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Log.i(LOG_TAG, "the json object, " + jsonObj);
                    JSONObject mainObject = jsonObj.getJSONObject("main");
                    Log.i(LOG_TAG, "the main object," + mainObject);
                    temp = mainObject.getDouble("temp");
                    Log.i(LOG_TAG, "the temp, " + temp);

                    FragmentWeatherDisplay fragmentWeatherDisplay = FragmentWeatherDisplay.newInstance(cityName, temp);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragmentWeatherDisplay);
                    fragmentTransaction.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(this, "Can't fetch data", Toast.LENGTH_LONG).show();
                } catch (NullPointerException e){
                    e.printStackTrace();
//                    Toast.makeText(this, "Can't fetch data", Toast.LENGTH_LONG).show();
                }

            }
        });

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

