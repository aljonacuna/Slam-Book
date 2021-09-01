package com.example.slambookproject.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.example.slambookproject.Utils.pass_encode_arr_for_spinner;
import com.example.slambookproject.Utils.urlClass;
import com.example.slambookproject.Utils.username_save;
import com.example.slambookproject.Utils.validateUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private ImageView closebtn,savebtn;
    private TextInputEditText unameET,newpassET,confpassET,oldpassET;
    private TextInputLayout unameTIP,newpassTIP,confpassTIP,oldpassTIP;
    private validateUser userValidation;
    private ProgressDialog progressDialog;
    username_save unamesave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        progressDialog = new ProgressDialog(this);
        unameTIP = findViewById(R.id.uname_changepassTIP);
        newpassTIP = findViewById(R.id.newpass_TIP);
        confpassTIP = findViewById(R.id.confpass_TIP);
        unameET = findViewById(R.id.uname_changepassET);
        newpassET = findViewById(R.id.newpass_ET);
        confpassET = findViewById(R.id.confpass_ET);
        oldpassTIP = findViewById(R.id.oldpass_TIP);
        oldpassET = findViewById(R.id.oldpass_ET);
        savebtn = findViewById(R.id.savebtn_changepass);
        closebtn = findViewById(R.id.closebtn_changepass);
        unamesave = new username_save();
        savebtn.setOnClickListener(this);
        closebtn.setOnClickListener(this);
        userValidation = new validateUser();
    }

    @Override
    public void onClick(View view) {
        if (view == savebtn){
            String uname = unameET.getText().toString();
            String newpass = newpassET.getText().toString();
            String confpass = confpassET.getText().toString();
            String oldpass = oldpassET.getText().toString();
            try {
                savePass(uname,newpass,confpass,oldpass);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (view == closebtn){
            cancel();
        }
    }

    private void cancel(){
        DialogUtils.dialog(this, "Cancel",
                "Are you sure you'd like to cancel on changing your password?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.dialog.dismiss();
                finish();
            }
        });
    }

    public boolean isPassword(String oldpass,String newpass,String confpass){
        if (TextUtils.isEmpty(oldpass)){
            oldpassTIP.setError("Current password is required");
            oldpassTIP.requestFocus();
            return false;
        }else if (TextUtils.isEmpty(newpass)){
            newpassTIP.setError("New password is required");
            newpassTIP.requestFocus();
            oldpassTIP.setErrorEnabled(false);
            return false;
        }else if (!newpass.equals(confpass)){
            confpassTIP.setError("Password do not match");
            confpassET.setText("");
            unameET.setText("");
            oldpassET.setText("");
            newpassTIP.setErrorEnabled(false);
            unameTIP.requestFocus();
            return false;
        }else{
            confpassTIP.setErrorEnabled(false);
            return true;
        }
    }
    public boolean isUname(String uname){
        if (TextUtils.isEmpty(uname)){
            unameTIP.setError("Username is required");
            unameTIP.requestFocus();
            return false;
        }else if (!unamesave.uname.equals(uname)){
            Toast.makeText(this, "Incorrect username", Toast.LENGTH_LONG).show();
            unameTIP.requestFocus();
            return false;
        }
        else{
            unameTIP.setErrorEnabled(false);
            return true;
        }
    }

    public void savePass(final String uname, final String newpass, String confpass, final String oldpass) throws JSONException {
        if (!isPassword(oldpass,newpass,confpass) || !isUname(uname)){
            return;
        }else{
            userValidation.ValidateUser(this, uname, oldpass, "", "", "login", new validateUser.checkUser() {
                @Override
                public void onCallback(boolean isValid) throws JSONException {
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialogloading);
                    if (isValid){
                        String url = urlClass.local_host+"users/update_user.php";
                        RequestQueue requestQueue = Volley.newRequestQueue(ChangePassword.this);
                        requestQueue.start();
                        JSONObject object = new JSONObject();
                        String pass = pass_encode_arr_for_spinner.encodePass(newpass);
                        object.put("uname",uname);
                        object.put("password",oldpass);
                        object.put("newpass",pass);
                        object.put("tag","pass");
                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String message = response.getString("message");
                                    Toast.makeText(ChangePassword.this, "Server Response "+message, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Log.e("LogError",error.toString());
                            }
                        });
                        requestQueue.add(objectRequest);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(ChangePassword.this, "Incorrect username or password!", Toast.LENGTH_LONG).show();
                        unameET.setText("");
                        oldpassET.setText("");
                        newpassET.setText("");
                        confpassET.setText("");
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