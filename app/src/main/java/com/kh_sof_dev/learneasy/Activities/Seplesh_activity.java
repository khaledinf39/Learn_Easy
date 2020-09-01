package com.kh_sof_dev.learneasy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
                    Intent intent = new Intent(Seplesh_activity.this, MainActivity.class);
                    startActivity(intent );
                    finish();
                }else {
                    Intent intent = new Intent(Seplesh_activity.this, Login_activity.class);
                    startActivity(intent );
                    finish();
                }



            }
        }, SPLASH_TIME);
    }
}
