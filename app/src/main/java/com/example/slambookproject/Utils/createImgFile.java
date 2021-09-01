package com.example.slambookproject.Utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class createImgFile {
    public static String currentpath;
    public static File createfile(Context context) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
        String imgfilename = "JPEG_" + timestamp;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File img = File.createTempFile(
                imgfilename,
                ".jpg",
                storageDir
        );
        currentpath = img.getAbsolutePath();
        return img;
    }
}
