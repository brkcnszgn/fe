package com.example.burakcan.fe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationListener;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.burakcan.fe.Common.Common;
import com.example.burakcan.fe.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import github.ishaan.buttonprogressbar.ButtonProgressBar;
import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    ButtonProgressBar bar;
    EditText edtPhone, edtPassword;
    com.rey.material.widget.CheckBox chkBox;
     ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        chkBox =  findViewById(R.id.ckbox);

        //paperdb
        Paper.init(this);

        //Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

         bar = (ButtonProgressBar) findViewById(R.id.btnLogin);
        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bar.startLoader();
                if (chkBox.isChecked())
                {
                    Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                    Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
                }

                    //bilgilendirme
                     progressDialog = new ProgressDialog(Login.this);
                    progressDialog.setMessage("Lutfen Bekleyin");
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bar.stopLoader();


                                table_user.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //user varmi yok mu kontrol

                                        if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {


                                            //kullanici bilgilerini kontrol et ve al
                                            //prograres iptal
                                            progressDialog.dismiss();

                                            User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                            if (user != null) {
                                                user.setPhone(edtPhone.getText().toString());
                                            }

                                            if (user != null) {
                                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                                    Toast.makeText(Login.this, "Giris Basarili", Toast.LENGTH_SHORT).show();
                                                    //part2
                                                    Intent home = new Intent(Login.this, Home.class);
                                                    Common.currentUser = user;
                                                    startActivity(home);
                                                    finish();

                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Login.this, "Giris Basarisiz", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(Login.this, "Kullanici veritabaninda Bulunmuyor", Toast.LENGTH_SHORT).show();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }


                    }, 2000);

            }
        });

    }
}
