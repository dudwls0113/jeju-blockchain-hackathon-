package com.hackthon.jejuhackathon.src.ride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.hackthon.jejuhackathon.R;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;


import java.util.List;

import ai.fritz.core.Fritz;
import ai.fritz.core.FritzManagedModel;
import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.objectdetectionmodelfast.ObjectDetectionOnDeviceModel;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionObject;
import ai.fritz.vision.PredictorStatusListener;
import ai.fritz.vision.objectdetection.FritzVisionObjectPredictor;
import ai.fritz.vision.objectdetection.FritzVisionObjectResult;
import ai.fritz.vision.objectdetection.ObjectDetectionManagedModel;

public class RideActivity extends AppCompatActivity {


    RenderScript renderScript;
    ScriptIntrinsicYuvToRGB yuvToRGB;
    int yuvDataLength = 0;
    Allocation allocationIn;
    Allocation allocationOut;
    Bitmap bitmapOut;
    CameraView cameraView;
    FritzVisionImage fritzVisionImage;
    FritzOnDeviceModel onDeviceModel;
    FritzVisionObjectPredictor objectPredictor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        cameraView = findViewById(R.id.cameraView);

        cameraView.setLifecycleOwner(this); //Automatically handles the camera lifecycle

//        Fritz.configure(this, "d6a754dde5c3471eb289ca96dcdecce4");
//        init();
//
//        ObjectDetectionOnDeviceModel = new onDeviceModel = new ObjectDetectionOnDeviceModel();
//        val objectPredictor = FritzVision.ObjectDetection.getPredictor(onDeviceModel);
//        var fritzVisionImage: FritzVisionImage
//        init();
    }

    void init() {
//        cameraView.setLifecycleOwner(this); //Automatically handles the camera lifecycle

//        FritzOnDeviceModel onDeviceModel = new ObjectDetectionOnDeviceModelFast();
//        FritzVisionObjectPredictor predictor = FritzVision.ObjectDetection.getPredictor(onDeviceModel);


//        ObjectDetectionOnDeviceModel onDeviceModel = new ObjectDetectionOnDeviceModel();
//        FritzVision objectPredictor = new FritzVision.getObjectDetection.getPredictor(onDeviceModel);

//        FritzVisionObjectResult objectResult = objectPredictor.predict(fritzVisionImage);
//        List<FritzVisionObject> visionObjects = objectResult.get();

//        FritzOnDeviceModel onDeviceModel = new ObjectDetectionOnDeviceModel();
        objectPredictor = FritzVision.ObjectDetection.getPredictor(onDeviceModel);
        onDeviceModel = new ObjectDetectionOnDeviceModel();


        FritzManagedModel managedModel = new ObjectDetectionManagedModel();
        FritzVision.ObjectDetection.loadPredictor(managedModel, new PredictorStatusListener<FritzVisionObjectPredictor>() {
            @Override
            public void onPredictorReady(FritzVisionObjectPredictor objectDetectionPredictor) {
                Log.d("아아", "Object Detection predictor is ready");
                objectPredictor = objectDetectionPredictor;
            }
        });

        cameraView.addFrameProcessor(new FrameProcessor() {
                                         @Override
                                         public void process(@NonNull Frame frame) {
                                             if (yuvDataLength == 0) {
                                                 //Run this only once
                                                 initializeData();
                                             }

                                             allocationIn.copyFrom(frame.getData());
                                             yuvToRGB.forEach(allocationOut);
                                             allocationOut.copyTo(bitmapOut);
                                             fritzVisionImage = FritzVisionImage.fromBitmap(bitmapOut);
                                             FritzVisionObjectResult objectResult = objectPredictor.predict(fritzVisionImage);
                                             for(int i=0; i< objectResult.getVisionObjectsByClass("person").size(); i++){
                                                 Log.d("결과", objectResult.getVisionObjectsByClass("person").get(i).getVisionLabel()+"");
                                             }
                                         }
                                     }
        );
    }

    private void initializeData() {
        yuvDataLength = 1000;
        renderScript = RenderScript.create(getBaseContext());
        yuvToRGB = ScriptIntrinsicYuvToRGB.create(renderScript, Element.U8_4(renderScript));
        allocationIn = Allocation.createSized(renderScript, Element.U8(renderScript), yuvDataLength);
        bitmapOut = Bitmap.createBitmap(6000 , 600, Bitmap.Config.ARGB_8888);
        allocationOut = Allocation.createFromBitmap(renderScript, bitmapOut);
        yuvToRGB.setInput(allocationIn);

    }


    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata imageMetaData = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(frame.getRotation())
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(data, imageMetaData);
        return image;
    }


}


