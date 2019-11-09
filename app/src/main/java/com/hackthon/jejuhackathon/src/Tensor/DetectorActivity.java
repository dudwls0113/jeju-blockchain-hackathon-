/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hackthon.jejuhackathon.src.Tensor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Location;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.BillActivity;
import com.hackthon.jejuhackathon.src.Tensor.customview.OverlayView;
import com.hackthon.jejuhackathon.src.Tensor.env.BorderedText;
import com.hackthon.jejuhackathon.src.Tensor.env.ImageUtils;
import com.hackthon.jejuhackathon.src.Tensor.env.Logger;
import com.hackthon.jejuhackathon.src.Tensor.tflite.Classifier;
import com.hackthon.jejuhackathon.src.Tensor.tflite.TFLiteObjectDetectionAPIModel;
import com.hackthon.jejuhackathon.src.Tensor.tracking.MultiBoxTracker;
import com.hackthon.jejuhackathon.src.map.Map2Activity;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolygon;
import com.skt.Tmap.TMapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.hackthon.jejuhackathon.src.BaseActivity.isHelmet;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener, TMapGpsManager.onLocationChangedCallback {
    private static final Logger LOGGER = new Logger();

    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;

    private Classifier detector;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private BorderedText borderedText;

    //////////////////////////////////////////

    TMapView mTmapView;
    private Context mContext;
    private boolean mTrackingMode = true;
    private TMapGpsManager mTmapGps = null;

    CoordinatorLayout mConstraintLayoutCamera;


    boolean isRecMode = false;

    TextView mTextViewRec;
    ////////////////////////////////////////

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Log.d("에러", e.toString());
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }

    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;
                        }

                        final List<Classifier.Recognition> mappedRecognitions =
                                new LinkedList<Classifier.Recognition>();

                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence) {
                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);

                                result.setLocation(location);
                                mappedRecognitions.add(result);
                            }
                        }

                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        computingDetection = false;

                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showFrameInfo(previewWidth + "x" + previewHeight);
                                        showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                                        showInference(lastProcessingTimeMs + "ms");
                                    }
                                });
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.not_move_activity, R.anim.not_move_activity);
    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(() -> detector.setUseNNAPI(isChecked));
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_map2);
        mTextViewRec = findViewById(R.id.recBtn);

        mTextViewRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecMode) {
                    mConstraintLayoutCamera.setVisibility(View.INVISIBLE);
                    mTmapView.setVisibility(View.VISIBLE);
                    isRecMode = false;
                    Log.d("클릭", "ㅇㅇ");
                } else {
                    mConstraintLayoutCamera.setVisibility(View.VISIBLE);
                    mTmapView.setVisibility(View.INVISIBLE);
                    isRecMode = true;
                    Log.d("클릭", "ddd");
                }
            }
        });
        mContext = this;
        mConstraintLayoutCamera = findViewById(R.id.camera_layout);
        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        mTmapView = new TMapView(this);

        mTmapView.setSKTMapApiKey("220db9a1-476e-4e2e-8691-794c9b0cd38e");
        linearLayoutTmap.addView(mTmapView);

        mTmapView.setCompassMode(true);
        mTmapView.setIconVisibility(true);

        mTmapView.setZoomLevel(15);
        mTmapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mTmapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        mTmapGps = new TMapGpsManager(DetectorActivity.this);
        mTmapGps.setMinTime(1000);
        mTmapGps.setMinDistance(5);
        mTmapGps.setProvider(mTmapGps.NETWORK_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        mTmapGps.OpenGps();

        mTmapView.setTrackingMode(true);
        mTmapView.setSightVisible(true);
//        TMapPoint tMapPoint = new TMapPoint(37.570841, 126.985302);
//
//        TMapCircle tMapCircle = new TMapCircle();
//        tMapCircle.setCenterPoint( tMapPoint );
//        tMapCircle.setRadius(300);
//        tMapCircle.setCircleWidth(2);
//        tMapCircle.setLineColor(Color.BLUE);
//        tMapCircle.setAreaColor(Color.GRAY);
//        tMapCircle.setAreaAlpha(100);
//        tMapView.addTMapCircle("circle1", tMapCircle);

        ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();
        alTMapPoint.add(new TMapPoint(33.514578, 126.529531));
        alTMapPoint.add(new TMapPoint(33.514674, 126.530126));
        alTMapPoint.add(new TMapPoint(33.514459, 126.530105));
        alTMapPoint.add(new TMapPoint(33.514468, 126.529547));

        TMapPolygon tMapPolygon = new TMapPolygon();
        tMapPolygon.setLineColor(Color.rgb(247, 181, 0));
        tMapPolygon.setPolygonWidth(2);
        tMapPolygon.setAreaColor(Color.rgb(247, 181, 0));
        tMapPolygon.setAreaAlpha(100);
        for (int i = 0; i < alTMapPoint.size(); i++) {
            tMapPolygon.addPolygonPoint(alTMapPoint.get(i));
        }

        mTmapView.addTMapPolygon("Line1", tMapPolygon);

        ArrayList<TMapPoint> alTMapPoint2 = new ArrayList<TMapPoint>();
        alTMapPoint2.add(new TMapPoint(33.514808, 126.526544));
        alTMapPoint2.add(new TMapPoint(33.514806, 126.526740));
        alTMapPoint2.add(new TMapPoint(33.514446, 126.526689));
        alTMapPoint2.add(new TMapPoint(33.514466, 126.526536));

        TMapPolygon tMapPolygon2 = new TMapPolygon();
        tMapPolygon2.setLineColor(Color.rgb(247, 181, 0));
        tMapPolygon2.setPolygonWidth(2);
        tMapPolygon2.setAreaColor(Color.rgb(247, 181, 0));
        tMapPolygon2.setAreaAlpha(100);
        for (int i = 0; i < alTMapPoint2.size(); i++) {
            tMapPolygon2.addPolygonPoint(alTMapPoint2.get(i));
        }

        mTmapView.addTMapPolygon("Line2", tMapPolygon2);

        ArrayList<TMapPoint> alTMapPoint3 = new ArrayList<TMapPoint>();
        alTMapPoint3.add(new TMapPoint(33.513805, 126.528364));
        alTMapPoint3.add(new TMapPoint(33.513670, 126.528400));
        alTMapPoint3.add(new TMapPoint(33.513750, 126.528690));
        alTMapPoint3.add(new TMapPoint(33.513956, 126.528513));

        TMapPolygon tMapPolygon3 = new TMapPolygon();
        tMapPolygon3.setLineColor(Color.rgb(247, 181, 0));
        tMapPolygon3.setPolygonWidth(2);
        tMapPolygon3.setAreaColor(Color.rgb(247, 181, 0));
        tMapPolygon3.setAreaAlpha(100);
        for (int i = 0; i < alTMapPoint3.size(); i++) {
            tMapPolygon3.addPolygonPoint(alTMapPoint3.get(i));
        }

        mTmapView.addTMapPolygon("Line3", tMapPolygon3);

        ArrayList<TMapPoint> alTMapPoint4 = new ArrayList<TMapPoint>();
        alTMapPoint4.add(new TMapPoint(33.514792, 126.528223));
        alTMapPoint4.add(new TMapPoint(33.514779, 126.528432));
        alTMapPoint4.add(new TMapPoint(33.514913, 126.528523));
        alTMapPoint4.add(new TMapPoint(33.514904, 126.528266));

        TMapPolygon tMapPolygon4 = new TMapPolygon();
        tMapPolygon4.setLineColor(Color.rgb(247, 181, 0));
        tMapPolygon4.setPolygonWidth(2);
        tMapPolygon4.setAreaColor(Color.rgb(247, 181, 0));
        tMapPolygon4.setAreaAlpha(100);
        for (int i = 0; i < alTMapPoint4.size(); i++) {
            tMapPolygon4.addPolygonPoint(alTMapPoint4.get(i));
        }

        mTmapView.addTMapPolygon("Line4", tMapPolygon4);

//        mTmapView.setCenterPoint(126.985302, 37.570841);
    }


    public void customOnClick(View view) {
        switch (view.getId()) {
            case R.id.finishRidingBtn:
                Intent intent = new Intent(mContext, BillActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onLocationChange(Location location) {
        if (mTrackingMode) {
            mTmapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
}
