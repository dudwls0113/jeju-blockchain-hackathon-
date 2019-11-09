package com.hackthon.jejuhackathon.src.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.BaseActivity;
import com.hackthon.jejuhackathon.src.main.MainActivity;
import com.hackthon.jejuhackathon.src.signup.SignUpActivity;

public class LogInActivity extends BaseActivity {

    EditText mIdEdit, mPwEdit;
    TextView mLogInBtn, mSignUpBtn;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mIdEdit = findViewById(R.id.idEditTextLogIn);
        mPwEdit = findViewById(R.id.pwEditTextLogIn);

        mLogInBtn = findViewById(R.id.logInBtn);
        mSignUpBtn = findViewById(R.id.goToSignUp);

        mSignUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        mLogInBtn.setOnClickListener(v -> {
            sp = getSharedPreferences("user", MODE_PRIVATE);
            String userId = sp.getString("userId", "");
            String userPw = sp.getString("userPw", "");

            if (mIdEdit.getText().toString().equals("") || mIdEdit.getText().toString().equals(null) || mPwEdit.getText().toString().equals("") || mPwEdit.getText().toString().equals(null)) {
                showCustomToast("아이디와 비밀번호를 입력해주세요!");
            } else if (userId.equals(mIdEdit.getText().toString()) && userPw.equals(mPwEdit.getText().toString())) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                showCustomToast("알맞은 아이디와 비밀번호를 입력해주세요!");
            }

        });
    }
}
