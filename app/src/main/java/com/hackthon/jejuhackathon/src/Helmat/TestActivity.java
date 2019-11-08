package com.hackthon.jejuhackathon.src.Helmat;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.BaseActivity;
import com.hackthon.jejuhackathon.src.main.interfaces.MainActivityView;

public class TestActivity extends BaseActivity {
    private TextView mTvHelloWorld;
    MyCameraPreview myCameraPreview;
    FrameLayout mFrame;
    int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mFrame = findViewById(R.id.cameraPreview);

//         상태바를 안보이도록 합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startCamera();
    }

    public void customOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                myCameraPreview.takePicture();
                break;
            default:
                break;
        }
    }

    void startCamera() {

        Log.e("Camera", "startCamera");

        // Create our Preview view and set it as the content of our activity.
        myCameraPreview = new MyCameraPreview(this, CAMERA_FACING);

        mFrame.addView(myCameraPreview, 0);

    }
}
