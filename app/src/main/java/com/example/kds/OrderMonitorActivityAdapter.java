package com.example.kds;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderMonitorActivityAdapter extends RecyclerView.Adapter<OrderMonitorActivityAdapter.MyViewHolder> {

    ArrayList<String> itemname, itemqty, invoicenumber, tableid, iteminvoicenumber;
    DatabaseReference kdsitemsreference;
    String username;

    public OrderMonitorActivityAdapter(ArrayList<String> itemname, ArrayList<String> itemqty, ArrayList<String> invoicenumber, ArrayList<String> tableid, ArrayList<String> iteminvoicenumber, String username) {
        this.itemname = itemname;
        this.itemqty = itemqty;
        this.invoicenumber = invoicenumber;
        this.tableid = tableid;
        this.iteminvoicenumber = iteminvoicenumber;
        this.username = username;
        kdsitemsreference = FirebaseDatabase.getInstance().getReference("KDSItems").child(username);
    }


    @NonNull
    @Override
    public OrderMonitorActivityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_kds_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderMonitorActivityAdapter.MyViewHolder holder, int position) {
        holder.invoicenumber.setText("Invoice - " + invoicenumber.get(position) + " ( Table - " + tableid.get(position) + " )");
        holder.items.setText("");
        for (int i = 0; i < itemname.size(); i++) {
            if (iteminvoicenumber.get(i).equals(invoicenumber.get(position))) {
                holder.items.append("\n"  + itemname.get(i) + " ( " + itemqty.get(i) + " )");
            }
        }
        holder.changeorderstatusbtn.setText("Start Preparing");

        holder.changeorderstatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kdsitemsreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(invoicenumber.get(position)).child("orderstatus").getValue(String.class).equals("To Be Prepared")) {
                            holder.changeorderstatusbtn.setText("Prepared");
                            kdsitemsreference.child(invoicenumber.get(position)).child("orderstatus").setValue("Prepared");
                        } else {
                            kdsitemsreference.child(invoicenumber.get(position)).removeValue();
                            kdsitemsreference.child("change").setValue("true");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoicenumber.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView invoicenumber, items;
        Button changeorderstatusbtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            invoicenumber = itemView.findViewById(R.id.invoice_number_textview);
            items = itemView.findViewById(R.id.items_textview);
            changeorderstatusbtn = itemView.findViewById(R.id.change_order_status);
        }
    }
}
