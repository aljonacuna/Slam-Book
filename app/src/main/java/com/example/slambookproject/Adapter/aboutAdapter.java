package com.example.slambookproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slambookproject.Model.aboutList;
import com.example.slambookproject.R;

import java.util.ArrayList;

public class aboutAdapter extends RecyclerView.Adapter<aboutAdapter.ViewHolder> {
    private Context context;
    private ArrayList<aboutList> devList;

    public aboutAdapter(Context context, ArrayList<aboutList> devList) {
        this.context = context;
        this.devList = devList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.about_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        aboutList devs = devList.get(position);
        holder.name.setText(devs.getName());
        holder.img.setImageResource(devs.getImg());
        holder.a1.setText(devs.getA1());
        holder.a2.setText(devs.getA2());
        holder.a3.setText(devs.getA3());
        holder.a4.setText(devs.getA4());
        holder.a5.setText(devs.getA5());
    }

    @Override
    public int getItemCount() {
        return devList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView a1,a2,a3,a4,a5,name;
        ImageView img,viewmore,viewlesss;
        LinearLayout ansLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            a1 = itemView.findViewById(R.id.ans1View);
            a2 = itemView.findViewById(R.id.ans2View);
            a3 = itemView.findViewById(R.id.ans3View);
            a4 = itemView.findViewById(R.id.ans4View);
            a5 = itemView.findViewById(R.id.ans5View);
            name = itemView.findViewById(R.id.displayname);
            img = itemView.findViewById(R.id.aboutimg);
            ansLayout = itemView.findViewById(R.id.QnA);
            viewlesss = itemView.findViewById(R.id.viewless);
            viewmore = itemView.findViewById(R.id.viewmore);
            viewlesss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewmore.setVisibility(View.VISIBLE);
                    viewlesss.setVisibility(View.GONE);
                    ansLayout.setVisibility(View.GONE);
                }
            });
            viewmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewmore.setVisibility(View.GONE);
                    viewlesss.setVisibility(View.VISIBLE);
                    ansLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
