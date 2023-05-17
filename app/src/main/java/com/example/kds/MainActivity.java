package com.example.kds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView username, password;
    DatabaseReference kdscredsdatabase;
    Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username_et);
        password = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        kdscredsdatabase = FirebaseDatabase.getInstance().getReference("KDSCreds");

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kdscredsdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String Username = dataSnapshot.child("username").getValue(String.class);
                            String Password = dataSnapshot.child("password").getValue(String.class);
                            if (username.getText().toString().equals(Username) && password.getText().toString().equals(Password)) {
                                Intent intent = new Intent(MainActivity.this, OrderMonitorActivity.class);
                                intent.putExtra("username", Username);
                                startActivity(intent);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}