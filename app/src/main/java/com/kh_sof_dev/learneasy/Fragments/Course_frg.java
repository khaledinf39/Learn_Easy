package com.kh_sof_dev.learneasy.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kh_sof_dev.learneasy.Activities.MainActivity;
import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.adapter.Course_adapter;
import com.kh_sof_dev.learneasy.adapter.Level_adapter;
import com.kh_sof_dev.learneasy.modul.Course;
import com.kh_sof_dev.learneasy.modul.Level;

import java.util.ArrayList;
import java.util.List;

public class Course_frg extends Fragment {


    public Course_frg(String uid) {
        level_id=uid;
    }
   public static String level_id=null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.course, container, false);
        init(root);
        loading();
        return root;
    }

    private void loading() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference()
                .child("Levels").child(level_id);
        myRef.child("Courses").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Course course=dataSnapshot.getValue(Course.class);
                course.setUid(dataSnapshot.getKey());
                courseList.add(course);
                adapter.notifyDataSetChanged();
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


    RecyclerView RV;
    Context mcontext=getActivity();
    Course_adapter adapter;
    List<Course> courseList;

    private void init(View root) {
        RV=root.findViewById(R.id.RV);
        RV.setLayoutManager(new GridLayoutManager(mcontext,2));
        courseList=new ArrayList<>();
        adapter=new Course_adapter(mcontext, courseList, new Course_adapter.Selected_item() {
            @Override
            public void Onselcted(Course course) {

            }
        });


    }

    public void switchFGM(Fragment fragment) {
        MainActivity.transaction =getChildFragmentManager().beginTransaction();
        MainActivity.transaction.replace(R.id.nav_host_fragment, fragment);
        MainActivity.transaction.commit();
    }
}

