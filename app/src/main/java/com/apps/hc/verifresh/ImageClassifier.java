/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.apps.hc.verifresh;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/** Classifies images with Tensorflow Lite. */
public class ImageClassifier {

  /** Tag for the {@link Log}. */
  private static final String TAG = "VeriFresh_classifier";

  /** Name of the model file stored in Assets. */
  private static final String MODEL_PATH = "graph3.lite";
    private static final String MODEL_PATH2= "graph.lite";
    private static final String MODEL_PATH3= "graph2.lite";

  /** Name of the label file stored in Assets. */
  private static final String LABEL_PATH = "labels3.txt";
  private static final String LABEL_PATH2 = "labels.txt";
    private static final String LABEL_PATH3 = "labels2.txt";

  /** Number of results to show in the UI. */
  private static final int RESULTS_TO_SHOW = 5;

  /** Dimensions of inputs. */
  private static final int DIM_BATCH_SIZE = 1;

  private static final int DIM_PIXEL_SIZE = 3;

  static final int DIM_IMG_SIZE_X = 224;
  static final int DIM_IMG_SIZE_Y = 224;

  private static final int IMAGE_MEAN = 128;
  private static final float IMAGE_STD = 128.0f;

  //Result calc
    ResultCalculator resultCalculator;

  /* Preallocated buffers for storing image data in. */
  private int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

  /** An instance of the driver class to run model inference with Tensorflow Lite. */
  private Interpreter tflite;
  private Interpreter intpr;
    private Interpreter tflite3;

  /** Labels corresponding to the output of the vision model. */
    private List<String> labelList;
    private List<String> labelList2;
    private List<String> labelList3;

    /**Labels and Accuracy lists**/
    private List<Prediction> predictions1=new ArrayList<>();
    private List<Prediction> predictions2=new ArrayList<>();
    private List<Prediction> predictions3=new ArrayList<>();

  /** A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs. */
  private ByteBuffer imgData = null;

  /** An array to hold inference results, to be feed into Tensorflow Lite as outputs. */
  private float[][] labelProbArray = null;
    private float[][] labelProbArray2 = null;
    private float[][] labelProbArray3 = null;

  /** multi-stage low pass filter **/
  private float[][] filterLabelProbArray = null;
    private float[][] filterLabelProbArray2 = null;
    private float[][] filterLabelProbArray3 = null;
  private static final int FILTER_STAGES = 3;
  private static final float FILTER_FACTOR = 0.4f;

