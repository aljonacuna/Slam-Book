package com.example.slambookproject.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
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
import com.example.slambookproject.Utils.validateUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout answer, uname;
    private TextInputEditText ansEt, unameEt;
    private Button confbtn;
    private ProgressDialog progressDialog;
    private validateUser validUser;
    private Spinner spinner;
    String sqStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        progressDialog = new ProgressDialog(this);
        answer = findViewById(R.id.forgotP_answer);
        ansEt = findViewById(R.id.forgotP_answerEt);
        uname = findViewById(R.id.forgotP_uname);
        unameEt = findViewById(R.id.forgotP_unameEt);
        spinner = findViewById(R.id.forgotP_quest);
        confbtn = findViewById(R.id.confbtn_forgotp);
        confbtn.setOnClickListener(this);
        questions();
    }

    @Override
    public void onClick(View view) {
        if (view == confbtn) {
            String ans = answer.getEditText().getText().toString();
            String unametxt = uname.getEditText().getText().toString();
            displayPass(ans, unametxt);
        }
    }

    public void questions() {
        ArrayList<String> questList = new ArrayList<String>();
        questList.add(0, "What is your question?");
        questList.add("What is your favourite number");
        questList.add("Who is your groupmates");
        questList.add("What is your favourite color");
        questList.add("What is your favourite song");
        questList.add("What is your middlename");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.ddmenu, questList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sqStr = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public boolean isUname(String unameTxt) {
        if (TextUtils.isEmpty(unameTxt)) {
            unameEt.setText("");
            uname.setError("Username is required");
            uname.requestFocus();
            return false;
        } else {
            uname.setErrorEnabled(false);
            return true;
        }
    }

    public boolean isSecretQuestion() {
        if (sqStr.equals("Choose your secret question")) {
            Toast.makeText(this, "Choose secret question!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean isAnswer(String answerTxt) {
        if (TextUtils.isEmpty(answerTxt)) {
            answer.setError("Please answer the question");
            ansEt.setText("");
            answer.requestFocus();
            return false;
        } else {
            answer.setErrorEnabled(false);
            return true;
        }
    }

    public void displayPass(final String ans,final String unametxt) {
        if (!isUname(unametxt) || !isSecretQuestion() || !isAnswer(ans)) {
            return;
        } else {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialogloading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            validUser.ValidateUser(this, unametxt, "", sqStr, ans, "forgot", new validateUser.checkUser() {
                @Override
                public void onCallback(boolean isValid) throws JSONException {
                    if (isValid) {
                        progressDialog.dismiss();
                    } else {
                        ansEt.setText("");
                        unameEt.setText("");
                        unameEt.requestFocus();
                        Toast.makeText(ForgotPassword.this, "Please enter correct information!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DialogUtils.dialog(this, "Cancel", "Are you sure you'd like to cancel this operation?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.dialog.dismiss();
                finish();
            }
        });
    }
}