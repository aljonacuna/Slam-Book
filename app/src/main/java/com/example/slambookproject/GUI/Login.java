package com.example.slambookproject.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.slambookproject.DBhandler.DBHelper;
import com.example.slambookproject.R;
import com.example.slambookproject.Utils.DialogUtils;
import com.example.slambookproject.Utils.urlClass;
import com.example.slambookproject.Utils.username_save;
import com.example.slambookproject.Utils.validateUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout pass, uname;
    private TextInputEditText passEt,unameEt;
    private Button login;
    private DBHelper myDB;
    private TextView forgotpass;
    private validateUser validUser;
    private ProgressDialog progressDialog;
    private TextView signupbtn;
    username_save unamesave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        pass = findViewById(R.id.loginpass);
        login = findViewById(R.id.loginbtn);
        uname = findViewById(R.id.loginuname);
        unameEt = findViewById(R.id.unameEt_login);
        passEt= findViewById(R.id.passEt_login);
        signupbtn = findViewById(R.id.signup_btn_login);
        forgotpass = findViewById(R.id.forgotpassword);
        unamesave = new username_save();
        validUser = new validateUser();
        myDB = new DBHelper(this);
        forgotpass.setOnClickListener(this);
        login.setOnClickListener(this);
        signupbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == login) {
            String passTxt = pass.getEditText().getText().toString();
            String unameTxt = uname.getEditText().getText().toString();
            checkPass(passTxt,unameTxt);
        } else if (view == forgotpass) {
            Intent intent = new Intent(this, ForgotPassword.class);
            startActivity(intent);
        } else if (view == signupbtn){
            Intent intent = new Intent(this,Signup.class);
            startActivity(intent);
            finish();
        }
    }
    public boolean isUname(String unameTxt){
        if (TextUtils.isEmpty(unameTxt)){
            unameEt.setText("");
            uname.setError("Username is empty");
            uname.requestFocus();
            return false;
        }else{
            uname.setErrorEnabled(false);
            return true;
        }
    }
    public boolean isPass(String passTxt){
        if (TextUtils.isEmpty(passTxt)){
            passEt.setText("");
            pass.setError("Password is empty");
            pass.requestFocus();
            return false;
        }else{
            pass.setErrorEnabled(false);
            return true;
        }
    }
    public void checkPass(final String passTxt,final String unameTxt) {
        if (!isUname(unameTxt) || !isPass(passTxt)) {
            return;
        }else{
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialoglogin);

            validUser.ValidateUser(this, unameTxt, passTxt,"","", "login", new validateUser.checkUser() {
                @Override
                public void onCallback(boolean isValid) throws JSONException {
                    if (isValid){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Login.this,MainScreen.class);
                                startActivity(intent);
                                unamesave.uname = unameTxt;
                                finish();
                                progressDialog.dismiss();
                            }
                        },1000);
                    }else{
                        Toast.makeText(Login.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                        uname.requestFocus();
                        unameEt.setText("");
                        passEt.setText("");
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DialogUtils.dialog(this, "Exit", "Do you really want to exit?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.dialog.dismiss();
                finishAffinity();
            }
        });
    }
}