  private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
      new PriorityQueue<>(
          RESULTS_TO_SHOW,
          new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
              return (o1.getValue()).compareTo(o2.getValue());
            }
          });
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels2 =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels3 =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

  /** Initializes an {@code ImageClassifier}. */
  ImageClassifier(Activity activity) throws IOException {
    tflite = new Interpreter(loadModelFile(activity));
    intpr=new Interpreter(loadModelFile2(activity));
    tflite3=new Interpreter(loadModelFile3(activity));
    labelList = loadLabelList(activity);
    labelList2=loadLabelList2(activity);
    labelList3=loadLabelList3(activity);
    resultCalculator=new ResultCalculator(activity);
    imgData =
        ByteBuffer.allocateDirect(
            4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
    imgData.order(ByteOrder.nativeOrder());
    labelProbArray = new float[1][labelList.size()];
    labelProbArray2 = new float[1][labelList2.size()];
      labelProbArray3 = new float[1][labelList3.size()];
    filterLabelProbArray = new float[FILTER_STAGES][labelList.size()];
    filterLabelProbArray2 = new float[FILTER_STAGES][labelList2.size()];
      filterLabelProbArray3 = new float[FILTER_STAGES][labelList3.size()];
    Log.d(TAG, "Created a Tensorflow Lite Image Classifier.");
  }

  /** Classifies a frame from the preview stream. */
  Prediction classifyFrame(Bitmap bitmap) {
      clearAll();
    if (tflite == null) {
      Log.e(TAG, "Image classifier has not been initialized; Skipped.");
      return new Prediction("init",0.0f);
    }
    convertBitmapToByteBuffer(bitmap);
    // Here's where the magic happens!!!
    long startTime = SystemClock.uptimeMillis();
    tflite.run(imgData, labelProbArray);
    intpr.run(imgData,labelProbArray2);
      tflite3.run(imgData,labelProbArray3);
    long endTime = SystemClock.uptimeMillis();
    Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));

    // smooth the results
    applyFilter();

    // print the results
    //String textToShow = printTopKLabels();
    //textToShow = Long.toString(endTime - startTime) + "ms" + textToShow;

      Prediction pred=printTopKLabels();
    return pred;
  }

  void applyFilter(){
    int num_labels =  labelList.size();

    // Low pass filter `labelProbArray` into the first stage of the filter.
    for(int j=0; j<num_labels; ++j){
      filterLabelProbArray[0][j] += FILTER_FACTOR*(labelProbArray[0][j] -
                                                   filterLabelProbArray[0][j]);
    }
    // Low pass filter each stage into the next.
    for (int i=1; i<FILTER_STAGES; ++i){
      for(int j=0; j<num_labels; ++j){
        filterLabelProbArray[i][j] += FILTER_FACTOR*(
                filterLabelProbArray[i-1][j] -
                filterLabelProbArray[i][j]);

      }
    }

    // Copy the last stage filter output back to `labelProbArray`.
    for(int j=0; j<num_labels; ++j){
      labelProbArray[0][j] = filterLabelProbArray[FILTER_STAGES-1][j];
    }
    applyFilter2();
      applyFilter3();
  }

    void applyFilter2(){
        int num_labels =  labelList2.size();

        // Low pass filter `labelProbArray` into the first stage of the filter.
        for(int j=0; j<num_labels; ++j){
            filterLabelProbArray2[0][j] += FILTER_FACTOR*(labelProbArray2[0][j] -
                    filterLabelProbArray2[0][j]);
        }
        // Low pass filter each stage into the next.
        for (int i=1; i<FILTER_STAGES; ++i){
            for(int j=0; j<num_labels; ++j){
                filterLabelProbArray2[i][j] += FILTER_FACTOR*(
                        filterLabelProbArray2[i-1][j] -
                                filterLabelProbArray2[i][j]);

            }
        }

        // Copy the last stage filter output back to `labelProbArray2`.
        for(int j=0; j<num_labels; ++j){
            labelProbArray2[0][j] = filterLabelProbArray2[FILTER_STAGES-1][j];
        }
    }
    void applyFilter3(){
        int num_labels =  labelList3.size();

        // Low pass filter `labelProbArray` into the first stage of the filter.
        for(int j=0; j<num_labels; ++j){
            filterLabelProbArray3[0][j] += FILTER_FACTOR*(labelProbArray3[0][j] -
                    filterLabelProbArray3[0][j]);
        }
        // Low pass filter each stage into the next.
        for (int i=1; i<FILTER_STAGES; ++i){
            for(int j=0; j<num_labels; ++j){
                filterLabelProbArray3[i][j] += FILTER_FACTOR*(
                        filterLabelProbArray3[i-1][j] -
                                filterLabelProbArray3[i][j]);

            }
        }

        // Copy the last stage filter output back to `labelProbArray3`.
        for(int j=0; j<num_labels; ++j){
            labelProbArray3[0][j] = filterLabelProbArray3[FILTER_STAGES-1][j];
        }
    }


  /** Closes tflite to release resources. */
  public void close() {
    tflite.close();
    tflite = null;
    intpr.close();
    intpr=null;
      tflite3.close();
      tflite3 = null;
  }

  /** Reads label list from Assets. */
  private List<String> loadLabelList(Activity activity) throws IOException {
    List<String> labelList = new ArrayList<String>();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH)));
    String line;
    while ((line = reader.readLine()) != null) {
      labelList.add(line);
    }
    reader.close();
    return labelList;
  }
    private List<String> loadLabelList2(Activity activity) throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH2)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }
    private List<String> loadLabelList3(Activity activity) throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH3)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

  /** Memory-map the model file in Assets. */
  private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
    AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
    FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
    FileChannel fileChannel = inputStream.getChannel();
    long startOffset = fileDescriptor.getStartOffset();
    long declaredLength = fileDescriptor.getDeclaredLength();
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
  }

    private MappedByteBuffer loadModelFile2(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH2);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private MappedByteBuffer loadModelFile3(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH3);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

  /** Writes Image data into a {@code ByteBuffer}. */
  private void convertBitmapToByteBuffer(Bitmap bitmap) {
    if (imgData == null) {
      return;
    }
    imgData.rewind();
    bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    // Convert the image to floating point.
    int pixel = 0;
    long startTime = SystemClock.uptimeMillis();
    for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
      for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
        final int val = intValues[pixel++];
        imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
        imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
        imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
      }
    }
    long endTime = SystemClock.uptimeMillis();
    Log.d(TAG, "Timecost to put values into ByteBuffer: " + Long.toString(endTime - startTime));
  }

  /** Prints top-K labels, to be shown in UI as the results. */
  private Prediction printTopKLabels() {
    for (int i = 0; i < labelList.size(); ++i) {
      sortedLabels.add(
          new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
      if (sortedLabels.size() > RESULTS_TO_SHOW) {
        sortedLabels.poll();
      }
    }

      for (int i = 0; i < labelList2.size(); ++i) {
          sortedLabels2.add(
                  new AbstractMap.SimpleEntry<>(labelList2.get(i), labelProbArray2[0][i]));
          if (sortedLabels2.size() > RESULTS_TO_SHOW) {
              sortedLabels2.poll();
          }
      }

      for (int i = 0; i < labelList3.size(); ++i) {
          sortedLabels3.add(
                  new AbstractMap.SimpleEntry<>(labelList3.get(i), labelProbArray3[0][i]));
          if (sortedLabels3.size() > RESULTS_TO_SHOW) {
              sortedLabels3.poll();
          }
      }

    String textToShow = "";
    final int size = sortedLabels.size();
      final int size2 = sortedLabels2.size();
      final int size3 = sortedLabels3.size();
      for (int i = 0; i < size3; ++i) {
          Map.Entry<String, Float> label3 = sortedLabels3.poll();
          textToShow = String.format("\n%s: %4.2f",label3.getKey()+"3",label3.getValue()) +textToShow;
          Prediction prediction=new Prediction(label3.getKey(),label3.getValue());
          predictions3.add(prediction);
      }
      for (int i = 0; i < size2; ++i) {
          Map.Entry<String, Float> label2 = sortedLabels2.poll();
          textToShow = String.format("\n%s: %4.2f",label2.getKey()+"2",label2.getValue()) +textToShow;
          Prediction prediction=new Prediction(label2.getKey(),label2.getValue());
          predictions2.add(prediction);
      }
    for (int i = 0; i < size; ++i) {
      Map.Entry<String, Float> label = sortedLabels.poll();
      textToShow = String.format("\n%s: %4.2f",label.getKey(),label.getValue()) + textToShow;
        Prediction prediction=new Prediction(label.getKey(),label.getValue());
        predictions1.add(prediction);
    }

    //textToShow=textToShow+"\n-hc-\n";

     //textToShow=resultCalculator.AnalyzeResults(predictions1,predictions2,predictions3);
      Prediction pred=resultCalculator.AnalyzeResults(predictions1,predictions2,predictions3);
    return pred;
  }

  public void clearAll(){
        predictions1.clear();
        predictions2.clear();
        predictions3.clear();
    }

}
