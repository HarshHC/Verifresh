package com.apps.hc.verifresh;

/**
 * Created by HarshC on 6/13/2018.
 */

public class Fruitifier {

    private static final int BAD=2;
    private static final int GOOD=1;
    private static final int NEUTRAL=0;

    public Fruitifier() {
    }

    public Popup getFinalOutput(String fruit){
        int isGood=NEUTRAL;
        fruit=fruit.toLowerCase();
        if(fruit.contains("good")){
            isGood=GOOD;
            fruit=fruit.replace("good","");
        }else if (fruit.contains("bad")){
            isGood=BAD;
            fruit=fruit.replace("bad","");
        }

        boolean accuracyLow=false;
        if (fruit.contains("low")){
            accuracyLow=true;
            fruit=fruit.replace("low","");
        }

        fruit=fruit.trim();
        String desc=getDesc(fruit);

        return new Popup(fruit,isGood,accuracyLow,desc);
    }

    private String getDesc(String fruit) {
        switch (fruit) {
            case "apple":
                return "";
            case "watermelon":
                return "Ones with a yellowish spot are fresh. Point towards it to confirm.";
            case "banana":
                return "";
            case "brinjal":
                return "";
            case "carrot":
                return "";
            case "coriander":
                return "";
            case "cabbage":
                return "";
            case "cheeku":
                return "";
            case "mint":
                return "";
            case "green capsicum":
                return "";
            case "ladyfinger":
                return "";
            case "mango":
                return "";
            case "melon":
                return "Good ones have a fruity smell";
            case "onion":
                return "";
            case "parthenium":
                return "Looks simillar to Corriander leaves and often mixed with it. It is not good for health.";
            case "peas":
                return "";
            case "pomegranate":
                return "Bad ones usually have dark spots";
            case "potato":
                return "";
            case "tomato":
                return "They are completely red. Bad ones are slightly yellowish.";
            case "waxy apple":
                return "This apple has wax on it which is not good for health. Waxy apples look very shiny.";
            default:
                return "ERROR";
        }
    }

    public String fruitify(String fruit){

        switch (fruit) {
            case "apple":
                return "Fresh/Healthy Apple";
            case "bad apple":
                return "Unhealthy/Not fresh Apple";
            case "bad brinjal":
                return bad("Brinjal");
            case "bad cabbage":
                return bad("Cabbage");
            case "bad carrot":
                return bad("Cabbage");
            case "bad cheeku":
                return bad("Cheeku");
            case "bad mango":
                return bad("Mango");
            case "bad mint":
                return bad("Mint Leaves");
            case "bad potato":
                return bad("Potato");
            case "bad tomato":
                return bad("Tomato");
            case "bad watermelon":
                return bad("Watermelon\n\n Turn around to be 100% sure. Check for a yellow spot to verify freshness.");
            case "banana":
                return good("Banana");
            case "brinjal":
                good("Brinjal");
            case "carrot":
                good("Carrot");
            case "coriander":
                return good("Corriander leaves");
            case "good cabbage":
                return good("Cabbage");
            case "good cheeku":
                return good("Cheeku");
            case "good mint":
                return good("Mint leaves");
            case "good watermelon":
                return good("Watermelon");
            case "green capsicum":
                return good("Capsicum");
            case "ladyfinger":
                return good("Ladyfinger");
            case "mango":
                return good("Mango");
            case "melon":
                return good("Melon\n\nIt should have a fruity smell.");
            case "onion":
                return good("Onion");
            case "parthenium":
                return "Parthenium Leaves\n\n Looks simillar to Corriander leaves and often mixed with it. It is not good for health.";
            case "peas":
                return good("Peas");
            case "pomegranate":
                return good("Pomogranate\n\nBad ones usually have dark spots");
            case "potato":
                return good("Potato");
            case "tomato":
                return good("Tomato\n\nThey are completely red.Bade ones are yellowish.");
            case "waxy apple":
                return "Waxy Apple\n\nThis apple has wax on it which is not good for health.Waxy apple look very shiny.";
            case "bad banana":
                return bad("Banana");
            default:
                return "ERROR";
        }
    }



    private String bad(String item){
        return "Unhealthy/Not fresh "+ item;
    }

    private String good(String item){
        return "Fresh/Healthy "+ item;
    }

}
