package com.example.vartikasharma.mynewproject;


import java.util.HashMap;

public class City {
    private int id;
    private String name;
    private HashMap<String,Double> coord;
    private String country;
    private int population;
    private HashMap<Integer,Integer> sys;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Double> getCoord() {
        return coord;
    }

    public void setCoord(HashMap<String, Double> coord) {
        this.coord = coord;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public HashMap<Integer, Integer> getSys() {
        return sys;
    }

    public void setSys(HashMap<Integer, Integer> sys) {
        this.sys = sys;
    }
}
