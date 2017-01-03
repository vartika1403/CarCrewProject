package com.example.vartikasharma.mynewproject;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient client;
    private String url;
    private Double temp;
    private FragmentEnterCityName fragmentEnterCityName;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        client = new OkHttpClient();

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

        fragmentEnterCityName.showLoader();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(getApplicationContext(), "Can't fetch data", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResponse(Response response) throws IOException {
                HttpHandler httpHandler = new HttpHandler();
                String jsonStr = httpHandler.makeServiceCall(url);

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject mainObject = jsonObj.getJSONObject("main");
                    temp = mainObject.getDouble("temp");

                    FragmentWeatherDisplay fragmentWeatherDisplay = FragmentWeatherDisplay.newInstance(cityName, temp);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragmentWeatherDisplay);
                    fragmentTransaction.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                  Toast.makeText(getApplicationContext(), "Can't fetch data", Toast.LENGTH_LONG).show();
                } catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Can't fetch data", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}

