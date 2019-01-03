package com.apps.hc.verifresh;

import android.app.Activity;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by HarshC on 6/10/2018.
 */

public class ResultCalculator {

    private Activity act;
    private ImageClassifier classifier;
    private Prediction pred;
    private Prediction pred1,pred2,pred3;

    public ResultCalculator(Activity activity) {
        this.act = activity;
    }

    public Prediction AnalyzeResults(List<Prediction> list1, List<Prediction> list2, List<Prediction> list3) {

        if (list1.size() != list2.size() || list1.size() != list3.size()) {
            Toast.makeText(act, "ERRORRRR", Toast.LENGTH_LONG).show();
            return null;
        }

        Collections.reverse(list1);
        Collections.reverse(list2);
        Collections.reverse(list3);


        Prediction rank1=findFinalResultForRank(1,list1,list2,list3);
        //Prediction rank2=findFinalResultForRank(2,list1,list2,list3);
        //Prediction rank3=findFinalResultForRank(3,list1,list2,list3);

        return rank1;
    }


    private Prediction findFinalResultForRank(int rank, List<Prediction> list1, List<Prediction> list2, List<Prediction> list3){
        rank=rank-1;
        pred1 = list1.get(rank);
        pred2 = list2.get(rank);
        pred3 = list3.get(rank);

        if (pred1.getConfidence() > 0.6) {
            return formatResult(pred1.getLabel(), pred1.getConfidence());
        }
//        //If any label has 100% confidence then it is final.
//        if (pred1.getConfidence() == 1.0) {
//            return formatResult(pred1.getLabel(), pred1.getConfidence());
//        } else if (pred2.getConfidence() == 1.0) {
//            return formatResult(pred2.getLabel(), pred2.getConfidence());
//        } else if (pred3.getConfidence() == 1.0) {
//            return formatResult(pred3.getLabel(), pred3.getConfidence());
//        }
//        //If all 3 labels are same
//        else if (Objects.equals(pred1.getLabel(), pred2.getLabel()) && Objects.equals(pred1.getLabel(), pred3.getLabel())) {
//            return formatResult(pred1.getLabel(), pred1.getConfidence());
//        }
//        //If pred1 and pred2 have accuracy over 90
//        else if (Objects.equals(pred1.getLabel(), pred2.getLabel()) && pred1.getConfidence() > 0.9 && pred2.getConfidence() > 0.9) {
//            return formatResult(pred1.getLabel(), (pred1.getConfidence()));
//        }
//        //If pred3 is potato with more than 90 accuracy
//        else if (pred3.getLabel().contains("potato")&& pred3.getConfidence() > 0.9) {
//            return formatResult(pred3.getLabel(), (pred3.getConfidence()));
//        }
//        //If pred1 and pred2 have accuracy above 70 and pred3 has simillar label
//        else if (Objects.equals(pred1.getLabel(), pred2.getLabel()) && pred1.getConfidence() > 0.7 && pred2.getConfidence() > 0.7 && pred1.getLabel().contains(pred3.getLabel())) {
//            return formatResult(pred1.getLabel(), (pred1.getConfidence()));
//        }
//        //If pred1 and pred2 have accuracy above 80
//        else if (Objects.equals(pred1.getLabel(), pred2.getLabel()) && pred1.getConfidence() > 0.8 && pred2.getConfidence() > 0.8) {
//            return formatResult(pred1.getLabel(), (pred1.getConfidence()));
//        }
//        //Low accuracy
//        else if (Objects.equals(pred1.getLabel(), pred2.getLabel()) && pred1.getConfidence() > 0.5 && pred2.getConfidence() > 0.5) {
//            return formatResult(pred1.getLabel() + " LOW", (pred1.getConfidence()));
//        }
        //No conclusion
        else return new Prediction("none",0.0f);
    }

    private Prediction formatResult(String label, Float confidence) {
//        int accuracy= (int) (confidence*100);
//        return "\n"+label+" : "+accuracy+"%";
        Prediction prediction=new Prediction(label, confidence);
        return prediction;
    }


}


