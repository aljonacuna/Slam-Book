package com.example.slambookproject.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.slambookproject.DBhandler.DBHelper;
import com.example.slambookproject.R;
import com.example.slambookproject.Utils.DialogUtils;
import com.example.slambookproject.Utils.pass_encode_arr_for_spinner;
import com.example.slambookproject.Utils.urlClass;
import com.example.slambookproject.Utils.validateUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.slambookproject.GUI.SplashScreen.Filename;
import static com.example.slambookproject.GUI.SplashScreen.passTag;

public class Signup extends AppCompatActivity implements OnClickListener {
    private TextInputLayout pass, answer, secretquest, confpass, uname;
    private TextInputEditText passEt, confEt, saEt, unameEt;
    private AutoCompleteTextView secretquestDD;
    private Button signup;
    private Spinner spinner;
    private TextView backtologin;
    private ArrayList<String> questions;
    private SharedPreferences preferences;
    validateUser userValidation;
    private ProgressDialog progressDialog;
    String sqStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_signup);
        pass = findViewById(R.id.pass);
        unameEt = findViewById(R.id.unameEt_signup);
        saEt = findViewById(R.id.sa_et);
        backtologin = findViewById(R.id.backtosignin);
        confEt = findViewById(R.id.confpassEt);
        passEt = findViewById(R.id.passEt);
        answer = findViewById(R.id.answer);
        spinner = findViewById(R.id.secretquestDD);
        confpass = findViewById(R.id.confpass);
        uname = findViewById(R.id.uname);
        signup = findViewById(R.id.confirmsignup);
        userValidation = new validateUser();
        preferences = getSharedPreferences(Filename, Context.MODE_PRIVATE);
        signup.setOnClickListener(this);
        backtologin.setOnClickListener(this);
        DDquestions();
    }

    @Override
    public void onClick(View view) {
        if (view == signup) {
            String passTxt = pass.getEditText().getText().toString();
            String answerTxt = answer.getEditText().getText().toString();
            String confpassTxt = confpass.getEditText().getText().toString();
            String unameTxt = uname.getEditText().getText().toString();
            try {
                signUp(passTxt, confpassTxt, answerTxt, unameTxt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (view == backtologin){
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            finish();
        }
    }


    public void DDquestions() {
        questions = new ArrayList<>();
        questions.add(0, "Choose your secret question");
        questions.add("What is your favourite number");
        questions.add("Who is your groupmates");
        questions.add("What is your favourite color");
        questions.add("What is your favourite song");
        questions.add("What is your middlename");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.ddmenu, questions);
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
            uname.setError("Username is required");
            uname.requestFocus();
            return false;
        }else if (unameTxt.length()<6){
            uname.setError("Username must contains atleast 6 characters");
            uname.requestFocus();
            return false;
        }
        else {
            uname.setErrorEnabled(false);
            return true;
        }
    }

    public boolean isPass(String passTxt, String confpassTxt) {
        if (TextUtils.isEmpty(passTxt)) {
            pass.setError("Password is empty");
            passEt.setText("");
            pass.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confpassTxt)) {
            confpass.setError("Confirm password is required");
            passEt.setText("");
            pass.requestFocus();
            return false;
        } else if (!passTxt.equals(confpassTxt)) {
            pass.setError("Password do not match");
            confEt.setText("");
            passEt.setText("");
            pass.requestFocus();
            return false;
        }else if (passTxt.length()<6){
            pass.setError("Password must contain atleast 6 characters");
            pass.requestFocus();
            return false;
        }
        else {
            pass.setErrorEnabled(false);
            confpass.setErrorEnabled(false);
            return true;
        }
    }

    public boolean isSecretQuestion() {
        if (sqStr.equals("Choose your secret question")) {
            Toast.makeText(this, "Please choose your secret question!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean isAnswer(String answerTxt) {
        if (TextUtils.isEmpty(answerTxt)) {
            answer.setError("Please answer the question");
            answer.requestFocus();
            return false;
        } else {
            answer.setErrorEnabled(false);
            return true;
        }
    }

    public void signUp(final String passTxt, String confpassTxt, final String answer, final String unameTxt) throws JSONException {
        if (!isUname(unameTxt) || !isPass(passTxt, confpassTxt) || !isSecretQuestion()
                || !isAnswer(answer)) {
            return;
        } else {
            validateUser.ValidateUser(this, unameTxt, passTxt, "", "", "register", new validateUser.checkUser() {
                @Override
                public void onCallback(boolean isValid) throws JSONException {
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialogregister);
                    if (isValid) {
                        String insert = "http://192.168.0.111/api/users/add_users.php";
                        RequestQueue rq = Volley.newRequestQueue(Signup.this);
                        rq.start();
                        String passText = pass_encode_arr_for_spinner.encodePass(passTxt);
                        final JSONObject account = new JSONObject();
                        account.put("uname", unameTxt);
                        account.put("password", passText);
                        account.put("sq", sqStr);
                        account.put("ans", answer);
                        JsonObjectRequest jsRequest = new JsonObjectRequest(Request.Method.POST, insert, account, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(Signup.this, "Signup successfully", Toast.LENGTH_LONG).show();
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(passTag, "pass");
                                    editor.commit();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(Signup.this,Login.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(Signup.this, "Error in processing the request: " + error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                        rq.add(jsRequest);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Signup.this, "User already exist!", Toast.LENGTH_LONG).show();
                        passEt.setText("");
                        confEt.setText("");
                        saEt.setText("");
                        unameEt.setText("");
                        uname.requestFocus();
                    }
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        DialogUtils.dialog(this, "Exit", "Are you sure you'd like to cancel registration?", new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.dialog.dismiss();
                finishAffinity();
            }
        });
    }
}

