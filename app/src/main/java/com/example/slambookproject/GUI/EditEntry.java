package com.example.slambookproject.GUI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.slambookproject.DBhandler.DBHelper;
import com.example.slambookproject.Model.Entrymodel;
import com.example.slambookproject.R;
import com.example.slambookproject.Utils.DialogUtils;
import com.example.slambookproject.Utils.createImgFile;
import com.example.slambookproject.Utils.getEntryimg;
import com.example.slambookproject.Utils.urlClass;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class EditEntry extends AppCompatActivity implements View.OnClickListener {
    private EditText ans1, ans2, ans3, ans4, ans5;
    private RelativeLayout replaceimg;
    private TextInputEditText fname_et, lname_et;
    private TextInputLayout fname, lname;
    private ImageView imguser, savebtn, closebtn;
    private Uri uri;
    private Uri cameraUri;
    private Bitmap mybitmap = null;
    public static final int REQUEST_Gallery = 2;
    public static final int REQUEST_Camera = 1;
    private ProgressDialog progressDialog;
    //from edittext
    String fnameText, lnameText, a1Text, a2Text, a3Text, a4Text, a5Text;
    //from intent
    String a1txt, a2txt, a3txt, a4txt, a5txt, fnametxt, lnametxt, imgStr;
    int id;

    getEntryimg getImg;
    private DialogUtils pick_img;
    private createImgFile create_imgfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        init();
    }

    private void cancel() {
        DialogUtils.dialog(this, "Cancel",
                "Are you sure you'd like to cancel on updating this entry?", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtils.dialog.dismiss();
                        backData();
                    }
                });
    }
    private void backData(){
        Intent intent = new Intent(this, ViewEntry.class);
        intent.putExtra("id", id);
        intent.putExtra("fname", fnametxt);
        intent.putExtra("lname", lnametxt);
        intent.putExtra("a1", a1txt);
        intent.putExtra("a2", a2txt);
        intent.putExtra("a3", a3txt);
        intent.putExtra("a4", a4txt);
        intent.putExtra("a5", a5txt);
        startActivity(intent);
        finish();
    }
    private void intentData() {
        getText();
        Intent intent = new Intent(this, ViewEntry.class);
        intent.putExtra("id", id);
        intent.putExtra("fname", fnameText);
        intent.putExtra("lname", lnameText);
        intent.putExtra("a1", a1Text);
        intent.putExtra("a2", a2Text);
        intent.putExtra("a3", a3Text);
        intent.putExtra("a4", a4Text);
        intent.putExtra("a5", a5Text);
        startActivity(intent);
        finish();
    }

    private void getText() {
        fnameText = fname_et.getText().toString();
        lnameText = lname_et.getText().toString();
        a1Text = ans1.getText().toString();
        a2Text = ans2.getText().toString();
        a3Text = ans3.getText().toString();
        a4Text = ans4.getText().toString();
        a5Text = ans5.getText().toString();
    }

    @Override
    public void onClick(View view) {
        if (view == savebtn) {
            getText();
            try {
                saveData(fnameText, lnameText, a1Text, a2Text, a3Text, a4Text, a5Text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (view == replaceimg) {
            pick_img.imgDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File photofile = null;
                        try {
                            photofile = create_imgfile.createfile(EditEntry.this);
                        } catch (IOException e) {

                        }
                        if (photofile != null) {
                            cameraUri = FileProvider.getUriForFile(EditEntry.this,
                                    "com.example.slambookproject.provider", photofile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
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

        } else if (view == fname_et) {
            fname_et.setFocusableInTouchMode(true);
            fname_et.setFocusable(true);
            fname_et.requestFocus();
        } else if (view == lname_et) {
            lname_et.setFocusableInTouchMode(true);
            lname_et.setFocusable(true);
            lname_et.requestFocus();
        } else if (view == ans1) {
            ans1.setFocusableInTouchMode(true);
            ans1.setFocusable(true);
            ans1.requestFocus();
        } else if (view == ans2) {
            ans2.setFocusableInTouchMode(true);
            ans2.setFocusable(true);
            ans2.requestFocus();
        } else if (view == ans3) {
            ans3.setFocusableInTouchMode(true);
            ans3.setFocusable(true);
            ans3.requestFocus();
        } else if (view == ans4) {
            ans4.setFocusableInTouchMode(true);
            ans4.setFocusable(true);
            ans4.requestFocus();
        } else if (view == ans5) {
            ans5.setFocusableInTouchMode(true);
            ans5.setFocusable(true);
            ans5.requestFocus();
        } else if (view == closebtn) {
            cancel();
        }
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
            if (requestCode == REQUEST_Gallery) {
                if (data != null) {
                    uri = data.getData();
                    mybitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imguser.setImageBitmap(mybitmap);
                }
            } else if (requestCode == REQUEST_Camera) {
                mybitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), cameraUri);
                imguser.setImageBitmap(mybitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void init() {
        progressDialog = new ProgressDialog(this);
        closebtn = findViewById(R.id.closebtn_editEnt);
        ans1 = findViewById(R.id.ans1_editEnt);
        ans2 = findViewById(R.id.ans2_editEnt);
        ans3 = findViewById(R.id.ans3_editEnt);
        ans4 = findViewById(R.id.ans4_editEnt);
        ans5 = findViewById(R.id.ans5_editEnt);
        replaceimg = findViewById(R.id.replace_img);
        fname = findViewById(R.id.fname_editEnt);
        lname = findViewById(R.id.lname_editEnt);
        fname_et = findViewById(R.id.fnam_et);
        lname_et = findViewById(R.id.lname_et);
        imguser = findViewById(R.id.imguser_editEnt);
        savebtn = findViewById(R.id.savebtn_EditEnt);
        getImg = new getEntryimg();
        create_imgfile = new createImgFile();
        pick_img = new DialogUtils();
        a1txt = getIntent().getExtras().getString("a1");
        a2txt = getIntent().getExtras().getString("a2");
        a3txt = getIntent().getExtras().getString("a3");
        a4txt = getIntent().getExtras().getString("a4");
        a5txt = getIntent().getExtras().getString("a5");
        fnametxt = getIntent().getExtras().getString("fname");
        lnametxt = getIntent().getExtras().getString("lname");

        id = getIntent().getIntExtra("id", 0);
        ans1.setText(a1txt);
        ans2.setText(a2txt);
        ans3.setText(a3txt);
        ans4.setText(a4txt);
        ans5.setText(a5txt);
        fname_et.setText(fnametxt);
        lname_et.setText(lnametxt);
        getImg.imgentry(this, id, new getEntryimg.getImagecallback() {
            @Override
            public void onCallback(Bitmap bitmap) {
                mybitmap = bitmap;
                imguser.setImageBitmap(bitmap);
            }
        });
        closebtn.setOnClickListener(this);
        replaceimg.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        fname_et.setOnClickListener(this);
        lname_et.setOnClickListener(this);
        ans1.setOnClickListener(this);
        ans2.setOnClickListener(this);
        ans3.setOnClickListener(this);
        ans4.setOnClickListener(this);
        ans5.setOnClickListener(this);

    }

    private void request(String imgStr, String fname, String lname, String a1, String a2, String a3, String a4, String a5) throws JSONException {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialogloading);
        String url = urlClass.local_host + "entry/update_entry.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("image", imgStr);
        obj.put("fname", fname);
        obj.put("lname", lname);
        obj.put("ans1", a1);
        obj.put("ans2", a2);
        obj.put("ans3", a3);
        obj.put("ans4", a4);
        obj.put("ans5", a5);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    Toast.makeText(EditEntry.this, "Server Response " + message, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    intentData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LogError", error.toString());
                progressDialog.dismiss();
            }
        });
        objectRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(objectRequest);
    }

    public void saveData(String fname, String lname, String a1, String a2, String a3, String a4, String a5) throws JSONException {
        if (!isName(fname, lname) || !isAns(a1, a2, a3, a4, a5)) {
            return;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (uri != null || cameraUri != null) {
                Toast.makeText(this, "bit " + mybitmap, Toast.LENGTH_SHORT).show();
                mybitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] imgTobyte = baos.toByteArray();
                imgStr = Base64.encodeToString(imgTobyte, Base64.DEFAULT);
                request(imgStr, fname, lname, a1, a2, a3, a4, a5);
            } else {
                Toast.makeText(this, "bit " + mybitmap, Toast.LENGTH_SHORT).show();
                mybitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] imgTobyte = baos.toByteArray();
                imgStr = Base64.encodeToString(imgTobyte, Base64.DEFAULT);
                request(imgStr, fname, lname, a1, a2, a3, a4, a5);
            }
        }
    }

    @Override
    public void onBackPressed() {
        cancel();
    }
}