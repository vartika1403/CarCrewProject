package com.example.vartikasharma.mynewproject;


import java.util.ArrayList;

public class MainWeatherClass {
    private City city;
    private int cod;
    private double message;
    private int cnt;
    private ArrayList<WeatherObject> list;


    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public double getMessage() {
        return message;
    }

    public void setMessage(double message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public ArrayList<WeatherObject> getList() {
        return list;
    }

    public void setList(ArrayList<WeatherObject> list) {
        this.list = list;
    }
}
