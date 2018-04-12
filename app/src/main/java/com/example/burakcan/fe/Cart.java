package com.example.burakcan.fe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burakcan.fe.Common.Common;
import com.example.burakcan.fe.Database.Database;
import com.example.burakcan.fe.Model.Order;
import com.example.burakcan.fe.Model.Request;
import com.example.burakcan.fe.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import github.ishaan.buttonprogressbar.ButtonProgressBar;

public class Cart extends AppCompatActivity {
    
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    
    FirebaseDatabase database;
    DatabaseReference requests;
    
    ButtonProgressBar btnPlaceOrder;
    TextView txtTotalPrice;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        //firebase
        database = FirebaseDatabase.getInstance();
        requests=database.getReference("Request");
        
        //init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        txtTotalPrice = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlaceOrder.startLoader();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnPlaceOrder.stopLoader();
                        if (cart.size() >  0 ) {
                            showAlertDialog();
                        } else
                        {
                                Toast.makeText(Cart.this, "Sepetenizde urun bulunmamaktadir.", Toast.LENGTH_SHORT).show();
                            }
                        }

                }, 2000);
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Son bir Adim");
        alertDialog.setMessage("Adresinizi girin : ");

        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),cart
                );

                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                //cart delte
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this,"Tesekkurler siparisiniz alinmistir.",Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        alertDialog.setNegativeButton("Hayir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loadListFood() {

        cart = new Database(getApplicationContext()).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("tr","TR");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    if (item.getTitle().equals(Common.DELETE))

        DeleteCart(item.getOrder());
    
    
     return true;   
    }

    private void DeleteCart(int position) {
        //eklenen siparisi kaldir
        cart.remove(position);
        //sql tablosundaki eski verileri silicez
        new Database(this).cleanCart();
        //yeni listeyi yuklucez
        for (Order item : cart)
            new Database(this).addToCart(item);

        loadListFood();
    }
}
