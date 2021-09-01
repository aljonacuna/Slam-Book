package com.example.slambookproject.Utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class pass_encode_arr_for_spinner {

    public static String encodePass(String password) {
        byte[] data = password.getBytes(StandardCharsets.UTF_8); // converts password to Base64 encoding for security
        password = Base64.encodeToString(data, Base64.DEFAULT); // converts password to Base64 encoding for security
        return password;
    }
}
