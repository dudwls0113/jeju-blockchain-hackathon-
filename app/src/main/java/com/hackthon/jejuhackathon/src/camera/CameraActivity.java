package com.hackthon.jejuhackathon.src.camera;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hackthon.jejuhackathon.R;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import ai.fritz.core.Fritz;
import ai.fritz.core.FritzManagedModel;
import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.objectdetectionmodelfast.ObjectDetectionOnDeviceModel;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.PredictorStatusListener;
import ai.fritz.vision.objectdetection.FritzVisionObjectPredictor;
import ai.fritz.vision.objectdetection.FritzVisionObjectResult;
import ai.fritz.vision.objectdetection.ObjectDetectionManagedModel;

public class CameraActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_camera);
        Fritz.configure(this, "d6a754dde5c3471eb289ca96dcdecce4");

        cameraView = findViewById(R.id.cameraView);

        cameraView.setLifecycleOwner(this); //Automatically handles the camera lifecycle
        init();
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
        onDeviceModel = new ObjectDetectionOnDeviceModel();
        objectPredictor = FritzVision.ObjectDetection.getPredictor(onDeviceModel);



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
                                             Log.d("아아", frame.getData()+"");

                                             allocationIn.copyFrom(frame.getData());
                                             yuvToRGB.forEach(allocationOut);
                                             allocationOut.copyTo(bitmapOut);
                                             fritzVisionImage = FritzVisionImage.fromBitmap(bitmapOut);
                                             FritzVisionObjectResult objectResult = objectPredictor.predict(fritzVisionImage);

                                             Log.d("아아", frame.getData()+"");

                                             for(int i=0; i< objectResult.getVisionObjectsByClass("person").size(); i++){
                                                 Log.d("아아", objectResult.getObjects().get(i).getVisionLabel()+"");
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

}
