package com.hackthon.jejuhackathon.src;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BillActivity extends BaseActivity {
    TextView mDateText, mRideMoney, mInsuText, mSaleText, mSumText, mMyKlayText, mNewKlayText;
    EditText mKlayEdit;
    ImageView mPayBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        mDateText = findViewById(R.id.billDateText);
        mRideMoney = findViewById(R.id.billRideMoneyText);
        mInsuText = findViewById(R.id.billInsuText);
        mSaleText = findViewById(R.id.billSaleText);
        mSumText = findViewById(R.id.billSumText);
        mMyKlayText = findViewById(R.id.billMyKlay);
        mNewKlayText = findViewById(R.id.billNewKlay);

        mKlayEdit = findViewById(R.id.billKlayEdit);
        mPayBtn = findViewById(R.id.payBtn);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy. MM. dd");
        String formatDate = sdfNow.format(date);
        mDateText.setText(formatDate);

        int ride = 1920;
        int insu = 500;

        mKlayEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int sum = 2000;
                mSumText.setText(sum+"원");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int sum = ride + insu - Integer.parseInt(mKlayEdit.getText().toString())*30;
                mSumText.setText(sum + "원");
                mSaleText.setText(Integer.parseInt(mKlayEdit.getText().toString())*30+"원");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payBtn:
                Intent intent = new Intent(BillActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
    }
}
