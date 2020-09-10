package com.kh_sof_dev.learneasy.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kh_sof_dev.learneasy.Activities.MainActivity;
import com.kh_sof_dev.learneasy.Activities.Course_Activity;
import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.adapter.Level_adapter;
import com.kh_sof_dev.learneasy.adapter.Users_adapter;
import com.kh_sof_dev.learneasy.modul.Course;
import com.kh_sof_dev.learneasy.modul.Level;
import com.kh_sof_dev.learneasy.modul.User;

import java.util.ArrayList;
import java.util.List;

public class Users_frg extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_users, container, false);
        init(root);
        loading();

        Log.d("here is HomeFragment: ",   "HomeFragment");
        return root;
    }

    private void loading() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference()
                .child("Users")
                ;
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user=dataSnapshot.getValue(User.class);
                user.setUid(dataSnapshot.getKey());
                userList.add(user);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ProgressBar progressBar;
    RecyclerView RV;
    Context mcontext=getContext();
    Users_adapter adapter;
    List<User> userList;

    private void init(View root) {
        progressBar=root.findViewById(R.id.progress);
        RV=root.findViewById(R.id.RV);
        RV.setLayoutManager(new LinearLayoutManager(mcontext,RecyclerView.VERTICAL,false));
        userList=new ArrayList<>();
        adapter=new Users_adapter(Users_frg.this.getContext(), userList,null);
        RV.setAdapter(adapter);


    }


}