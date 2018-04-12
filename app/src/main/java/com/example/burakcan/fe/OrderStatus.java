package com.example.burakcan.fe;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.burakcan.fe.Common.Common;
import com.example.burakcan.fe.Model.Request;
import com.example.burakcan.fe.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;




public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;

    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");
        // listorder menusune tasi
        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //orderstatusda home activityi calstiricaz
        if (getIntent() == null )
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));//servisdeki valuekey
    }


       private void loadOrders (String phone){

           Query listFoodByKatagoriId = requests.orderByChild("userPhone").equalTo(phone);
            FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                    .setQuery(listFoodByKatagoriId, Request.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {
                    holder.txtOrderId.setText(adapter.getRef(position).getKey());
                    holder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                    holder.txtOrderPhone.setText(model.getPhone());
                    holder.txtOrderAddress.setText(model.getAddress());


                }

                @NonNull
                @Override
                public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);

                    return new OrderViewHolder(itemView);
                }
            };
            adapter.startListening();


            recyclerView.setAdapter(adapter);
        }

        @Override
        protected void onStop () {
            super.onStop();
            adapter.stopListening();
        }
    }
