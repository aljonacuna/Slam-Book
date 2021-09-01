package com.example.slambookproject.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import org.json.JSONObject;

import java.util.ArrayList;

public class ChangeSecretQuestion extends AppCompatActivity implements OnClickListener {
    private ImageView closebtn,savebtn;
    private TextInputLayout passTIP,ansTIP,unameTIP;
    private TextInputEditText passET,ansET,unameEt;
    private validateUser userValidation;
    private ProgressDialog progressDialog;
    private Spinner spinner;
    String sqStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_secret_pass);
        progressDialog = new ProgressDialog(this);
        unameEt = findViewById(R.id.uname_changeSQET);
        unameTIP = findViewById(R.id.uname_changeSQTIP);
        passTIP = findViewById(R.id.pass_changeSQTIP);
        passET = findViewById(R.id.pass_changeSQET);
        spinner = findViewById(R.id.change_SQTIP);
        ansTIP = findViewById(R.id.ans_TIP);
        ansET = findViewById(R.id.ans_ET);
        savebtn = findViewById(R.id.savebtn_changeSQ);
        closebtn = findViewById(R.id.closebtn_changeSQ);
        savebtn.setOnClickListener(this);
        closebtn.setOnClickListener(this);
        userValidation = new validateUser();
        sqList();
    }

    @Override
    public void onClick(View view) {
        if (view == savebtn){
            String uname = unameTIP.getEditText().getText().toString();
            String pass = passTIP.getEditText().getText().toString();
            String ans = ansTIP.getEditText().getText().toString().trim();
            try {
                saveSQ(uname,pass,ans);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (view == closebtn){
            cancel();
        }
    }

    public void sqList(){
        ArrayList<String>questions = new ArrayList<>();
        questions.add(0,"Choose your secret question");
        questions.add("What is your favourite number");
        questions.add("Who is your groupmates");
        questions.add("What is your favourite color");
        questions.add("What is your favourite song");
        questions.add("What is your middlename");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.ddmenu,questions);
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

    public boolean isPass(String pass){
        if (TextUtils.isEmpty(pass)){
            passTIP.setError("Password is required");
            passTIP.requestFocus();
            return false;
        } else {
            passTIP.setErrorEnabled(false);
            return true;
        }
    }

    public boolean isSqandAns(String ans){
        if (sqStr.equals("Choose your secret question")){
            Toast.makeText(this, "What is your secret question?", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(sqStr)){
            Toast.makeText(this, "What is your secret question?", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(ans)){
            ansTIP.setError("Answer is empty");
            ansTIP.requestFocus();
            return false;
        }else {
            return true;
        }
    }
    public boolean isUname(String uname){
        if (TextUtils.isEmpty(uname)){
            unameTIP.setError("Username is empty");
            unameTIP.requestFocus();
            return false;
        }else if (!uname.equals(username_save.uname)){
            Toast.makeText(this, "Incorrect username!", Toast.LENGTH_SHORT).show();
            unameTIP.requestFocus();
            return false;
        }
        else{
            unameTIP.setErrorEnabled(false);
            return true;
        }
    }
    private void cancel(){
        DialogUtils.dialog(this, "Cancel",
                "Are you sure you'd like to cancel on changing your secret question?", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtils.dialog.dismiss();
                        finish();

                    }
                });
    }
    public void saveSQ(final String uname, final String pass, final String ans) throws JSONException{
        if (!isUname(uname) || !isPass(pass) || !isSqandAns(ans)){
            return;
        }else{
            userValidation.ValidateUser(this, uname, pass, "", "", "login", new validateUser.checkUser() {
                @Override
                public void onCallback(boolean isValid) throws JSONException {
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialogloading);
                    if (isValid){
                        String url = urlClass.local_host+"users/update_user.php";
                        RequestQueue requestQueue = Volley.newRequestQueue(ChangeSecretQuestion.this);
                        requestQueue.start();
                        JSONObject obj = new JSONObject();
                        obj.put("uname",uname);
                        obj.put("password",pass);
                        obj.put("sq",sqStr);
                        obj.put("ans",ans);
                        obj.put("tag","sq");
                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String message = response.getString("message");
                                    Toast.makeText(ChangeSecretQuestion.this, "Successfully change secret question!", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("LogError",error.toString());
                                progressDialog.dismiss();
                            }
                        });
                        requestQueue.add(objectRequest);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(ChangeSecretQuestion.this, "Incorrect username or password!", Toast.LENGTH_LONG).show();
                        unameEt.setText("");
                        passET.setText("");
                        ansET.setText("");
                        unameTIP.requestFocus();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        cancel();
    }
}