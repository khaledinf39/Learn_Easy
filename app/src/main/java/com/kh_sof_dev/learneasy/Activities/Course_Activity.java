package com.kh_sof_dev.learneasy.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransitionImpl;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.adapter.Course_adapter;
import com.kh_sof_dev.learneasy.modul.Course;
import com.kh_sof_dev.learneasy.modul.ResizePickedImage;
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
        setContentView(R.layout.courses_activity);
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
ImageButton back;
    private void init() {
        RV=findViewById(R.id.RV);
        back=findViewById(R.id.back);
        RV.setLayoutManager(new LinearLayoutManager(mcontext,RecyclerView.VERTICAL,false));
        courseList=new ArrayList<>();
        adapter=new Course_adapter(mcontext, courseList, new Course_adapter.Selected_item() {
            @Override
            public void Onselcted(Course course,int position) {
Course_Details_pop(mcontext,position);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void OnPlay(Course course, int position) {
                sound_url=course.getSound();
                Log.d("sound_url",sound_url);
                if (sound_url!=null){
                    play_audio();
                }

            }

            @Override
            public void OnDelete(Course course, int position) {
                Delete_course(course);
            }
        });
        RV.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        if (!MainActivity.isAdmin){
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               NewCourse_pop(Course_Activity.this);
            }
        });
back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Finsh();
    }
});

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void Finsh() {
        stopPlaying();
        finish();
    }

    public interface goListenner{
        void Go();
        void Cancel();
    }
    public void Conferme_POP(final Context mContext, final goListenner listenner ){
        final Dialog dialog=new Dialog(mContext);
        dialog.setContentView(R.layout.popup_conf);
        Button conf=dialog.findViewById(R.id.confBtn);
        Button cancel=dialog.findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenner.Cancel();
                dialog.dismiss();
            }
        });
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenner.Go();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void Delete_course(final Course course) {

        Conferme_POP(mcontext, new goListenner() {
            @Override
            public void Go() {
                FirebaseDatabase database=FirebaseDatabase.getInstance();
                database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference()
                        .child("Levels").child(Course_Activity.level_id).child("Courses");
                myRef.child(course.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                          try {
                              StorageReference storageReference_img = FirebaseStorage.getInstance().getReference()
                                      .child(course.getImage());
                              storageReference_img.delete();
                          } catch (Exception e) {
                              e.printStackTrace();
                          }

                            try {
                                StorageReference storageReference_audio = FirebaseStorage.getInstance().getReference()
                                        .child(course.getSound());
                                storageReference_audio.delete();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            courseList.remove(course);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void Cancel() {

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
            mPath_sound=data.getData();
//            Uri file = data.getData();
//            Toast.makeText(mcontext,data.getData().getLastPathSegment(),Toast.LENGTH_SHORT).show();
        }


        }
    }
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
    String mPath=null;
    Uri mPath_sound=null;
    String sound_url=null;
    private void save_course(final TextView name) {

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

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final String key = myRef.push().getKey();




        //////*************************Loading  sound*****************************/
        System.out.println("Start upload sound");
        final StorageReference ref1 = storage.getReference().child("CourseSound").child(course.getName()+".mp3");

        final UploadTask uploadTask1 = ref1.putFile(mPath_sound);

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

                       sound_url=uri.toString();
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
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("sound", sound_url);
                                        map.put("name", name.getText().toString());
                                        map.put("image", uri.toString());
                                        myRef.child(key).updateChildren(map);
                                        mPath=null;
                                        sound_url=null;
                                        mPath_sound=null;
                                        /********************** finsh****************/
                                        dialog.dismiss();
                                        Toast.makeText(Course_Activity.this,
                                                "Done with Successfully  ..!"
                                                , Toast.LENGTH_LONG).show();


                                    }

                                });

                            }
                        });


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
    public void Course_Details_pop(final Context mcontext, final int position){

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.cource_details, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow POPDialog = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        POPDialog.showAtLocation(RV, Gravity.CENTER, 0, 0);

        /////Dispaly Course info
        final TextView name=popupView.findViewById(R.id.name);
        final ImageView img=popupView.findViewById(R.id.img);
        place_course_inf(name,img,courseList.get(position));
        final int[] MyPosition = {position};
        final ImageButton play=popupView.findViewById(R.id.sound);
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
            play_audio();
            }
        });
        final ImageButton next=popupView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if (MyPosition[0]+1<courseList.size()){
                   MyPosition[0] =MyPosition[0]+1;
                   place_course_inf(name,img,courseList.get(MyPosition[0]));
               }else {
                   Toast.makeText(mcontext," Congratulation you have complete this level with successfully "
                           ,Toast.LENGTH_SHORT).show();
                   FirebaseDatabase database=FirebaseDatabase.getInstance();
                   database = FirebaseDatabase.getInstance();
                   FirebaseAuth auth= FirebaseAuth.getInstance();
                   final DatabaseReference myRef = database.getReference()
                           .child("Users");

                   myRef.child(auth.getUid()).child("My_level").setValue(MainActivity.My_level+1);
                   POPDialog.dismiss();
               }
            }
        });
        ImageButton last=popupView.findViewById(R.id.last);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MyPosition[0]-1>=0){
                    MyPosition[0] = MyPosition[0]-1;
                    place_course_inf(name,img,courseList.get(MyPosition[0]));
                }

            }
        });

        ImageView close=popupView.findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                POPDialog.dismiss();

            }
        });
        ImageView delete=popupView.findViewById(R.id.delete);
        if (!MainActivity.isAdmin){
            delete.setVisibility(View.GONE);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_course(courseList.get(MyPosition[0]));
                POPDialog.dismiss();

            }
        });


    }

    Boolean isPLAYING=false;
    MediaPlayer mp=null;
    private void play_audio() {

        if (!isPLAYING) {
            isPLAYING = true;
             mp = new MediaPlayer();
            try {
                mp.setDataSource(sound_url);
                mp.prepare();
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        isPLAYING=false;
                    }
                });
            } catch (IOException e) {
                Log.e("Audio", "prepare() failed");
            }
        } else {
            isPLAYING = false;
            stopPlaying();
        }

    }
    private void stopPlaying() {
        if (mp!=null){
        mp.release();
        mp = null;
        }
    }

    private void place_course_inf(TextView name, ImageView img,Course course) {
        name.setText(course.getName());
        Picasso.with(mcontext)
                .load(course.getImage())
                .placeholder(R.drawable.ic_logo)
                .into(img);
        sound_url=course.getSound();
    }


}

