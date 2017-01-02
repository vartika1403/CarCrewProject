package com.example.vartikasharma.mynewproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentWeatherDisplay extends Fragment {
    private static final String CITY_NAME = FragmentWeatherDisplay.class.getSimpleName() + ".city_name" ;
    @BindView(R.id.text_city_name)
    TextView textCityName;


    public static FragmentWeatherDisplay newInstance(String param1) {
        FragmentWeatherDisplay fragmentWeatherDisplay = new FragmentWeatherDisplay();
        Bundle args = new Bundle();
        args.putString(CITY_NAME, param1);
      //  args.putString(ARG_PARAM2, param2);
        fragmentWeatherDisplay.setArguments(args);
        return fragmentWeatherDisplay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          /*  mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_weather_display, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
