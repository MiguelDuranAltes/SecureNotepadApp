package com.example.securenotepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    public static final String SALT = "salt";
    private int attempts;
    private Handler handler;
    private boolean waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        attempts = 0;
        handler = new Handler();
        waiting = false;

        TextView passwordscreen = findViewById(R.id.password);
        MaterialButton loginbtn = findViewById(R.id.button);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.securenotepad", Context.MODE_PRIVATE);

        String salt = prefs.getString(SALT,"default");
        if (salt.equals("default")) {
            Intent intent3 = new Intent(MainActivity.this, MainActivity3.class);
            startActivity(intent3);
        } else {
            String password_stored = prefs.getString("hashpassword", "default");

            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (waiting) {
                            // If the user is waiting don't let do more attempts
                            Toast.makeText(MainActivity.this, "Please wait 30 seconds", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String contra = get_SHA_256_SecurePassword(passwordscreen.getText().toString(), salt);
                        if (contra.equals(password_stored)) {
                            // correct
                            Intent intent_12 = new Intent(MainActivity.this, MainActivity2.class);
                            Toast.makeText(MainActivity.this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            intent_12.putExtra("contra",contra);
                            startActivity(intent_12);
                        } else {
                            // incorrect
                            Toast.makeText(MainActivity.this, "LOGIN FAILED, TRY AGAIN", Toast.LENGTH_SHORT).show();
                            attempts++;
                            maxAttempts();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void maxAttempts(){
        if (attempts == 5) {
            // Wait 30 seconds after 5 wrong attempts
            waiting = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    waiting = false;
                    attempts = 0; // Reboot the counter
                }
            }, 30000); // 30 seconds in miliseconds

        }
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
}
