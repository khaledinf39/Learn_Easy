package com.kh_sof_dev.learneasy.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kh_sof_dev.learneasy.Fragments.Users_frg;
import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.modul.Course;
import com.kh_sof_dev.learneasy.modul.Level;
import com.kh_sof_dev.learneasy.modul.ResizePickedImage;
import com.kh_sof_dev.learneasy.Fragments.HomeFragment;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int My_level=1;
    public static boolean isAdmin=false;
    private AppBarConfiguration mAppBarConfiguration;
    private SlidingRootNav nav;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /************************* Drawer*******************/
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
         fab = findViewById(R.id.fab);
        nav= new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withDragDistance(250)
                .withRootViewScale(1f)
                .withMenuOpened(false)
                .withGravity(SlideGravity.LEFT)
                .withMenuLayout(R.layout.navigation_content)
                .inject();
        View navigation = findViewById(R.id.nav_view);
        TextView Home = navigation.findViewById(R.id.home);
        TextView Student = navigation.findViewById(R.id.student);
        TextView logout = navigation.findViewById(R.id.logout);
        fab.setOnClickListener(this);
        Home.setOnClickListener(this);
        Student.setOnClickListener(this);
        logout.setOnClickListener(this);
if (!MainActivity.isAdmin){
    Student.setVisibility(View.GONE);
}

        switchFGM(new HomeFragment());
        /////user information
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
       if (user!=null) {
           TextView user_name = navigation.findViewById(R.id.user_name);
           TextView phone = navigation.findViewById(R.id.phone);
           user_name.setText(user.getDisplayName());
           phone.setText(user.getPhoneNumber());
       }
       else {
           logout.setVisibility(View.GONE);
       }


    }
    public static FragmentTransaction transaction;
    public  void switchFGM(Fragment fragment) {
        MainActivity.transaction = getSupportFragmentManager().beginTransaction();
        MainActivity.transaction.replace(R.id.mainContainer, fragment);
        MainActivity.transaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    ////////////////////////////////Add new Level //////////////////////////////////////////////
    ///images
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private void imageBrowse(int PICK_IMAGE_REQUEST) {
        if (EasyPermissions.hasPermissions(getApplicationContext(), galleryPermissions)) {
            Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

          startActivityForResult(pickerPhotoIntent, PICK_IMAGE_REQUEST);


        } else {
            EasyPermissions.requestPermissions(this, "Access for storage",
                    1000, galleryPermissions);
        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

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
            Toast.makeText(MainActivity.this,
                    "Select image please  ..!"
                    , Toast.LENGTH_LONG).show();
            return;
        }
        if (mPath_sound==null){
            Toast.makeText(MainActivity.this,
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
                        Toast.makeText(MainActivity.this,
                                "Done with Successfully  ..!"
                                , Toast.LENGTH_LONG).show();
                        mPath_sound=null;

                    }

                });

            }
        });
    }


    private void save_Level(TextView name) {

        if (name.getText().toString().isEmpty()){
            name.setError(name.getHint());
            return;
        }
        if (mPath==null){
            Toast.makeText(MainActivity.this,
                    "Select image please  ..!"
                    , Toast.LENGTH_LONG).show();
            return;
        }
        final Level level=new Level();
        level.setName(name.getText().toString());

        //////*************************Loding*****************************/
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Loading");
        final String msg="wait a minute please ...!";
        dialog.setMessage(msg);
        dialog.show();


        FirebaseDatabase database=FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference()
                .child("Levels")
                ;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final String key = myRef.push().getKey();

        System.out.println("Start upload images");
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(mPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final StorageReference ref = storage.getReference().child("levelsImage").child(level.getName() + ".jpg");

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
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("img", uri.toString());
//                        myRef.child(key).updateChildren(map);
                        level.setImg(uri.toString());
                        myRef.child(key).setValue(level);
                        /********************** finsh****************/
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,
                                "Done with Successfully  ..!"
                                , Toast.LENGTH_LONG).show();
                        mPath=null;

                    }

                });

            }
        });



    }

    Bitmap bitmap=null;
    String TAG="uploaded";
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int PICK_Audio_REQUEST =2 ;
    public void NewLevel_pop(final Context mcontext){

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mcontext);
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View sheetView = inflater.inflate(R.layout.popup_addlevel, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        final EditText text=sheetView.findViewById(R.id.name);
        final Button upload=sheetView.findViewById(R.id.uploadimg);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBrowse(PICK_IMAGE_REQUEST);
                Log.d("upload","upoading");
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

                save_Level(text);
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
    public void NewCourse_pop(final Context mcontext){

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mcontext);
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View sheetView = inflater.inflate(R.layout.popup_addcourse, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        final EditText text=sheetView.findViewById(R.id.name);
        final EditText uploadimg=sheetView.findViewById(R.id.uploadimg);
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



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home:{
                switchFGM (new HomeFragment());
                fab.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.student:{
                switchFGM (new Users_frg());
                fab.setVisibility(View.GONE);
                break;
            }
            case R.id.logout:{
               FirebaseAuth auth=FirebaseAuth.getInstance();
               auth.signOut();
               finish();
                break;
            }
            case R.id.fab:{
                NewLevel_pop(MainActivity.this);
                break;
            }
        }
    }
}
