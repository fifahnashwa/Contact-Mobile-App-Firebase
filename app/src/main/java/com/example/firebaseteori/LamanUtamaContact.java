package com.example.firebaseteori;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LamanUtamaContact extends AppCompatActivity {
    private Button btnKeluar;
    private FirebaseAuth mAuth;
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private List<Contact> contactList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laman_utama_contact);

        mAuth = FirebaseAuth.getInstance();

        btnKeluar = findViewById(R.id.btn_keluar);
        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                Intent intent = new Intent(LamanUtamaContact.this, DetailContactActivity.class);
                intent.putExtra("contactId", contact.getId());
                intent.putExtra("contactName", contact.getName());
                intent.putExtra("contactNumber", contact.getPhoneNumber());
                startActivity(intent);
            }
        });
        contactList = new ArrayList<>();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("contacts").child(userId);

            loadContactsFromFirebase();
        }

        ImageView addnewContact = findViewById(R.id.addContact);
        addnewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LamanUtamaContact.this, InsertContactActivity.class), 100);
            }
        });
    }

    public void logOut() {
        mAuth.signOut();
        Intent intent = new Intent(LamanUtamaContact.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loadContactsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    if (contact != null) {
                        contact.setId(snapshot.getKey());
                        contactList.add(contact);
                    }
                }

                adapter.setContactList(contactList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LamanUtamaContact.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadContactsFromFirebase();
        }
    }

}