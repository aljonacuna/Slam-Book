package com.example.slambookproject.GUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.slambookproject.Utils.getEntryimg;
import com.example.slambookproject.Utils.urlClass;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewEntry extends AppCompatActivity {
    private TextView name;
    private EditText a1, a2, a3, a4, a5;
    private ImageView imguser;
    private DBHelper myDB;
    private Toolbar toolbar;
    String a1txt, a2txt, a3txt, a4txt, a5txt, fnametxt, lnametxt, img;
    int id;
    getEntryimg getImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);
        name = findViewById(R.id.nameFull);
        imguser = findViewById(R.id.imguserView);
        myDB = new DBHelper(this);
        a1 = findViewById(R.id.ans1View);
        a2 = findViewById(R.id.ans2View);
        a3 = findViewById(R.id.ans3View);
        a4 = findViewById(R.id.ans4View);
        a5 = findViewById(R.id.ans5View);
        toolbar = findViewById(R.id.view_entryToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        getImg = new getEntryimg();
        a1txt = getIntent().getExtras().getString("a1");
        a2txt = getIntent().getExtras().getString("a2");
        a3txt = getIntent().getExtras().getString("a3");
        a4txt = getIntent().getExtras().getString("a4");
        a5txt = getIntent().getExtras().getString("a5");
        fnametxt = getIntent().getExtras().getString("fname");
        lnametxt = getIntent().getExtras().getString("lname");
        id = getIntent().getIntExtra("id", 0);
        a1.setText(a1txt);
        a2.setText(a2txt);
        a3.setText(a3txt);
        a4.setText(a4txt);
        a5.setText(a5txt);
        name.setText(fnametxt + " " + lnametxt);
        getImg.imgentry(this, id, new getEntryimg.getImagecallback() {
            @Override
            public void onCallback(Bitmap bitmap) {
                imguser.setImageBitmap(bitmap);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_delete_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editEntry:
                Intent intent = new Intent(this, EditEntry.class);
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
                break;
            case R.id.deleteEntry:
                String title = "Delete Entry?";
                String msg = "Are you sure you want to delete this entry?";
                DialogUtils.dialog(this, title, msg, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        del();
                        DialogUtils.dialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
        return true;
    }

    private void del() {
        String url = urlClass.local_host + "entry/del_entry.php?id=" + id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("code");
                    if (message.equals("001")) {
                        Toast.makeText(ViewEntry.this, "Entry successfully deleted!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewEntry.this, MainScreen.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ViewEntry.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoError", error.toString());
            }
        });
        requestQueue.add(objectRequest);
    }

    private void cancel() {
        DialogUtils.dialog(this, "Back?",
                "If you click yes you will be redirected to main screen", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtils.dialog.dismiss();
                        Intent intent = new Intent(ViewEntry.this, MainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        cancel();
    }
}