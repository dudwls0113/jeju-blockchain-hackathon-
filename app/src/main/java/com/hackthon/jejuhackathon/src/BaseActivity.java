package com.hackthon.jejuhackathon.src;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.bumptech.glide.Glide;
import com.hackthon.jejuhackathon.R;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    public AppCompatDialog mProgressDialog;

    public void showCustomToast(final String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void showProgressDialog(Activity activity) {

        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            progressSET(message);
        } else {

            mProgressDialog = new AppCompatDialog(activity);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.setContentView(R.layout.custom_dialog_loading);
            mProgressDialog.show();

        }


        final ImageView img_loading_frame = mProgressDialog.findViewById(R.id.iv_frame_loading);
        Glide.with(this).asGif().load(R.raw.preloader_circleround).into(img_loading_frame);

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    static public boolean isHelmet = true;

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
