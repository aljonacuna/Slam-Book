package com.example.slambookproject.GUI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.slambookproject.Adapter.aboutAdapter;
import com.example.slambookproject.Model.aboutList;
import com.example.slambookproject.R;

import java.util.ArrayList;

public class About extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<aboutList>devList;
    private RecyclerView recyclerView;
    private aboutAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        devList = new ArrayList<>();
        recyclerView = findViewById(R.id.aboutrv);
        devList.add(new aboutList("Aljon C. Acuna",R.drawable.aljon,"To become a professionl developer",
                "26","aljon","Yun ka by kuya wil","Ice cream"));
        devList.add(new aboutList("Cristine San Diego",R.drawable.tine,"To be a successful businesswoman",
                "22","tine","You","Sinigang"));
        devList.add(new aboutList("Rodilyn Faustino",R.drawable.pres,"To be a programmer",
                "21","lindel","Your song","Beef bulalo"));
        devList.add(new aboutList("Jonnel Onesa",R.drawable.jonell,"To be successful busineman",
                "32","nhel","Your love","Adobo"));
        devList.add(new aboutList("Dominic Ignacio",R.drawable.edz,"To become a network administrator",
                "23","edz","I believe i can fly","Nilaga"));

        adapter = new aboutAdapter(this,devList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View view) {

    }
}