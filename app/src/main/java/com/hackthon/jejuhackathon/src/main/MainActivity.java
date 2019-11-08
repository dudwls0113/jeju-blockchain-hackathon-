package com.hackthon.jejuhackathon.src.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.BaseActivity;
import com.hackthon.jejuhackathon.src.InsuActivity;
import com.hackthon.jejuhackathon.src.Map2Activity;
import com.hackthon.jejuhackathon.src.VideoActivity;


public class MainActivity extends BaseActivity {
    ImageView mMenuHamburger;
    DrawerLayout mDrawerLayout;
    View mDrawerView;

    RadioButton mNoInsuRadio, mBasicInsuRadio, mPremiumInsuRadio;

    TextView mBtnGoToRide;

    boolean isHamburgerOpen = false;
    int mInsuType = 100;
    long backPressedTime = 0;
    int FINISH_INTERVAL_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerView = findViewById(R.id.drawer);

        mNoInsuRadio = findViewById(R.id.noInsuRadio);
        mBasicInsuRadio = findViewById(R.id.basicInsuRadio);
        mPremiumInsuRadio = findViewById(R.id.premiumInsuRadio);

        mBtnGoToRide = findViewById(R.id.btnGoToRide);
        mBtnGoToRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInsuType == 100) {
                    showCustomToast("보험 여부 선택해주세요");
                } else {
                    Intent intent = new Intent(MainActivity.this, Map2Activity.class);
                    intent.putExtra("insuType", mInsuType);
                    startActivity(intent);
                }
            }
        });

        mMenuHamburger = findViewById(R.id.menuHamburger);
        mMenuHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHamburgerOpen) {
                    mDrawerLayout.closeDrawers();
                    isHamburgerOpen = false;
                } else {
                    mDrawerLayout.openDrawer(mDrawerView);
                    isHamburgerOpen = true;
                }
            }
        });

        mDrawerLayout.addDrawerListener(listener);
        mDrawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.videoBtnLayout:
                Intent intent = new Intent(this, VideoActivity.class);
                startActivity(intent);
                break;
            case R.id.insuBtnLayout:
                Intent intent1 = new Intent(this, InsuActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(mDrawerView);
        } else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
                showCustomToast("뒤로가기를 한번 더 누르면 앱을 종료합니다");
            }

        }

    }

    public void onClickRadioBtn(View view) {
        unCheckRadioBtn();
        switch (view.getId()) {
            case R.id.noInsuRadio:
                mNoInsuRadio.setChecked(true);
                mInsuType = 0;
                break;
            case R.id.basicInsuRadio:
                mBasicInsuRadio.setChecked(true);
                mInsuType = 1;
                break;
            case R.id.premiumInsuRadio:
                mPremiumInsuRadio.setChecked(true);
                mInsuType = 2;
                break;
        }
    }

    public void unCheckRadioBtn() {
        mNoInsuRadio.setChecked(false);
        mBasicInsuRadio.setChecked(false);
        mPremiumInsuRadio.setChecked(false);
    }
}
