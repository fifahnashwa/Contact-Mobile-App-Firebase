package com.example.firebaseteori;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LamanDaftar extends AppCompatActivity{
    private static final String TAG = "LamanDaftar";
    private EditText etEmail;
    private EditText etPass;
    private Button btnDaftar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laman_daftar);

        etEmail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_pass);
        btnDaftar = findViewById(R.id.btn_daftar);

        mAuth = FirebaseAuth.getInstance();

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp(etEmail.getText().toString(), etPass.getText().toString());
            }
        });
    }

    public void signUp(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(LamanDaftar.this,
                                    user.toString(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LamanDaftar.this,
                                    task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Required");
            result = false;
        } else {
            etEmail.setError(null);
        }
        if (TextUtils.isEmpty(etPass.getText().toString())) {
            etPass.setError("Required");
            result = false;
        } else {
            etPass.setError(null);
        }
        return result;
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
        Intent intent = new Intent(LamanDaftar.this, LamanUtamaContact.class);
        startActivity(intent);
        } else {
            Toast.makeText(LamanDaftar.this, "Sign up failed", Toast.LENGTH_SHORT).show();}
    }
}