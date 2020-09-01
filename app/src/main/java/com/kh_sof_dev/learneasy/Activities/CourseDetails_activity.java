package com.kh_sof_dev.learneasy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kh_sof_dev.learneasy.R;


public class CourseDetails_activity extends AppCompatActivity implements View.OnClickListener {

    private static final long SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cource_details);

init();
    }
    ImageButton back,delete,last,next,play;
    TextView name;
    ImageView imag;
    private void init() {
        back=findViewById(R.id.back);
        delete=findViewById(R.id.delete);
        last=findViewById(R.id.last);
        next=findViewById(R.id.next);
        play=findViewById(R.id.sound);
        name=findViewById(R.id.name);
        imag=findViewById(R.id.img);

        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        next.setOnClickListener(this);
        last.setOnClickListener(this);
        play.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.back:
                finish();
                break;
            case R.id.last:
                break;
            case R.id.next:
                break;
            case R.id.sound:
                break;
            case R.id.delete:
                break;
        }
    }
}
