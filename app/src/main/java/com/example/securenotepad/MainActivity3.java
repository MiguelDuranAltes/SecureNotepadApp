package com.example.securenotepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class MainActivity3 extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        TextView new_password = findViewById(R.id.newpassword);
        TextView repeat_password = findViewById(R.id.repeatedpassword);

        String salt;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.securenotepad", Context.MODE_PRIVATE);

        MaterialButton savebtn = findViewById(R.id.change);

        try {
            salt = getSalt();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new_password.getText().toString().equals(repeat_password.getText().toString())) {
                    if (acceptablePassword(new_password.getText().toString())) {
                        String hashpassword = get_SHA_256_SecurePassword(new_password.getText().toString(), salt);
                        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("salt", salt);
                        editor.putString("hashpassword", hashpassword);
                        editor.apply();
                        startActivity(intent);
                    } else
                        Toast.makeText(MainActivity3.this, "INVALID PASSWORD, MINIMUM NUMBER OF CHARACTERS:12", Toast.LENGTH_SHORT).show();
                } else
                        Toast.makeText(MainActivity3.this, "THE PASSWORDD DON'T MATCH", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private static String get_SHA_256_SecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }
    public boolean acceptablePassword(String password){
        return password.length()>11;
    }
}