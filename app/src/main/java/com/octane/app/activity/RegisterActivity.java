package com.octane.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octane.app.API.API;
import com.octane.app.API.APIClient;
import com.octane.app.GlobalConstant;
import com.octane.app.Model.ResponseModel;
import com.octane.app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends Activity implements View.OnClickListener{

    TextView btnRegister;
    ImageView btnBack;
    EditText edtFN;
    EditText edtLN;
    EditText edtEmail;
    EditText edtCarModel;
    EditText edtPwd;
    EditText edtConfirm;
    Vibrator v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.img_bk_signup);

        v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        setContentView(R.layout.activity_register);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        edtFN = findViewById(R.id.edtFN);
        edtLN = findViewById(R.id.edtLN);
        edtEmail = findViewById(R.id.edtEmail);
        edtCarModel = findViewById(R.id.edtCarModel);
        edtPwd = findViewById(R.id.edtPwd);
        edtConfirm = findViewById(R.id.edtConfirm);

        btnRegister.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
    private void validateData(){
        String firstName = edtFN.getText().toString();
        String lastName = edtLN.getText().toString();
        String email = edtEmail.getText().toString();
        String city = ((EditText)findViewById(R.id.edtCity)).getText().toString();
        String state = ((EditText)findViewById(R.id.edtState)).getText().toString();
        String country = ((EditText)findViewById(R.id.edtCountry)).getText().toString();
        String phone = ((EditText)findViewById(R.id.edtPhone)).getText().toString();
        String carModel = edtCarModel.getText().toString();
        String pwd = edtPwd.getText().toString();
        String confirmPwd = edtConfirm.getText().toString();
        if(TextUtils.isEmpty(firstName)){
            edtFN.setError("Please enter your First Name");
            edtFN.requestFocus();
            v.vibrate(100);
            return;
        }
        if(TextUtils.isEmpty(lastName)){
            edtLN.setError("Please enter your Last Name");
            edtLN.requestFocus();
            v.vibrate(100);
            return;
        }
        if(!GlobalConstant.isValidEmail(email)){
            edtEmail.setError("Please enter your correct Email");
            edtEmail.requestFocus();
            v.vibrate(100);
            return;
        }
        if(TextUtils.isEmpty(carModel)){
            edtCarModel.setError("Please enter your car model");
            edtCarModel.requestFocus();
            v.vibrate(100);
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            edtPwd.setError("Please enter password");
            edtPwd.requestFocus();
            v.vibrate(100);
            return;
        }
        if(TextUtils.isEmpty(confirmPwd)){
            edtConfirm.setError("Please enter confirm password");
            edtConfirm.requestFocus();
            v.vibrate(100);
            return;
        }
        if(!pwd.equals(confirmPwd)){
            edtPwd.setError("Password is incorrect");
            edtPwd.requestFocus();
            v.vibrate(100);
            return;
        }
        registerUser(firstName,lastName,email,city,state,country,phone,carModel,pwd);
    }
    private void registerUser(String fn, String ln, final String email, String city, String state, String country, String phone, String carModel, final String pwd){
        btnRegister.setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        API api = APIClient.getClient().create(API.class);
        Call<ResponseModel> login = api.register(fn,ln,email,city,state,country,phone,carModel,pwd);
        login.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if(response.body() == null)
                    return;
                if(response.body().getIsSuccess() == 1){
                    Toast.makeText(RegisterActivity.this,"Register success",Toast.LENGTH_LONG).show();
                    Intent returnIntent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("email",email);
                    bundle.putString("pwd",pwd);
                    returnIntent.putExtra("result",bundle);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    btnRegister.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegister:
                validateData();
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }
}
