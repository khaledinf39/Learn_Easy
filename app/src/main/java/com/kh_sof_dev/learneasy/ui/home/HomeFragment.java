package com.kh_sof_dev.learneasy.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kh_sof_dev.learneasy.Activities.MainActivity;
import com.kh_sof_dev.learneasy.Fragments.Course_frg;
import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.adapter.Level_adapter;
import com.kh_sof_dev.learneasy.modul.Level;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
init(root);
loading();
        return root;
    }

    private void loading() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference()
                .child("Levels")
                ;
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Level level=dataSnapshot.getValue(Level.class);
                level.setUid(dataSnapshot.getKey());
                levelList.add(level);
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
    Context mcontext=getActivity();
    Level_adapter adapter;
    List<Level> levelList;

    private void init(View root) {
        progressBar=root.findViewById(R.id.progress);
        RV=root.findViewById(R.id.RV);
        RV.setLayoutManager(new GridLayoutManager(mcontext,2));
        levelList=new ArrayList<>();
        adapter=new Level_adapter(mcontext, levelList, new Level_adapter.Selected_item() {
            @Override
            public void Onselcted(Level level) {
                switchFGM(new Course_frg(level.getUid()));
            }
        });


    }

    public void switchFGM(Fragment fragment) {
        MainActivity.transaction =getChildFragmentManager().beginTransaction();
        MainActivity.transaction.replace(R.id.nav_host_fragment, fragment);
        MainActivity.transaction.commit();
    }
}
