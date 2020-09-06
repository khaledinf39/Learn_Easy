package com.kh_sof_dev.learneasy.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kh_sof_dev.learneasy.Activities.MainActivity;
import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.adapter.Course_adapter;
import com.kh_sof_dev.learneasy.adapter.Level_adapter;
import com.kh_sof_dev.learneasy.modul.Course;
import com.kh_sof_dev.learneasy.modul.Level;
import com.kh_sof_dev.learneasy.modul.ResizePickedImage;
import com.kh_sof_dev.learneasy.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class Course_Activity extends AppCompatActivity {



   public static String level_id=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
     level_id= bundle.getString("level_id");

        init();
        loading();

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
                findViewById(R.id.progress).setVisibility(View.GONE);
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
    Context mcontext=Course_Activity.this;
    Course_adapter adapter;
    List<Course> courseList;

    private void init() {
        RV=findViewById(R.id.RV);
        RV.setLayoutManager(new LinearLayoutManager(mcontext,RecyclerView.VERTICAL,false));
        courseList=new ArrayList<>();
        adapter=new Course_adapter(mcontext, courseList, new Course_adapter.Selected_item() {
            @Override
            public void Onselcted(Course course,int position) {
                Intent intent=new Intent(Course_Activity.this,CourseDetails_activity.class);
                intent.putExtra("course_id",position);
//                intent.putParcelableArrayListExtra("courses", (ArrayList<? extends Parcelable>) courseList);
                startActivity(intent);
            }
        });
        RV.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               NewCourse_pop(Course_Activity.this);
            }
        });


    }

    ///images
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    Bitmap bitmap=null;
    String TAG="uploaded";
    private void imageBrowse(int id) {
        if (EasyPermissions.hasPermissions(getApplicationContext(), galleryPermissions)) {

            if (id==PICK_IMAGE_REQUEST){
                Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickerPhotoIntent, id);
            }
            else {
                Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickerPhotoIntent, id);
            }



        } else {
            EasyPermissions.requestPermissions(this, "Access for storage",
                    1000, galleryPermissions);
        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

        if (requestCode==PICK_IMAGE_REQUEST){
            Uri returnUri = data.getData();
            ResizePickedImage resizePickedImage = new ResizePickedImage();
            String realePath = resizePickedImage.getRealPathFromURI(returnUri, this);
            System.out.println(realePath);
            String compresedImagePath;
            Bitmap bitmapImage = null;
            try {
                Uri contentURI = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                BitmapDrawable background = new BitmapDrawable(bitmap);


                compresedImagePath = resizePickedImage.resizeAndCompressImageBeforeSend
                        (this, realePath, "image"+requestCode);
                mPath=compresedImagePath;



            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            mPath_sound=data.getData().getPath();
        }


        }
    }
    String mPath=null;
    String mPath_sound=null;
    private void save_course(TextView name) {

        if (name.getText().toString().isEmpty()){
            name.setError(name.getHint());
            return;
        }
        if (mPath==null){
            Toast.makeText(Course_Activity.this,
                    "Select image please  ..!"
                    , Toast.LENGTH_LONG).show();
            return;
        }
        if (mPath_sound==null){
            Toast.makeText(Course_Activity.this,
                    "Select Sound please  ..!"
                    , Toast.LENGTH_LONG).show();
            return;
        }
        final Course course=new Course();
        course.setName(name.getText().toString());
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference()
                .child("Levels").child(Course_Activity.level_id).child("Courses");


        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Loading");
        final String msg="wait a minute please ...!";
        dialog.setMessage(msg);
        dialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final String key = myRef.push().getKey();
        myRef.child(key).setValue(course);

        //////*************************Loding  image*****************************/
        System.out.println("Start upload images");
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(mPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final StorageReference ref = storage.getReference().child("CourseImage").child(course.getName() + ".jpg");

        final UploadTask uploadTask = ref.putStream(stream);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
                double progress = (-100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                Log.d(TAG, "onProgress: " + progress);
                System.out.println("Upload is " + progress + "% done");
                dialog.setMessage(msg+"\n"+
                        " Uploaded " + progress + "% ");

            }
        });
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                // GET THE IMAGE DOWNLOAD URL

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println("uri " + uri.toString());
                        Log.d(TAG, "onSuccess: the image uploaded " + uri.toString());
                        course.setImage(uri.toString());
                        mPath=null;


                    }

                });

            }
        });


        //////*************************Loding  sound*****************************/
        System.out.println("Start upload sound");
        InputStream stream1 = null;
        try {
            stream = new FileInputStream(new File(mPath_sound));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final StorageReference ref1 = storage.getReference().child("CourseSound").child(course.getName() + ".mp3");

        final UploadTask uploadTask1 = ref1.putStream(stream);

        uploadTask1.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
                double progress = (-100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                Log.d(TAG, "onProgress: " + progress);
                System.out.println("Upload is " + progress + "% done");
                dialog.setMessage(msg+"\n"+
                        " Uploaded " + progress + "% ");

            }
        });
        uploadTask1.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                // GET THE IMAGE DOWNLOAD URL

                ref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println("uri " + uri.toString());
                        Log.d(TAG, "onSuccess: the image and sound uploaded " + uri.toString());
                        Map<String, Object> map = new HashMap<>();
                        map.put("image", course.getImage());
                        map.put("sound", uri.toString());
                        myRef.child(key).updateChildren(map);
                        /********************** finsh****************/
                        dialog.dismiss();
                        Toast.makeText(Course_Activity.this,
                                "Done with Successfully  ..!"
                                , Toast.LENGTH_LONG).show();
                        mPath_sound=null;

                    }

                });

            }
        });
    }
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int PICK_Audio_REQUEST =2 ;
    public void NewCourse_pop(final Context mcontext){

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mcontext);
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View sheetView = inflater.inflate(R.layout.popup_addcourse, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        final EditText text=sheetView.findViewById(R.id.name);
        final Button uploadimg=sheetView.findViewById(R.id.uploadimg);
        uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBrowse(PICK_IMAGE_REQUEST);
            }
        });
        final Button uploadsound=sheetView.findViewById(R.id.uploadsound);
        uploadsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBrowse(PICK_Audio_REQUEST);
            }
        });
        Button save=sheetView.findViewById(R.id.save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text.getText().toString().isEmpty()){
                    text.setError(text.getHint());
                    return;
                }
                save_course(text);
                mBottomSheetDialog.cancel();


            }
        });
        ImageView close=sheetView.findViewById(R.id.close_btn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.cancel();



            }
        });


    }

}

