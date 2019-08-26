package com.octane.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octane.app.API.API;
import com.octane.app.API.APIClient;
import com.octane.app.GlobalConstant;
import com.octane.app.Model.ResponseModel;
import com.octane.app.Preference.SharedPref;
import com.octane.app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity implements View.OnClickListener{

    EditText edtEmail;
    EditText edtPwd;
    TextView btnLogin;
    TextView btnRegister;
    ProgressBar progressBar;
    Vibrator v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPwd = (EditText)findViewById(R.id.edtPwd);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        btnLogin = (TextView)findViewById(R.id.btnLogin);
        btnRegister = (TextView)findViewById(R.id.register);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void validateUserData(){
        final String email = edtEmail.getText().toString();
        final String password = edtPwd.getText().toString();

        if(!GlobalConstant.isValidEmail(email)){
            edtEmail.setError("Please enter your email");
            edtEmail.requestFocus();
            v.vibrate(100);
            return;
        }
        if(TextUtils.isEmpty(password)){
            edtPwd.setError("Please enter your password");
            edtPwd.requestFocus();
            v.vibrate(100);
            return;
        }
        loginUser(email,password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                Bundle bundle = data.getBundleExtra("result");
                edtEmail.setText(bundle.getString("email"));
                edtPwd.setText(bundle.getString("pwd"));
                edtEmail.setError(null);
                edtPwd.setError(null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void loginUser(final String email, final String password){
        edtEmail.setEnabled(false);
        edtPwd.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);
        API api = APIClient.getClient().create(API.class);
        Call<ResponseModel> login = api.login(email,password);
        login.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if(response.body() == null)
                    return;
                if(response.body().getIsSuccess() == 1){
                    //get username
                    //startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                    Toast.makeText(LoginActivity.this,"Login Success",Toast.LENGTH_LONG).show();
                    //storing the user in shared preferences
                    SharedPref.getInstance(LoginActivity.this).storeUserInfo(email,password);
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    edtEmail.setEnabled(true);
                    edtPwd.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    btnRegister.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnLogin:
                validateUserData();
                break;
            case R.id.register:
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivityForResult(intent,1);
                break;
        }
    }
}
