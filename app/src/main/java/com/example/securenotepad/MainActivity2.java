package com.example.securenotepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;


public class MainActivity2 extends AppCompatActivity {

    private String note_text;
    private String encrypt_text;

    private static SecretKey secretKeySpec;
    private static IvParameterSpec ivParameterSpec;

    private static final String IV_PREF = "iv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView password_stored = findViewById(R.id.password);

        MaterialButton changebtn = findViewById(R.id.change);

        MaterialButton savebtn = findViewById(R.id.save);

        EditText note = findViewById(R.id.editTextTextMultiLine);

        initializeIv();

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.securenotepad", Context.MODE_PRIVATE);

        String salt = prefs.getString("salt","default");

        Intent intento = getIntent();

        String hashPassword = intento.getStringExtra("contra");

        secretKeySpec = generatePBKDF2Key(hashPassword,salt,256,10000);

        String encrypted_text = prefs.getString("note_text","Example note");

        if (!encrypted_text.equals("Example note")) {
            note.setText(decrypt(encrypted_text));
        }



        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note_text = note.getText().toString();
                if (!note_text.equals("")) {

                    encrypt_text = encrypt(note_text);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("note_text", encrypt_text);
                    editor.apply();
                }

            }
        });

        changebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(acceptablePassword(password_stored.getText().toString())) {
                        Intent intent_21 = new Intent(MainActivity2.this, MainActivity.class);
                        //I have  to decrypt the note with the last password and encrypt it with the new one using PBKDF2
                        String hashedPassword = get_SHA_256_SecurePassword(password_stored.getText().toString(),salt);
                        change_encrypt(hashedPassword,salt);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("salt", salt);
                        editor.putString("hashpassword", hashedPassword);
                        editor.apply();
                        Toast.makeText(MainActivity2.this, "PASSWORD CHANGED", Toast.LENGTH_SHORT).show();
                        startActivity(intent_21);
                    }
                    else
                        Toast.makeText(MainActivity2.this, "INVALID PASSWORD, MINIMUM NUMBER OF CHARACTERS:12", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
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
    public boolean acceptablePassword(String password){
        return password.length()>11;
    }

    private void initializeIv() {

        try {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.securenotepad", Context.MODE_PRIVATE);
            // Try to charge the key and the stored initialization vector
            String storedIv = prefs.getString(IV_PREF, null);

            if (storedIv != null) {
                ivParameterSpec = new IvParameterSpec(Base64.decode(storedIv, Base64.DEFAULT));
            } else {
                // If there are no stored values, generate new ones and store them
                ivParameterSpec = generateIv();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(IV_PREF, Base64.encodeToString(ivParameterSpec.getIV(), Base64.DEFAULT));
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] decodedBytes = Base64.decode(encrypted, Base64.DEFAULT);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Unknown Error";
        }
    }

    public static SecretKey generatePBKDF2Key(String password,  String salt, int keyLength, int iterations) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, keyLength);
            return factory.generateSecret(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void change_encrypt(String password, String salt){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.example.securenotepad", Context.MODE_PRIVATE);

        String decrypted_text = decrypt(prefs.getString("note_text","Example note"));

        secretKeySpec = generatePBKDF2Key(password,salt, 256, 10000);

        encrypt_text = encrypt(decrypted_text);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("note_text", encrypt_text);
        editor.apply();
    }
}

