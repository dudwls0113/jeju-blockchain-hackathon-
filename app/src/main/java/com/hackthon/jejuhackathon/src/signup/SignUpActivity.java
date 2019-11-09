package com.hackthon.jejuhackathon.src.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.BaseActivity;
import com.hackthon.jejuhackathon.src.login.LogInActivity;

public class SignUpActivity extends BaseActivity {
    EditText mIdEdit, mPwEdit, mNameEdit, mKlaytnIdEdit;
    TextView mSignUpBtn;
    public SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sp = getSharedPreferences("user", MODE_PRIVATE);

        mIdEdit = findViewById(R.id.idEditTextSignUp);
        mPwEdit = findViewById(R.id.pwEditTextSignUp);
        mNameEdit = findViewById(R.id.nameEditTextSignUp);
        mKlaytnIdEdit = findViewById(R.id.klaytnIdEditText);

        mSignUpBtn = findViewById(R.id.signUpBtn);

        mSignUpBtn.setOnClickListener(v -> {
            if (mIdEdit.getText().toString().equals("") || mPwEdit.getText().toString().equals("") || mNameEdit.getText().toString().equals("") || mKlaytnIdEdit.getText().toString().equals("")) {
                showCustomToast("빈칸을 채워주세요.");
            } else {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("userId", mIdEdit.getText().toString());
                editor.putString("userPw", mPwEdit.getText().toString());
                editor.putString("userName", mNameEdit.getText().toString());
                editor.putString("klaytnId", mKlaytnIdEdit.getText().toString());
                editor.commit();
                showCustomToast("회원가입에 성공하였습니다.");
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
            }

        });
    }
}
