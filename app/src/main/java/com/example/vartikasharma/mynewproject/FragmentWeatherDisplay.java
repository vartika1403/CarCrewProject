package com.example.vartikasharma.mynewproject;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentWeatherDisplay extends Fragment {
    private static final String CITY_NAME = FragmentWeatherDisplay.class.getSimpleName() + ".city_name" ;
    private static final String CITY_TEMP = FragmentWeatherDisplay.class.getSimpleName() + ".temp";
    @BindView(R.id.text_city_name)
    TextView textCityName;
    @BindView(R.id.temperature)
    TextView textCityTemp;
    private String cityName;
    private Double cityTemp;


    public static FragmentWeatherDisplay newInstance(String city, Double temp) {
        FragmentWeatherDisplay fragmentWeatherDisplay = new FragmentWeatherDisplay();
        Bundle args = new Bundle();
        args.putString(CITY_NAME, city);
        args.putDouble(CITY_TEMP, temp);
        fragmentWeatherDisplay.setArguments(args);
        return fragmentWeatherDisplay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityName = getArguments().getString(CITY_NAME);
            cityTemp = getArguments().getDouble(CITY_TEMP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_weather_display, container, false);
        ButterKnife.bind(this, view);
        textCityName.setText(cityName);
        String temp = String.valueOf(cityTemp);
        Log.i(CITY_NAME, "the temp, " + temp);
        textCityTemp.setText(temp);
        return view;
    }
}
