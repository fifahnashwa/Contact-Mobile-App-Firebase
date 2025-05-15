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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailContactActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etPhoneNumber;
    private Button btnUpdate;
    private Button btnDelete;
    private String contactId;
    private String contactName;
    private String contactPhoneNumber;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_contact);
        etName = findViewById(R.id.det_name);
        etPhoneNumber = findViewById(R.id.det_phoneNumber);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);

        contactId = getIntent().getStringExtra("contactId");
        contactName = getIntent().getStringExtra("contactName");
        contactPhoneNumber = getIntent().getStringExtra("contactNumber");

        etName.setText(contactName);
        etPhoneNumber.setText(contactPhoneNumber);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("contacts").child(userId).child(contactId);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContact();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact();
            }
        });
    }

    private void updateContact() {
        if (!validateForm()) {
            return;
        }
        String name = etName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        Contact updatedContact = new Contact(name, phoneNumber);

        databaseReference.setValue(updatedContact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailContactActivity.this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DetailContactActivity.this, "Failed to add contact: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
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

        if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
            etPhoneNumber.setError("Required");
            result = false;
        } else {
            etPhoneNumber.setError(null);
        }
        return result;
    }

    private void deleteContact() {
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailContactActivity.this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DetailContactActivity.this, "Failed to delete contact: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}