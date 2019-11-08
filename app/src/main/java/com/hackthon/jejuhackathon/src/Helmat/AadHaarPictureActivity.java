//package com.hackthon.jejuhackathon.src.Helmat;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.util.Rational;
//import android.util.Size;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.camera.core.CameraInfoUnavailableException;
//import androidx.camera.core.CameraX;
//import androidx.camera.core.FlashMode;
//import androidx.camera.core.ImageCapture;
//import androidx.camera.core.ImageCaptureConfig;
//import androidx.camera.core.Preview;
//import androidx.camera.core.PreviewConfig;
//import androidx.lifecycle.LifecycleOwner;
//
//import com.hackthon.jejuhackathon.R;
//import com.hackthon.jejuhackathon.src.BaseActivity;
//
//import org.json.JSONException;
//
//import java.io.File;
//
//
//public class AadHaarPictureActivity extends BaseActivity {
//    private static final String TAG = "AADHAAR";
//    private String mType = null;
//    private Context mContext;
//    private TextView mTypeTv, mTitleTv, mGuideTv;
//    private ImageButton mCloseIbtn, mActionIbtn, mSubActionIbtn;
//
//    private ImageView mTakenIv;
//    private TextureView mCameraTextureView;
//    private Preview mPreview;
//    private ImageCapture mImageCapture;
//    CameraX.LensFacing lensFacing = CameraX.LensFacing.BACK;
//    private Uri mImgUri;
//    FrameLayout mFrameLayout;
//    private Boolean mSaveMode = false, isFrontUpload = false, isBackUpload = false;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_aadhaar_picture);
//
//        // 상태바를 안보이도록 합니다.
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        initViews();
//        try {
//            startCamera();
//        } catch (CameraInfoUnavailableException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        saveProgress();
//    }
//
//    public void initViews() {
//        mContext = getApplicationContext();
//        mActionIbtn = findViewById(R.id.selfie_action_ibtn);// btn of take a picture and save image
//        mSubActionIbtn = findViewById(R.id.aadhaar_picture_sub_action_ibtn);// btn of light and refresh
//        mFrameLayout = findViewById(R.id.activity_camera_frame);
//        mCameraTextureView = findViewById(R.id.selfie_camera_ttv);
//        mTakenIv = findViewById(R.id.selfie_taken_iv);
//
////        mCloseIbtn.setOnClickListener(this);
////        mActionIbtn.setOnClickListener(this);
////        mSubActionIbtn.setOnClickListener(this);
//    }
//
//    public void customOnClick(View view) throws JSONException {
//        switch (view.getId()) {
//            case R.id.selfie_action_ibtn:
//                if (!mSaveMode)
//                    controlPicture();
//                else {
//                    uploadImage(mImgUri);
//                }
//                break;
//
//            case R.id.aadhaar_picture_sub_action_ibtn:
//                if (!mSaveMode) //take a picture mode
//                    controlFlash();
//                else
//                    refreshPicture();
//                break;
//        }
//    }
//
////    private void initType() {
////        if (mType.equals("FRONT")) {
////            mTypeTv.setText(R.string.aadhaar_front);
////            mTitleTv.setText(R.string.aadhaar_picture_front_side);
////            mGuideTv.setText(R.string.aadhaar_picture_front_side_guide);
////        } else if (mType.equals("BACK")) {
////            mTypeTv.setText(R.string.aadhaar_back);
////            mTitleTv.setText(R.string.aadhaar_picture_back_side);
////            mGuideTv.setText(R.string.aadhaar_picture_back_side_guide);
////        }
////    }
//
//
////    @SuppressLint("RestrictedApi")
////    private void startCamera() {
////        lensFacing = CameraX.LensFacing.FRONT;
////        try {
////            CameraX.getCameraWithLensFacing(lensFacing);
////        } catch (CameraInfoUnavailableException e) {
////            e.printStackTrace();
////        }
////
////        CameraX.unbindAll();
////
////        Rational aspectRatio = new Rational(1, 500);
////        Size screen = new Size(mCameraTextureView.getWidth(), mCameraTextureView.getHeight());
////
////        PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
////        mPreview = new Preview(previewConfig);
////        mPreview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
////            @Override
////            public void onUpdated(Preview.PreviewOutput output) {
////                ViewGroup parent = (ViewGroup) mCameraTextureView.getParent();
////                parent.removeView(mCameraTextureView);
////                parent.addView(mCameraTextureView, 0);
////
////                mCameraTextureView.setSurfaceTexture(output.getSurfaceTexture());
////                updateTransform();
////            }
////        });
////
////        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
////                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
////                .setTargetAspectRatio(aspectRatio).build();
////        mImageCapture = new ImageCapture(imageCaptureConfig);
////        CameraX.bindToLifecycle((LifecycleOwner) this, mPreview, mImageCapture);
////
//////        lensFacing = lensFacing == CameraX.LensFacing.FRONT ? CameraX.LensFacing.BACK : CameraX.LensFacing.FRONT;
//////        try {
//////            // Only bind use cases if we can query a camera with this orientation
//////            bindCameraUseCases();
//////        } catch (CameraInfoUnavailableException e) {
//////            // Do nothing
//////        }
////
////    }
//
//    @SuppressLint("RestrictedApi")
//    private void startCamera() throws CameraInfoUnavailableException {
//        lensFacing = CameraX.LensFacing.FRONT;
//        CameraX.getCameraWithLensFacing(lensFacing);
//
//        bindCameraUseCases();
//
//    }
//
//    private void bindCameraUseCases() {
//        // Make sure that there are no other use cases bound to CameraX
//        CameraX.unbindAll();
//
//        Rational aspectRatio = new Rational(1, 1);
//        Size screen = new Size(mFrameLayout.getWidth(), mFrameLayout.getHeight());
//
//        PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
////        previewConfig.set(CameraX.LensFacing.FRONT);
//        mPreview = new Preview(previewConfig);
//        mPreview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
//            @Override
//            public void onUpdated(Preview.PreviewOutput output) {
//                ViewGroup parent = (ViewGroup) mCameraTextureView.getParent();
//                parent.removeView(mCameraTextureView);
//                parent.addView(mCameraTextureView, 0);
//
//                mCameraTextureView.setSurfaceTexture(output.getSurfaceTexture());
//                updateTransform();
//            }
//        });
//
//        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
//                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
////                .setLensFacing(lensFacing)
//                .setLensFacing(CameraX.LensFacing.FRONT)
////                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
//                .setTargetAspectRatio(aspectRatio)
//                .build()
//                ;
//        mImageCapture = new ImageCapture(imageCaptureConfig);
//
////        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
////                .setLensFacing(lensFacing)
////                .build();
////        imageCapture = new ImageCapture(imageCaptureConfig);
//
//        // Apply declared configs to CameraX using the same lifecycle owner
////        CameraX.bindToLifecycle(this, preview, imageCapture);
//
//        CameraX.bindToLifecycle( this, mPreview , mImageCapture);
//    }
//
//
////    private void bindCameraUseCases() {
////        // Make sure that there are no other use cases bound to CameraX
////        CameraX.unbindAll();
////
////        PreviewConfig previewConfig = new PreviewConfig.Builder().
////                setLensFacing(lensFacing)
////                .build();
////        Preview preview = new Preview(previewConfig);
////
////        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
////                .setLensFacing(lensFacing)
////                .build();
////        imageCapture = new ImageCapture(imageCaptureConfig);
////
////        // Apply declared configs to CameraX using the same lifecycle owner
////        CameraX.bindToLifecycle(this, preview, imageCapture);
////    }
//
//
//    private void updateTransform() {
//        Matrix mx = new Matrix();
//        float w = mFrameLayout.getMeasuredWidth();
//        float h = mFrameLayout.getMeasuredHeight();
//
//        float cX = w / 2f;
//        float cY = h / 2f;
//
//        int rotatinDegree;
//        int rotation = (int) mCameraTextureView.getRotation();
//
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                rotatinDegree = 0;
//                break;
//            case Surface.ROTATION_90:
//                rotatinDegree = 90;
//                break;
//            case Surface.ROTATION_180:
//                rotatinDegree = 180;
//                break;
//            case Surface.ROTATION_270:
//                rotatinDegree = 270;
//                break;
//            default:
//                return;
//        }
//
//        mx.postRotate((float) rotatinDegree, cX, cY);
//        mCameraTextureView.setTransform(mx);
//    }
//
//    private void controlPicture() {
//        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".png");
//
//        mImageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
//            @Override
//            public void onImageSaved(@NonNull File file) {
//                /*String msg = "Saved: " + file.getAbsolutePath();
//                showCustomToast(msg);*/
//                Matrix rotateMatrix = new Matrix();
//                rotateMatrix.postRotate(90);
//
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                Bitmap sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
//                        bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false);
//
//                mTakenIv.setImageBitmap(sideInversionImg); //이미지 회전하고 rendering
//                mImgUri = Uri.fromFile(file); // file to uri
//                //Log.i(TAG, ""+mImgUri);
//                changeMode();//모드 체인지
//            }
//
//            @Override
//            public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
//
//            }
//
////            @Override
////            public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
////                String msg = "Failed: " + message;
////                showCustomToast(msg);
////                if (cause != null)
////                    cause.printStackTrace();
////            }
//        });
//    }
//
//
//    private void changeMode() { //업로드모드랑 사진찍기 모드 변환
//        if (!mSaveMode)
//            mSaveMode = true;
//        else
//            mSaveMode = false;
//
////        mActionIbtn.setImageDrawable(mSaveMode ? getDrawable(R.drawable.aadhaar_picture_save) : getDrawable(R.drawable.take_a_picture_btn_img));
////        mSubActionIbtn.setImageDrawable(mSaveMode ? getDrawable(R.drawable.aadhaar_picture_refresh) : getDrawable(R.drawable.aadhaar_light_off));
//    }
//
//    private void controlFlash() {
//        if (mImageCapture.getFlashMode() == FlashMode.OFF) {
////            mSubActionIbtn.setImageDrawable(getDrawable(R.drawable.aadhaar_light_on));
//            mImageCapture.setFlashMode(FlashMode.ON);
//        } else {
////            mSubActionIbtn.setImageDrawable(getDrawable(R.drawable.aadhaar_light_off));
//            mImageCapture.setFlashMode(FlashMode.OFF);
//        }
//    }
//
//    private void uploadImage(Uri imgUri) {
////        final AadHaarService aadHaarService = new AadHaarService(this);
////        aadHaarService.uploadFileToFireBase(imgUri, mType);
//    }
//
//    private void upLoadUrl(String url) throws JSONException {
////        final AadHaarService aadHaarService = new AadHaarService(this);
////        Log.i(TAG, sSharedPreferences.getString(X_ACCESS_TOKEN, ""));
////        if(mType.equals("FRONT"))
////            aadHaarService.upLoadUrl(url, 1);
////        else
////            aadHaarService.upLoadUrl(url, 2);
//    }
//
//    private void saveProgress() {
////        final SharedPreferences.Editor editor = sSharedPreferences.edit();
////
////        if (mType.equals("FRONT") && isFrontUpload)
////            editor.putString("front", "33"); //사진찍기에 성공했으면 진행률 증가
////        else if(mType.equals("BACK") && isBackUpload)
////            editor.putString("back", "33");
////
////        editor.apply();
//    }
//
//    private void refreshPicture() {
//        changeMode();
//        mTakenIv.setImageResource(android.R.color.transparent);
//    }
//
////    @Override
////    public void validateSuccess(String message) {}
////
////    @Override
////    public void upLoadUrlSuccess(String message) {}
////
////    @Override
////    public void validateFailure(String message) {
////        showCustomToast(message);
////    }
////
////    @Override
////    public void upLoadDone(Uri url) {
////        if(mType.equals("FRONT"))
////            isFrontUpload = true;
////        else isBackUpload = true;
////
////        try {
////            upLoadUrl(String.valueOf(mImgUri));
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////        FireBase Image등록 후 바로 저장된 url 받아와 서버에 등록
//
////        finish();
//}
