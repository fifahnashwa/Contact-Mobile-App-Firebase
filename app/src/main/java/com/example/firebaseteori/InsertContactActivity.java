package com.example.firebaseteori;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertContactActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etName;
    private EditText etPNumber;
    private Button btnSubmit;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert_contact);

        etName = findViewById(R.id.et_name);
        etPNumber = findViewById(R.id.et_phoneNumber);
        btnSubmit = findViewById(R.id.btn_submit);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        submitData();
    }

    public void submitData() {
        if (!validateForm()) {
            return;
        }
        String name = etName.getText().toString();
        String phoneNumber = etPNumber.getText().toString();
        Contact newContact = new Contact(name, phoneNumber);

        databaseReference.child("contacts").child(mAuth.getUid()).push().setValue(newContact)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(InsertContactActivity.this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InsertContactActivity.this, "Failed to add contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(etName.getText().toString())) {
            etName.setError("Required");
            result = false;
        } else {
            etName.setError(null);
        }

        if (TextUtils.isEmpty(etPNumber.getText().toString())) {
            etPNumber.setError("Required");
            result = false;
        } else {
            etPNumber.setError(null);
        }

        return result;
    }

}