/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hackthon.jejuhackathon.src.Helmat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.ExifInterface;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageContext;
import com.google.api.services.vision.v1.model.ImageSource;
import com.google.api.services.vision.v1.model.TextAnnotation;


import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.BaseActivity;
import com.hackthon.jejuhackathon.src.Map2Activity;
import com.hackthon.jejuhackathon.src.main.MainActivity;
import com.hackthon.jejuhackathon.src.ride.RideActivity;
import com.hackthon.jejuhackathon.src.utils.PackageManagerUtils;


import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okio.ByteString;


public class HelmetActivity extends BaseActivity {

    private Context mContext;
    String url;
    String mGender;
    Intent mIntent;
    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2; //앨범에서 사진 가져오기
    private static final int CROP_FROM_CAMERA = 3; //가져온 사진을 자르기 위한 변수

    private static final int BEFORE_IMAGE = 4; //가져온 사진을 자르기 위한 변수
    private static final int AFTER_IMAGE = 5; //가져온 사진을 자르기 위한 변수
    private static final int AFTER_SEVER_UPLOAD = 6; //가져온 사진을 자르기 위한 변수

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수
    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수
    private Uri photoUri;
    private ImageView mImageViewThumbnail, mImageViewButton, mImageViewDone;
    private EditText mTextViewName, mTextViewCode, mTextViewDept;
    private int mMode = BEFORE_IMAGE;

    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    private static final int MAX_DIMENSION = 1200;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDWgCoxrW3F93kneyob_0zFWvy6JngwM00";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private Button mButtonMan, mButtonWoman;

    private boolean genderCheck = false;

    private TextView mTextViewResult;

    MyCameraPreview myCameraPreview;
    FrameLayout mFrame;

    int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(ExifInterface.ORIENTATION_NORMAL, 0);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_90, 90);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_180, 180);
        ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_270, 270);
    }

    private ImageView mImageViewHelmet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helmet);
        mContext = this;
        checkPermissions();
        init();
    }


    void init() {

        mFrame = findViewById(R.id.cameraPreview);

//         상태바를 안보이도록 합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startCamera();

    }

    void startCamera() {

        // Create our Preview view and set it as the content of our activity.
        myCameraPreview = new MyCameraPreview(this, CAMERA_FACING);

        mFrame.addView(myCameraPreview, 0);

    }

    public boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showCustomToast(mContext.getString(R.string.school_certification_permission));
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showCustomToast(mContext.getString(R.string.school_certification_permission));
                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showCustomToast(mContext.getString(R.string.school_certification_permission));
                            }
                        }
                    }
                } else {
                    showCustomToast(mContext.getString(R.string.school_certification_permission));
                }
                return;
            }
        }
    }

//
//    public File getCameraFile() {
//        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return new File(dir, FILE_NAME);
//    }
//
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//        String imageFileName = "IP" + timeStamp + "_";
//        File storageDir = new File(Environment.getExternalStorageDirectory() + "/jeju/"); //test라는 경로에 이미지를 저장하기 위함
//        if (!storageDir.exists()) {
//            storageDir.mkdirs();
//        }
//        File image = File.createTempFile(
//                imageFileName,
//                ".jpg",
//                storageDir
//        );
//        return image;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) {
//            Toast.makeText(HelmetActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//        }
//        if (requestCode == PICK_FROM_ALBUM) {
//            if (data == null) {
//                return;
//            }
//            photoUri = data.getData();
//            cropImage();
//        } else if (requestCode == PICK_FROM_CAMERA) {
//            cropImage();
//            MediaScannerConnection.scanFile(HelmetActivity.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
//                    new String[]{photoUri.getPath()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                        }
//                    });
//        } else if (requestCode == CROP_FROM_CAMERA) {
//            try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.
//
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
//                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 800, 500);
//                ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축
//
//
//                mImageViewHelmet.setImageBitmap(thumbImage);
////                Glide.with(mContext).load(R.drawable.activity_helmet_image).into(mImageViewHelmet);
//                mMode = AFTER_IMAGE;
//            } catch (Exception e) {
//            }
//            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
////            uploadImage(photoUri);
//            uploadImage(data.getData());
//        }
//    }


    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

//                bitmap = rotate(bitmap, 90); //샘플이미지파일


                callCloudVision(bitmap);
//                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "다시 시도해주세요", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "다시 시도해주세요", Toast.LENGTH_LONG).show();
        }
//        showProgressDialog(this);

    }

    public void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
//        mImageDetails.setText(R.string.loading_message);
        //로딩넣으면될듯

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    public static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("");

//        List<TextAnnotation> labels = response.getResponses().get(0).getFullTextAnnotation();

