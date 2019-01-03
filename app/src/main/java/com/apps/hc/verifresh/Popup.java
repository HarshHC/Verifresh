package com.apps.hc.verifresh;

/**
 * Created by HarshC on 6/19/2018.
 */

public class Popup {
    private String name;
    private String health;
    private String accuracy;
    private String description;

    public Popup(String name, int hlth, Boolean acrcy, String description) {
        this.name = name;
        if(hlth==1){
            this.health = "Fresh / Healthy";
        }else if(hlth==2){
            this.health = "Not Fresh / Unhealthy";
        }else if(hlth==0){
            this.health = "";
        }
        if(acrcy){
            this.accuracy="LOW ACCURACY";
        }else this.accuracy="";
        this.description = description;
    }

    public String getName() {
        return name.substring(0,1).toUpperCase()+name.substring(1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
