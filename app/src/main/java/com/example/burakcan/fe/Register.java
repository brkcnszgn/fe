package com.example.burakcan.fe;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.burakcan.fe.Common.Common;
import com.example.burakcan.fe.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import github.ishaan.buttonprogressbar.ButtonProgressBar;

public class Register extends AppCompatActivity {
    ButtonProgressBar btnLog;
    MaterialEditText edtPhone, edtPassword, edtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtName = findViewById(R.id.edtName);

//Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        final ButtonProgressBar bar = (ButtonProgressBar) findViewById(R.id.btnRegister);
        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.startLoader();

                    //bilgilendirme
                    final ProgressDialog progressDialog = new ProgressDialog(Register.this);
                    progressDialog.setMessage("Lutfen Bekleyin");
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bar.stopLoader();
                            table_user.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //numara kontrolu
                                    if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Register.this, "Numara bulunmaktadir.", Toast.LENGTH_SHORT).show();

                                    } else {
                                        progressDialog.dismiss();
                                        User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                                        table_user.child(edtPhone.getText().toString()).setValue(user);
                                        Toast.makeText(Register.this, "Kayit Basarili.", Toast.LENGTH_SHORT).show();
                                        finish();
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
