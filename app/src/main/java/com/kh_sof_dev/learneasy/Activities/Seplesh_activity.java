package com.kh_sof_dev.learneasy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kh_sof_dev.learneasy.R;


public class Seplesh_activity extends AppCompatActivity {

    private static final long SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seplash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth auth= FirebaseAuth.getInstance();
                FirebaseUser user=auth.getCurrentUser();
                if (user!=null){
                    if (user.getPhoneNumber().equals("+213672886642") ||
                            user.getPhoneNumber().equals("+966533598959")){
                        MainActivity.isAdmin=true;
                        Intent intent = new Intent(Seplesh_activity.this, MainActivity.class);
                        startActivity(intent );
                        finish();
                        return;
                    }
                    ///get my last level

                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    DatabaseReference reference=database.getReference().child("Users").child(auth.getUid());
                    reference.child("My_level").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                MainActivity.My_level=dataSnapshot.getValue(Integer.class);
                            }
                            Intent intent = new Intent(Seplesh_activity.this, MainActivity.class);
                            startActivity(intent );
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    Intent intent = new Intent(Seplesh_activity.this, Login_activity.class);
                    startActivity(intent );
                    finish();
                }



            }
        }, SPLASH_TIME);
    }
}
