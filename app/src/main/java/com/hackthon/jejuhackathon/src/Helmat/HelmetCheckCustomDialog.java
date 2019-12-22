package com.hackthon.jejuhackathon.src.Helmat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.Tensor.DetectorActivity;


public class HelmetCheckCustomDialog extends Dialog {

    ImageView mImageViewClose;
    Button mBtnReTry, mBtnSkip;
    Context mContext;
    TextView mTextViewTitle;

    private DeleteDialogListener deleteDialogListener;

    public HelmetCheckCustomDialog(Context context, final DeleteDialogListener dialogListener) {
        super(context);
        mContext = context;
        this.deleteDialogListener = dialogListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        setContentView(R.layout.custom_helmet_check);     //다이얼로그에서 사용할 레이아웃입니다.

        mBtnReTry = findViewById(R.id.dialog_delete_btn_retry);
        mBtnSkip = findViewById(R.id.dialog_delete_btn_skip);
//        mTextViewTitle = findViewById(R.id.dialog_delete_tv_title);

//        mTextViewTitle.setText(title);

        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("클릭", "aaa");
                try {
                    Intent intent = new Intent(mContext, DetectorActivity.class);
                    mContext.startActivity(intent);
                    dismiss();
                }
                catch (Exception e){
                    Log.d("클릭", e.toString());
                }
            }
        });
        mBtnReTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialogListener.clickReTryBtn();

                dismiss();
            }
        });

//        mImageViewClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
//            }
//        });

    }

    public interface DeleteDialogListener {
        void clickReTryBtn();
        void clickSkipBtn();

    }

}
