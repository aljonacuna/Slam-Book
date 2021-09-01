package com.example.slambookproject.GUI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.slambookproject.Adapter.EntryAdapter;
import com.example.slambookproject.DBhandler.DBHelper;
import com.example.slambookproject.GUI.AddEntry;
import com.example.slambookproject.Model.Entrymodel;
import com.example.slambookproject.R;
import com.example.slambookproject.Utils.DialogUtils;
import com.example.slambookproject.Utils.urlClass;
import com.example.slambookproject.Utils.username_save;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainScreen extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private DBHelper myDB;
    private ImageView sortbtn;
    private ArrayList<Entrymodel> entries = new ArrayList<>();
    private TextView emptylbl;
    private RecyclerView recyclerView;
    private EntryAdapter adapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout linearLayout;
    String sortby = "";
    username_save unamesave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rv_entry);
        emptylbl = findViewById(R.id.noentrylbl);
        sortbtn = findViewById(R.id.sortEntry);
        unamesave = new username_save();
        shimmerFrameLayout = findViewById(R.id.shimmerMainScreen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myDB = new DBHelper(this);
        sortbtn.setOnClickListener(this);
        displayEntry();
    }

    @Override
    public void onClick(View view) {
        if (view == sortbtn) {
            toSort();
        }
    }

    public void displayEntry() {
        String url = urlClass.local_host + "entry/display_all.php?key=" + unamesave.uname;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    entries.clear();
                    for (int x = 0; x < response.length(); x++) {
                        JSONObject jsonObject = response.getJSONObject(x);
                        Entrymodel entrymodel = new Entrymodel();
                        String code = jsonObject.getString("code");
                        if (code.equals("001")) {
                            entrymodel.setId(jsonObject.getInt("id"));
                            byte[] toByteimg = Base64.decode(jsonObject.getString("image"), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(toByteimg, 0, toByteimg.length);
                            entrymodel.setImage(bitmap);
                            String unamestr = jsonObject.getString("uname");
                            entrymodel.setUname(unamestr);
                            entrymodel.setFname(jsonObject.getString("fname"));
                            entrymodel.setLname(jsonObject.getString("lname"));
                            entrymodel.setAnswer1(jsonObject.getString("ans1"));
                            entrymodel.setAnswer2(jsonObject.getString("ans2"));
                            entrymodel.setAnswer3(jsonObject.getString("ans3"));
                            entrymodel.setAnswer4(jsonObject.getString("ans4"));
                            entrymodel.setAnswer5(jsonObject.getString("ans5"));

                            entries.add(entrymodel);
                            emptylbl.setVisibility(View.GONE);
                        } else {
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            emptylbl.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(MainScreen.this, "Catch e " + e.toString(), Toast.LENGTH_LONG).show();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    Log.e("catch ", e.toString());
                    e.printStackTrace();
                }
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                setAdapter();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainScreen.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(jsonArrayRequest);
    }

    private void setAdapter() {
        adapter = new EntryAdapter(MainScreen.this, entries);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainScreen.this));
    }

    public void toSort() {
        final String[] sortArr = this.getResources().getStringArray(R.array.sort);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Sort by?");
        dialog.setSingleChoiceItems(R.array.sort, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sortby = sortArr[i];

            }
        });
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (sortby.equals("Asc")) {
                    Collections.sort(entries, new Comparator<Entrymodel>() {
                        @Override
                        public int compare(Entrymodel entrymodel, Entrymodel t1) {
                            return entrymodel.getLname().compareTo(t1.getLname());
                        }
                    });
                    setAdapter();
                } else if (sortby.equals("Desc")) {
                    Collections.sort(entries, Collections.reverseOrder(new Comparator<Entrymodel>() {
                        @Override
                        public int compare(Entrymodel entrymodel, Entrymodel t1) {
                            return entrymodel.getLname().compareTo(t1.getLname());
                        }
                    }));
                    setAdapter();
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuhome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addentry:
                Intent intent = new Intent(this, AddEntry.class);
                startActivity(intent);
                finish();
                break;
            case R.id.changepass:
                Intent changepass = new Intent(this, ChangePassword.class);
                startActivity(changepass);
                break;
            case R.id.changesq:
                Intent changesq = new Intent(this, ChangeSecretQuestion.class);
                startActivity(changesq);
                break;
            case R.id.about:
                Intent about = new Intent(this, About.class);
                startActivity(about);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }
    private void back(){
        DialogUtils.dialog(this, "Exit", "Are you sure you'd like to exit?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                DialogUtils.dialog.dismiss();
            }
        });
    }
    @Override
    public void onBackPressed() {
        back();
    }
}