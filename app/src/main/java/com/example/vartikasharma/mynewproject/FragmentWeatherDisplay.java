package com.example.vartikasharma.mynewproject;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentWeatherDisplay extends Fragment {
    private static final String CITY_NAME = FragmentWeatherDisplay.class.getSimpleName() + ".city_name" ;
    private static final String CITY_TEMP = FragmentWeatherDisplay.class.getSimpleName() + ".temp";
    private static final String CITY_PRESSURE = FragmentWeatherDisplay.class.getSimpleName() + ".pressure";
    private static final String CITY_HUMIDITY = FragmentWeatherDisplay.class.getSimpleName() + ".humidity";

    @BindView(R.id.text_city_name)
    TextView textCityName;
    @BindView(R.id.temperature)
    TextView textCityTemp;
    @BindView(R.id.pressure)
    TextView textCityPressure;
    @BindView(R.id.humidity)
    TextView textCityHumidity;

    private String cityName;
    private double cityTemp;
    private double cityPressure;
    private double cityHumidity;


    public static FragmentWeatherDisplay newInstance(String city, double temp, double pressure, double humidity) {
        FragmentWeatherDisplay fragmentWeatherDisplay = new FragmentWeatherDisplay();
        Bundle args = new Bundle();
        args.putString(CITY_NAME, city);
        args.putDouble(CITY_TEMP, temp);
        args.putDouble(CITY_PRESSURE, pressure);
        args.putDouble(CITY_HUMIDITY, humidity);
        fragmentWeatherDisplay.setArguments(args);
        return fragmentWeatherDisplay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityName = getArguments().getString(CITY_NAME);
            cityTemp = getArguments().getDouble(CITY_TEMP);
            cityPressure = getArguments().getDouble(CITY_PRESSURE);
            cityHumidity = getArguments().getDouble(CITY_HUMIDITY);
        }
    }

    private double convertTemp(Double cityTemp) {
        cityTemp = (cityTemp - 273.15);
        return cityTemp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_weather_display, container, false);
        ButterKnife.bind(this, view);
        textCityName.setText(cityName);
        double cityTempCelsius = convertTemp(cityTemp);
        String temp = String.format("%.2f", cityTempCelsius);
        textCityTemp.setText(temp + (char) 0x00B0 );
        textCityPressure.setText(String.valueOf(cityPressure));
        textCityHumidity.setText(String.valueOf(cityHumidity));

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return view;
    }
}
