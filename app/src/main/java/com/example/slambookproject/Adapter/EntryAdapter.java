package com.example.slambookproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slambookproject.GUI.ViewEntry;
import com.example.slambookproject.Model.Entrymodel;
import com.example.slambookproject.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Entrymodel>entryList;

    public EntryAdapter(Context context, ArrayList<Entrymodel> entryList) {
        this.context = context;
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entry_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entrymodel entrymodel = entryList.get(position);
        String lname = entrymodel.getLname();
        String getFirstLetter = lname.substring(0,1);
        holder.firstletter.setText(getFirstLetter);
        holder.name.setText(entrymodel.getFname() +" "+lname);
        int lengtname = holder.name.length();
        if (lengtname>12){
            holder.name.setTextSize(16);
        }
        holder.img.setImageBitmap(entrymodel.getImage());
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView firstletter,name;
        private ImageView img;
        private ConstraintLayout viewEntryBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstletter = itemView.findViewById(R.id.firstLettername);
            name = itemView.findViewById(R.id.entryname);
            img = itemView.findViewById(R.id.entry_img);
            viewEntryBtn = itemView.findViewById(R.id.sublayoutEntryList);
            viewEntryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Bitmap bitmap  = entryList.get(pos).getImage();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[]toByteImg = baos.toByteArray();
                    Intent intent = new Intent(context, ViewEntry.class);
                    intent.putExtra("id",entryList.get(pos).getId());
                    intent.putExtra("fname",entryList.get(pos).getFname());
                    intent.putExtra("lname",entryList.get(pos).getLname());
                    intent.putExtra("a1",entryList.get(pos).getAnswer1());
                    intent.putExtra("a2",entryList.get(pos).getAnswer2());
                    intent.putExtra("a3",entryList.get(pos).getAnswer3());
                    intent.putExtra("a4",entryList.get(pos).getAnswer4());
                    intent.putExtra("a5",entryList.get(pos).getAnswer5());
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
        }
    }
}
