package com.example.vartikasharma.carcrew;


public class Conf {
    private final static String fbaseDomainURI = "https://carcrew-project.firebaseio.com/";

    public static String firebaseUserDataURI() {
        return fbaseDomainURI + "data/";
    }

    public static String firebaseUserOpenQueries() {
        return fbaseDomainURI + "open/";
    }
}
