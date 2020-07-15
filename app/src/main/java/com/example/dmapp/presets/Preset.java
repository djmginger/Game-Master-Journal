package com.example.dmapp.presets;

class Preset {

    // Store the name of the preset
    private String value;

    // Constructor that is used to create an instance of the Preset object
    public Preset(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String mName) {
        this.value = mName;
    }

}

