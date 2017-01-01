package com.example.vartikasharma.mynewproject;


import java.util.HashMap;

public class WeatherObject {
    private int dt;
    private Main main;
    private Weather weather;
    private Clouds clouds;
    private Wind wind;
    private HashMap<String, Double> rain;
    private HashMap<String, Double> snow;
    private HashMap<String, String> sys;
    private String dt_txt;

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public HashMap<String, Double> getRain() {
        return rain;
    }

    public void setRain(HashMap<String, Double> rain) {
        this.rain = rain;
    }

    public HashMap<String, Double> getSnow() {
        return snow;
    }

    public void setSnow(HashMap<String, Double> snow) {
        this.snow = snow;
    }

    public HashMap<String, String> getSys() {
        return sys;
    }

    public void setSys(HashMap<String, String> sys) {
        this.sys = sys;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }
}
