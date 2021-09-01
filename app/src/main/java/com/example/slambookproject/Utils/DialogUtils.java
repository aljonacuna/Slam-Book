package com.example.slambookproject.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slambookproject.R;

public class DialogUtils {
    public static Dialog dialog;
    public static Dialog img_dialog;
    public static void dialog(final Context context, String titletxt, String msgtxt, View.OnClickListener listener){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_dialog);
        TextView title = dialog.findViewById(R.id.title_dialog);
        TextView msg = dialog.findViewById(R.id.msg_dialog);
        Button accept = dialog.findViewById(R.id.accept_btn_dialog);
        Button cancel = dialog.findViewById(R.id.cancel_btn_dialog);
        accept.setOnClickListener(listener);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Cancel operation",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        title.setText(titletxt);
        msg.setText(msgtxt);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public static void imgDialog(final Context context,View.OnClickListener camerListener,View.OnClickListener galleryListener){
        img_dialog = new Dialog(context);
        img_dialog.setContentView(R.layout.pickimg_dialog);
        Button camerabtn = img_dialog.findViewById(R.id.camera_btn);
        Button gallerybtn = img_dialog.findViewById(R.id.gallery_btn);
        camerabtn.setOnClickListener(camerListener);
        gallerybtn.setOnClickListener(galleryListener);
        img_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        img_dialog.show();
    }
}
