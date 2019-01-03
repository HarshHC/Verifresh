package com.apps.hc.verifresh;

/**
 * Created by HarshC on 6/10/2018.
 */

public class Prediction {

    private String label;
    private Float confidence;

    public Prediction(String label, Float confidence) {
        this.label = label;
        this.confidence = confidence;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Float getConfidence() {
        return confidence;
    }

    public void setConfidence(Float confidence) {
        this.confidence = confidence;
    }
}
