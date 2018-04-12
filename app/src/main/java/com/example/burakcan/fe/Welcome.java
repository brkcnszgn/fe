package com.example.burakcan.fe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class Welcome extends AppCompatActivity {


    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

       // btnLog = findViewById(R.id.btnLogin);
      //  btnReg = findViewById(R.id.btnRegister);
        txtSlogan = findViewById(R.id.txtSlogan);
        // texte verilen fonts ozellligi
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        Paper.init(this);


        final ButtonProgressBar bar = (ButtonProgressBar) findViewById(R.id.btnLogin);
        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bar.startLoader();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bar.stopLoader();

                                //islem kontorulunden sonra yonlendirme
                                Intent git = new Intent(Welcome.this, Login.class);
                                startActivity(git);

                        }
                    }, 2000);

            }
        });
        final ButtonProgressBar bar2 = (ButtonProgressBar) findViewById(R.id.btnRegister);
        bar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar2.startLoader();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bar2.stopLoader();
                            Intent git = new Intent(Welcome.this, Register.class);
                            startActivity(git);
                        }
                    }, 2000);
                }

        });
        //kontrol et ckhbox

        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null)
        {
            if (!user.isEmpty()&&!pwd.isEmpty())
            {
                login(user,pwd);
            }
        }

    }

    private void login(final String phone, final String pwd) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user varmi yok mu kontrol

                if (dataSnapshot.child(phone).exists()) {


                    User user = dataSnapshot.child(phone).getValue(User.class);
                    if (user != null) {
                        user.setPhone(phone);
                    }

                    if (user != null) {
                        if (user.getPassword().equals(pwd)) {
                            Toast.makeText(Welcome.this, "Giris Basarili", Toast.LENGTH_SHORT).show();
                            //part2
                            Intent home = new Intent(Welcome.this, Home.class);
                            Common.currentUser = user;
                            startActivity(home);
                            finish();

                        } else {

                            Toast.makeText(Welcome.this, "Giris Basarisiz", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {

                    Toast.makeText(Welcome.this, "Kullanici veritabaninda Bulunmuyor", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

