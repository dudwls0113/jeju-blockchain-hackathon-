//package com.hackthon.jejuhackathon.src.Helmat;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Rational;
//import android.util.Size;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import androidx.camera.core.CameraX;
//import androidx.camera.core.ImageCapture;
//import androidx.camera.core.ImageCaptureConfig;
//import androidx.camera.core.Preview;
//import androidx.camera.core.PreviewConfig;
//import androidx.lifecycle.LifecycleOwner;
//
//
//import com.hackthon.jejuhackathon.R;
//import com.hackthon.jejuhackathon.src.BaseActivity;
//
//import org.json.JSONException;
//
//import java.io.File;
//
//public class SelfieActivity extends BaseActivity {
//    private static final String TAG = "SELFIE";
//    private Context mContext;
//    private ImageButton mCloseIbtn, mActionIbtn, mSubActionIbtn;
//
//    private ImageView mTakenIv;
//    private TextureView mCameraTextureView;
//    private Preview mPreview;
//    private ImageCapture mImageCapture;
//
//    private Uri mImgUri;
//
//    private Boolean mSaveMode = false;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera_ex);
//        initViews();
//        startCamera();
//    }
//
//
//    public void initViews() {
//        mContext = getApplicationContext();
//        mActionIbtn = findViewById(R.id.selfie_action_ibtn);// btn of take a picture and save image
//
//        mCameraTextureView = findViewById(R.id.selfie_camera_ttv);
//        mTakenIv = findViewById(R.id.selfie_taken_iv);
//
////        mCloseIbtn.setOnClickListener(this);
////        mActionIbtn.setOnClickListener(this);
////        mSubActionIbtn.setOnClickListener(this);
//    }
//
//    private void startCamera() {
//        CameraX.unbindAll();
//
//        Rational aspectRatio = new Rational(1, 1);
//        Size screen = new Size(mCameraTextureView.getWidth(), mCameraTextureView.getHeight());
//
//        PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen)
//                .setLensFacing(CameraX.LensFacing.FRONT).build();
//        mPreview = new Preview(previewConfig);
//
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
//        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
//                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).setLensFacing(CameraX.LensFacing.FRONT)
//                .setTargetAspectRatio(aspectRatio).build();
//        mImageCapture = new ImageCapture(imageCaptureConfig);
//
//        CameraX.bindToLifecycle((LifecycleOwner) this, mPreview, mImageCapture);
//    }
//
//    public void customOnClick(View view) throws JSONException {
//        switch (view.getId()) {
//            case R.id.selfie_action_ibtn:
//                if (!mSaveMode)
//                    controlPicture();
//                else {
////                    uploadImage(mImgUri);
//                }
//                break;
////            case R.id.selfie_close_ibtn:
////                finish();
////                break;
////            case R.id.selfie_sub_action_ibtn:
////                if (mSaveMode)//take a picture mode
////                    refreshPicture();
////                break;
//
//        }
//    }
//
//    private void updateTransform() {
//        Matrix mx = new Matrix();
//        float w = mCameraTextureView.getMeasuredWidth();
//        float h = mCameraTextureView.getMeasuredHeight();
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
//                rotateMatrix.postRotate(270);
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
//            public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
//                String msg = "Failed: " + message;
//                showCustomToast(msg);
//                if (cause != null)
//                    cause.printStackTrace();
//            }
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
//        mSubActionIbtn.setVisibility(mSaveMode ? View.VISIBLE : View.GONE);
//    }
//
//
////    private void uploadImage(Uri imgUri) {
////        final SelfieService selfieService = new SelfieService(this);
////        selfieService.uploadFileToFireBase(imgUri);
////    }
////
////    private void upLoadUrl(String url) throws JSONException {
////        final SelfieService selfieService = new SelfieService(this);
////        selfieService.upLoadUrl(url, 4);
////    }
//
//
//    private void refreshPicture() {
//        changeMode();
//        mTakenIv.setImageResource(android.R.color.transparent);
//    }
//
////    @Override
////    public void kycSucess(String message) {
////        Intent intent = new Intent(SelfieActivity.this, AllStepActivity.class);
////        startActivity(intent);
////        finish();
////    }
////
////    @Override
////    public void validateFailure(String message) {
////        showCustomToast(message);
////    }
////
////    @Override
////    public void upLoadDone(Uri uri) {
////        mImgUri = uri;
////        try {
////            upLoadUrl(String.valueOf(mImgUri));
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////    }
//}
