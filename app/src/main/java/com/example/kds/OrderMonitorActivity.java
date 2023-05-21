package com.example.kds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderMonitorActivity extends AppCompatActivity {

    DatabaseReference kdsitemsreference;
    String username;
    ArrayList<String> itemname = new ArrayList<>();
    ArrayList<String> itemqty = new ArrayList<>();
    ArrayList<String> invoicenumber = new ArrayList<>();
    ArrayList<String> tableid = new ArrayList<>();
    ArrayList<String> iteminvoicenumber = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_monitor);
        username = getIntent().getStringExtra("username");
        kdsitemsreference = FirebaseDatabase.getInstance().getReference("KDSItems").child(username);
        recyclerView = findViewById(R.id.activity_order_monitor_recyclerview);
        layoutManager = new LinearLayoutManager(OrderMonitorActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new OrderMonitorActivityAdapter(itemname,itemqty,invoicenumber,tableid,iteminvoicenumber, username);
        recyclerView.setAdapter(adapter);

        checkitems();
        kdscheck();

        kdsitemsreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("change").getValue(String.class).equals("true")) {
                    checkitems();
                    kdscheck();
                    kdsitemsreference.child("change").setValue("false");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void kdscheck() {
        kdsitemsreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invoicenumber.clear();
                tableid.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String invno = dataSnapshot.getKey();
                    kdsitemsreference.child(invno).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String orderStatus = snapshot.child("orderstatus").getValue(String.class);
                            if (orderStatus != null && orderStatus.equals("To Be Prepared")) {
                                invoicenumber.add(snapshot.child("invoicenumber").getValue(String.class));
                                tableid.add(snapshot.child("tableid").getValue(String.class));
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public  void  checkitems() {
        kdsitemsreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemname.clear();
                itemqty.clear();
                iteminvoicenumber.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String invno = dataSnapshot.getKey();
                    kdsitemsreference.child(invno).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                String id = dataSnapshot1.getKey();
                                if (dataSnapshot.child(id).child("itemname").getValue(String.class) != null) {
                                    String orderStatus = dataSnapshot.child("orderstatus").getValue(String.class);
                                    if (orderStatus != null && orderStatus.equals("To Be Prepared")) {
                                        itemname.add(dataSnapshot.child(id).child("itemname").getValue(String.class));
                                        itemqty.add(dataSnapshot.child(id).child("itemqty").getValue(String.class));
                                        iteminvoicenumber.add(dataSnapshot.child("invoicenumber").getValue(String.class));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}