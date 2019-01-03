package com.apps.hc.verifresh;

public class Additive {
    public static final String COLOUR="colour"; //100-199
    public static final String PRESERVATIVE="preservative"; //200-299
    public static final String ANTIOXIDANT="antioxidant"; //300-399
    public static final String EMULSIFIER="emulsifier"; //400-499
    public static final String ACIDITY_REGULATOR="acidity regulator"; //500-599
    public static final String FLAVOUR="flavour"; //600-699
    private String mCode;
    private String mName;
    private String mType;
    private boolean isHarmful;
    private boolean isCancerous;
    private boolean isHyperActivivityCausing;
    private boolean isAsthmaCausing;

    public Additive(String mCode, String mName, String mType, boolean isHarmful, boolean isCancerous, boolean isHyperActivivityCausing, boolean isAsthmaCausing) {
        this.mCode = mCode;
        this.mName = mName;
        this.mType = mType;
        this.isHarmful = isHarmful;
        this.isCancerous = isCancerous;
        this.isHyperActivivityCausing = isHyperActivivityCausing;
        this.isAsthmaCausing = isAsthmaCausing;
        determineType(mCode);
    }

    public Additive(String mCode, String mType, boolean isHarmful, boolean isCancerous, boolean isHyperActivivityCausing, boolean isAsthmaCausing) {
        this.mCode = mCode;
        this.mType = mType;
        this.isHarmful = isHarmful;
        this.isCancerous = isCancerous;
        this.isHyperActivivityCausing = isHyperActivivityCausing;
        this.isAsthmaCausing = isAsthmaCausing;
        determineType(mCode);
    }

    public void determineType(String code_){
        int code=Integer.parseInt(code_);
        if(code>=100&&code<200){
            setmType(COLOUR);
        }else if(code>=200&&code<300){
            setmType(PRESERVATIVE);
        }else if(code>=300&&code<400){
            setmType(ANTIOXIDANT);
        }else if(code>=400&&code<500){
            setmType(EMULSIFIER);
        }else if(code>=500&&code<600){
            setmType(ACIDITY_REGULATOR);
        }else if(code>=600&&code<700){
            setmType(FLAVOUR);
        }
    }


    public String getCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public boolean isHarmful() {
        return isHarmful;
    }

    public void setHarmful(boolean harmful) {
        isHarmful = harmful;
    }

    public boolean isCancerous() {
        return isCancerous;
    }

    public void setCancerous(boolean cancerous) {
        isCancerous = cancerous;
    }

    public boolean isHyperActivivityCausing() {
        return isHyperActivivityCausing;
    }

    public void setHyperActivivityCausing(boolean hyperActivivityCausing) {
        isHyperActivivityCausing = hyperActivivityCausing;
    }

    public boolean isAsthmaCausing() {
        return isAsthmaCausing;
    }

    public void setAsthmaCausing(boolean asthmaCausing) {
        isAsthmaCausing = asthmaCausing;
    }
}
