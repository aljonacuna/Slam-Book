package com.example.slambookproject.GUI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.slambookproject.DBhandler.DBHelper;
import com.example.slambookproject.Model.Entrymodel;
import com.example.slambookproject.R;
import com.example.slambookproject.Utils.DialogUtils;
import com.example.slambookproject.Utils.createImgFile;
import com.example.slambookproject.Utils.permissionUtils;
import com.example.slambookproject.Utils.urlClass;
import com.example.slambookproject.Utils.username_save;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AddEntry extends AppCompatActivity implements View.OnClickListener {
    private EditText ans1, ans2, ans3, ans4, ans5;
    private RelativeLayout addimg;
    private TextView lbl;
    private TextInputLayout fname, lname;
    private ImageView imguser, savebtn, addimgicon,closebtn;
    private Uri uri;
    private Bitmap bitmap;
    private CardView cv;
    public static final int REQUEST_Gallery = 2;
    public static final int REQUEST_Camera = 1;
    private DialogUtils pick_img;
    private createImgFile create_imgfile;
    File photofile;
    Uri photoUri;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        progressDialog = new ProgressDialog(this);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        ans4 = findViewById(R.id.ans4);
        ans5 = findViewById(R.id.ans5);
        addimg = findViewById(R.id.addimg);
        closebtn = findViewById(R.id.closebtnaddentry);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        cv = findViewById(R.id.cv_circleimgadd);
        imguser = findViewById(R.id.imguser);
        savebtn = findViewById(R.id.savebtn);
        addimgicon = findViewById(R.id.addimageicon);
        lbl = findViewById(R.id.lbladdimg);
        addimg.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        closebtn.setOnClickListener(this);
        pick_img = new DialogUtils();
        create_imgfile = new createImgFile();
    }

    @Override
    public void onClick(View view) {
        if (view == addimg) {
            pick_img.imgDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        photofile = null;
                        try {
                            photofile = create_imgfile.createfile(AddEntry.this);
                        } catch (IOException e) {

                        }
                        if (photofile != null) {
                            photoUri = FileProvider.getUriForFile(AddEntry.this,
                                    "com.example.slambookproject.provider", photofile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, REQUEST_Camera);
                            pick_img.img_dialog.dismiss();
                        }
                    }
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_Gallery);
                    pick_img.img_dialog.dismiss();
                }
            });

        } else if (view == savebtn) {
            String fnameTxt = fname.getEditText().getText().toString();
            String lnameTxt = lname.getEditText().getText().toString();
            String ans1Txt = ans1.getText().toString().trim();
            String ans2Txt = ans2.getText().toString().trim();
            String ans3Txt = ans3.getText().toString().trim();
            String ans4Txt = ans4.getText().toString().trim();
            String ans5Txt = ans5.getText().toString().trim();
            try {
                saveEntry(fnameTxt, lnameTxt, ans1Txt, ans2Txt, ans3Txt, ans4Txt, ans5Txt);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        } else if (view == closebtn){
            cancel();
        }
    }

    private void cancel(){
        DialogUtils.dialog(this, "Cancel",
                "Are you sure you'd like to discard your entry?", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtils.dialog.dismiss();
                        Intent intent = new Intent(AddEntry.this,MainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
    private boolean isName(String fnameText, String lnameText) {
        if (TextUtils.isEmpty(fnameText)) {
            Toast.makeText(this, "First name is required!", Toast.LENGTH_SHORT).show();
            fname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(lnameText)) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show();
            lname.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isAns(String ans1Text, String ans2Text, String ans3Text, String ans4Text, String ans5Text) {
        if (TextUtils.isEmpty(ans1Text)) {
            Toast.makeText(this, "Please answer all the questions", Toast.LENGTH_SHORT).show();
            ans1.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(ans2Text)) {
            Toast.makeText(this, "Please answer all the questions", Toast.LENGTH_SHORT).show();
            ans2.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(ans3Text)) {
            Toast.makeText(this, "Please answer all the questions", Toast.LENGTH_SHORT).show();
            ans3.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(ans4Text)) {
            Toast.makeText(this, "Please answer all the questions", Toast.LENGTH_SHORT).show();
            ans4.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(ans5Text)) {
            Toast.makeText(this, "Please answer all the questions", Toast.LENGTH_SHORT).show();
            ans5.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_Camera) {
                cv.setVisibility(View.VISIBLE);
                addimgicon.setVisibility(View.GONE);
                lbl.setVisibility(View.GONE);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                imguser.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_Gallery) {
                if (data != null) {
                    cv.setVisibility(View.VISIBLE);
                    addimgicon.setVisibility(View.GONE);
                    lbl.setVisibility(View.GONE);
                    uri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imguser.setImageBitmap(bitmap);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEntry(final String fnameTxt, final String lnameTxt, final String ans1Txt,
                          final String ans2Txt, final String ans3Txt, final String ans4Txt, final String ans5Txt) throws JSONException, IOException {
        if (!isName(fnameTxt, lnameTxt) || !isAns(ans1Txt, ans2Txt, ans3Txt, ans4Txt, ans5Txt)) {
            return;
        } else {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialogloading);
            if (uri != null || photoUri != null) {
                String url = urlClass.local_host + "entry/add_entry.php";
                if (uri == null) {
                    bitmap = new Compressor(this)
                            .setMaxWidth(800)
                            .setMaxHeight(800)
                            .setQuality(50)
                            .compressToBitmap(photofile);
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                final byte[] toImgbyte = baos.toByteArray();
                final String imgStr = Base64.encodeToString(toImgbyte, Base64.DEFAULT);
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("uname", username_save.uname);
                jsonObject.put("image", imgStr);
                jsonObject.put("fname", fnameTxt);
                jsonObject.put("lname", lnameTxt);
                jsonObject.put("ans1", ans1Txt);
                jsonObject.put("ans2", ans2Txt);
                jsonObject.put("ans3", ans3Txt);
                jsonObject.put("ans4", ans4Txt);
                jsonObject.put("ans5", ans5Txt);
                RequestQueue requestQueue = Volley.newRequestQueue(AddEntry.this);
                requestQueue.start();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject
                        , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //retrieve response message returned by API
                            String message = response.getString("message");
                            Toast.makeText(AddEntry.this, "Entry successfully added", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddEntry.this,MainScreen.class);
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley error: ", error.toString());
                        progressDialog.dismiss();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 3000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 3000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {

                    }
                });
                requestQueue.add(jsonObjectRequest);
            }else{
                progressDialog.dismiss();
                Toast.makeText(this, "Please insert image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        cancel();
    }
}