package com.apps.hc.verifresh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class IngredientsActivity extends AppCompatActivity {

    private static final String TAG ="IngredientsActivity//";
    private ImageButton imageButton;
    private Uri mCropImageUri;
    private FirebaseVisionImage image;
    private ScrollView bottom;
    private List<String> preservatives,            //200-299
            flavours,                              //600-699
            emulsifiers,                           //400-499
            colors,                                //100-199
            acidity_regulators,                    //500-599
            antioxidants;                          //300-399
    private List<Additive> Additives;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        imageButton = findViewById(R.id.select_btn);
        imageButton.setOnClickListener(this::onSelectImageClick);

        preservatives=new ArrayList<>();
        flavours=new ArrayList<>();
        emulsifiers=new ArrayList<>();
        colors=new ArrayList<>();
        acidity_regulators=new ArrayList<>();
        antioxidants=new ArrayList<>();
        Additives=new ArrayList<>();

        bottom=findViewById(R.id.bottombar);
        bottom.setVisibility(View.GONE);
        bottom.setAlpha(0.0f);
        bottom.animate()
                .translationY(bottom.getHeight())
                .setDuration(2000);

    }

    /**
     * Start pick image activity with chooser.
     */
    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //Recognize text
                try {
                    image = FirebaseVisionImage.fromFilePath(IngredientsActivity.this, result.getUri());
                    imageButton.setImageURI(result.getUri());
                    findViewById(R.id.select_txt).setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                detectText(image);


//                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void detectText(FirebaseVisionImage image) {
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();

        Task<FirebaseVisionText> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(firebaseVisionText -> {
                            if(bottom.getVisibility()==View.GONE){
                                bottom.setVisibility(View.VISIBLE);
                                //bottom.setAlpha(0.0f);
                                bottom.animate()
                                        .translationY(bottom.getHeight())
                                        .alpha(1.0f)
                                        .setDuration(1000);
                            }

                            for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()) {
                                String text = block.getText();
                                String text2=scanTextForAdditives(text);
                                TextView output=findViewById(R.id.output);
                                output.setText(text2+"\n\nDetected text:\n\n"+text);
                                TextView op=findViewById(R.id.output_txt);
                                op.setText("Detected text:\n\n"+text);

                            }
                        })
                        .addOnFailureListener(
                                e -> Log.e(TAG,e.toString()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAutoZoomEnabled(true)
                .setActivityTitle("Select Ingredients")
                .start(this);
    }

    public void info(View view) {
        SimpleTarget target1 = new SimpleTarget.Builder(this)
                .setPoint(findViewById(R.id.select_btn)) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(300f) // radius of the Target
                .setTitle("Select Image") // title
                .setDescription("Click to select image from camera or gallery and then crop it show only the ingredients on a food product") // description
                .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                    @Override
                    public void onStarted(SimpleTarget target) {
                        // do something
                    }
                    @Override
                    public void onEnded(SimpleTarget target) {
                        // do something
                    }
                })
                .build();

        Spotlight.with(this)
                .setDuration(1000L) // duration of Spotlight emerging and disappearing in ms
                .setAnimation(new DecelerateInterpolator(2f)) // animation of Spotlight
                .setTargets(target1) // set targets. see below for more info
                .setOnSpotlightStartedListener(new OnSpotlightStartedListener() { // callback when Spotlight starts
                    @Override
                    public void onStarted() {
                        Toast.makeText(IngredientsActivity.this, "spotlight is started", Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnSpotlightEndedListener(new OnSpotlightEndedListener() { // callback when Spotlight ends
                    @Override
                    public void onEnded() {
                        Toast.makeText(IngredientsActivity.this, "spotlight is ended", Toast.LENGTH_SHORT).show();
                    }
                })
                .start(); // start Spotlight
    }

    public String scanTextForAdditives(String text){

        preservatives.clear();
        colors.clear();
        flavours.clear();
        emulsifiers.clear();
        text=text.toLowerCase();

       // List<Integer> e_numbers=findCodes(text);
        //List<Additive> =prepareAdditives(e_numbers);

        List<String> codes=new ArrayList<>();
        codes.add("e");
        codes.add("ins");
        for(int j=0;j<codes.size();j++){

            for(int i=100;i<200;i++){
                if (text.contains(codes.get(j)+"-"+i)){
                    colors.add(codes.get(j)+"-"+i+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }else if (text.contains(codes.get(j)+i)){
                    colors.add(codes.get(j)+i+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }else if (text.contains("("+i+")")){
                    colors.add("INS-"+"("+i+")"+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }
            }
            for(int i=200;i<300;i++){
                if (text.contains(codes.get(j)+"-"+i)){
                    preservatives.add(codes.get(j)+"-"+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }else if (text.contains(codes.get(j)+i)){
                    preservatives.add(codes.get(j)+i+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }else if (text.contains("("+i+")")){
                    preservatives.add("INS-"+"("+i+")"+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }
            }
            for(int i=400;i<500;i++){
                if (text.contains(codes.get(j)+"-"+i)){
                    emulsifiers.add(codes.get(j)+"-"+i+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }else if (text.contains(codes.get(j)+i)){
                    emulsifiers.add(codes.get(j)+i+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }else if (text.contains("("+i+")")){
                    emulsifiers.add("INS-"+"("+i+")"+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }
            }
            for(int i=600;i<700;i++){
                if (text.contains(codes.get(j)+"-"+i)){
                    flavours.add(codes.get(j)+"-"+i+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }else if (text.contains(codes.get(j)+i)){
                    flavours.add(codes.get(j)+i+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }else if (text.contains("("+i+")")){
                    flavours.add("INS-"+"("+i+")"+getAdditiveData(i+"")+"\n"+getHarmfulinfo(i+"")+"\n\n");
                }
            }

        }

        String output="This product contains:\n";
        if(colors.size()!=0){
            output=output+"\nArtifical colours: \n";
            for(int i=0;i<colors.size();i++){
                output=output+ colors.get(i)+" ";
            }
        }else {
            output=output+"\nNo added colors\n";
        }
        if(emulsifiers.size()!=0){
            output=output+"\nEmulsifiers: \n";
            for(int i=0;i<emulsifiers.size();i++){
                output=output+ emulsifiers.get(i)+" ";
            }
        }else {
            output=output+"\nNo added emulsifiers\n";
        }
        if(preservatives.size()!=0){
            output=output+"\nPreservatives: \n";
            for(int i=0;i<preservatives.size();i++){
                output=output+ preservatives.get(i)+" ";
            }
        }else {
            output=output+"\nNo added preservatives\n";
        }
        if(flavours.size()!=0){
            output=output+"\nArtifical flavours: \n";
            for(int i=0;i<flavours.size();i++){
                output=output+ flavours.get(i)+" ";
            }
        }else {
            output=output+"\nNo added flavours\n";
        }
        return output;
    }

    private List<Additive> prepareAdditives(List<Integer> codes) {
        List<Additive> temp=new ArrayList<>();
        for(int i=0;i<codes.size();i++){
            String name=getAdditiveData(codes.get(i)+"");
        }


        return null;
    }

    private List<Integer> findCodes(String text) {
        List<String> precodes=new ArrayList<>();
        List<Integer> codes=new ArrayList<>();
        precodes.add("e");
        precodes.add("ins");
        for(int j=0;j<precodes.size();j++) {

            for (int i = 100; i < 700; i++) {
                if (text.contains(precodes.get(j) + "-" + i)) {
                    codes.add(i);
                } else if (text.contains(precodes.get(j) + i)) {
                    codes.add(i);
                } else if (text.contains("(" + i + ")")) {
                    codes.add(i);
                }else if (text.contains("," + i)) {
                    codes.add(i);
                }else if (text.contains(", " + i)) {
                    codes.add(i);
                }
            }
        }
        return codes;
    }

    public String getAdditiveData(String code){
        InputStream is=null;

        try {

            is=getAssets().open("ins_wiki.html");
            org.jsoup.nodes.Document doc = Jsoup.parse(is, "UTF-8", "http://example.com/");

            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");

            for (int i = 2; i < rows.size(); i++) { //first/second row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");

                if (cols.get(0).text().equals(code)) {
                   String additive=cols.get(4).text();
                   String category=cols.get(5).text();

                   return additive +"("+category+")";
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "NOT FOUND";
    }

    public String getHarmfulinfo(String code){
        InputStream is=null;
        String output="NOT Harmful ";

        try {

            is=getAssets().open("harmful.html");
            org.jsoup.nodes.Document doc = Jsoup.parse(is, "UTF-8", "http://example.com/");

            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");

            for (int i = 2; i < rows.size(); i++) { //first/second row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");
                if (cols.get(0).text().contains(code)) {
                    //String additive=cols.get(1).text();
                    boolean causesHAct=false;
                    boolean causesCancer=false;
                    boolean causesAsthma=false;
                    if(cols.get(2).text().contains("H")){
                       causesHAct=true;
                    }
                    if(cols.get(4).text().contains("C")){
                        causesCancer=true;
                    }
                    if(cols.get(3).text().contains("A")){
                        causesAsthma=true;
                    }

                    output="Harmful ";
                    if(causesAsthma){
                        output=output+"Asthma ";
                    }
                    if(causesCancer){
                        output=output+"Cancer ";
                    }
                    if(causesHAct){
                        output=output+"Hyper Activity ";
                    }

                    return output;
                        }
                    }

            return output;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "NOT FOUND";
    }

    public void start_search(View view) {
        EditText manual_search=findViewById(R.id.manual_search);
        String text=scanTextForAdditives(manual_search.getText().toString());

        TextView output=findViewById(R.id.output);
        output.setText(text+"\n\nDetected text:\n\n"+manual_search.getText().toString());

        if(bottom.getVisibility()==View.GONE){
            bottom.setVisibility(View.VISIBLE);
            //bottom.setAlpha(0.0f);
            bottom.animate()
                    .translationY(bottom.getHeight())
                    .alpha(1.0f)
                    .setDuration(1000);
        }
    }
}