//        final TextAnnotation text = response.getResponses().get(0).getLabelAnnotations();
//
////        if (labels != null) {
////            for (EntityAnnotation label : labels) {
////                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
////                message.append("\n");
////            }
////        } else {
////            message.append("nothing");
////        }
//
//        if (text != null) {
//            message.append(text.getText());
//        }


        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("nothing");
        }

        return message.toString();

    }


    public class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<HelmetActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(HelmetActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            Log.d("result", result);
            showCustomToast(result);
            hideProgressDialog();
            if(result.contains("Helmet")){

            }
            else{
                HelmetCheckCustomDialog helmetCheckCustomDialog = new HelmetCheckCustomDialog(mContext, new HelmetCheckCustomDialog.DeleteDialogListener() {
                    @Override
                    public void clickYesBtn() {

                    }

                    @Override
                    public void clickNoBtn() {
                        Intent intent = new Intent(mContext, Map2Activity.class);
                        startActivity(intent);
                    }
                });
                helmetCheckCustomDialog.show();
            }
            HelmetActivity activity = mActivityWeakReference.get();
        }

    }

    public Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(10);
                add(labelDetection);
            }});
            annotateImageRequest.setImageContext(new ImageContext());

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

//
//    private Bitmap rotate(Bitmap bitmap, float degree) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }


//    private void goToAlbum() {
//
//        Intent intent = new Intent(Intent.ACTION_PICK); //ACTION_PICK 즉 사진을 고르겠다!
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, PICK_FROM_ALBUM);
//    }
//
//
//    public void cropImage() {
//        this.grantUriPermission("com.android.camera", photoUri,
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(photoUri, "image/*");
//
//        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
//        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        int size = list.size();
//        if (size == 0) {
//            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 2);
//            intent.putExtra("scale", true);
//            File croppedFileName = null;
//            try {
//                croppedFileName = createImageFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            File folder = new File(Environment.getExternalStorageDirectory() + "/inha/");
//            File tempFile = new File(folder.toString(), croppedFileName.getName());
//
//            photoUri = FileProvider.getUriForFile(HelmetActivity.this,
//                    "com.hackthon.jejuhackathon.provider", tempFile);
//
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//
//            intent.putExtra("return-data", false);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행
//
//            Intent i = new Intent(intent);
//            ResolveInfo res = list.get(0);
//            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            grantUriPermission(res.activityInfo.packageName, photoUri,
//                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            startActivityForResult(i, CROP_FROM_CAMERA);
//
//        }
//
//    }


    public void customOnClick(View view)  {
        switch (view.getId()) {
            case R.id.btn_camera:
                showProgressDialog(this);
                myCameraPreview.takePicture();
                break;
            default:
                break;

        }

    }

//    void uploadFileToFireBase(Uri mImageUri) throws JSONException {
////        final LoadingDialog loadingDialog = new LoadingDialog(mContext);
////        loadingDialog.show();
////        progressDialog.setTitle("업로드중...");
////        progressDialog.show();
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        Date now = new Date();
//        final StorageReference storageRef = storage.getReferenceFromUrl("gs://inha-taxi.appspot.com/").child("user/" + mImageUri.getLastPathSegment());
//        UploadTask uploadTask = storageRef.putFile(mImageUri);
//        storageRef.putFile(mImageUri)
//                //성공시
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                        progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
//                    }
//                })
//                //실패시
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        loadingDialog.dismiss();
//                        showCustomToast("업로드 실패");
//                    }
//                })
//                //진행중
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
////                        @SuppressWarnings("VisibleForTests")
////                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
////                        progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
//                    }
//                });
//
//        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                return storageRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
////                    mProfileImageUrl = downloadUri.toString();
//
//
//                    Glide.with(mContext)
//                            .load(downloadUri.toString())
//                            .fitCenter()
////                            .placeholder(R.drawable.placeholder)
////                            .error(R.drawable.imagenotfound)
//                            .into(mImageViewThumbnail);
////                    mImageViewThumbnail
//
//                    url = downloadUri.toString();
//                    //Log.d("로그", url);
//
//                    loadingDialog.dismiss();
////                    mTextViewTitle.setText("이미지 첨부가\n완료되었습니다.");
////                    mTextViewContent.setText("빠른 시일 내에 처리하겠습니다.");
//
////                    Glide.with(mContext).load(R.drawable.btn_yellow_ok).into(mImageViewButton);
//
//                    mMode = AFTER_SEVER_UPLOAD;//                    try {
//////                        postPofileImage();
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                    showCustomToast(mContext, "업로드!");
//                } else {
//                    //실패시
//                }
//            }
//        });
//    }


}