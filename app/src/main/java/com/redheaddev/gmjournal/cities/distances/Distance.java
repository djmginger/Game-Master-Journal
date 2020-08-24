package com.redheaddev.gmjournal.cities.distances;

public class Distance {

    // Store the name of the preset
    private String toCity;
    private String distance;

    // Constructor that is used to create an instance of the Preset object

    public Distance(String toCity, String distanceValue) {
        this.toCity = toCity;
        this.distance = distanceValue;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String mName) {
        this.distance = mName;
    }

}

