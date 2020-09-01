package com.kh_sof_dev.learneasy.Popup;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kh_sof_dev.learneasy.R;
import com.kh_sof_dev.learneasy.modul.Level;
import com.kh_sof_dev.learneasy.modul.ResizePickedImage;

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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class AddNew_popup {
    public interface Listenner{
        void Go(Double price);
        void Cancel();
    }

//    ///images
//    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//
//    private void imageBrowse(int PICK_IMAGE_REQUEST,final Context mcontext) {
//        if (EasyPermissions.hasPermissions(mcontext.getApplicationContext(), galleryPermissions)) {
//            Intent pickerPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//            mcontext.startActivityForResult(pickerPhotoIntent, PICK_IMAGE_REQUEST);
//
//
//        } else {
//            EasyPermissions.requestPermissions(this, "Access for storage",
//                    1000, galleryPermissions);
//        }
//
//    }
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//
//            Uri returnUri = data.getData();
//            ResizePickedImage resizePickedImage = new ResizePickedImage();
//            String realePath = resizePickedImage.getRealPathFromURI(returnUri, this);
//            System.out.println(realePath);
//            String compresedImagePath;
//            Bitmap bitmapImage = null;
//            try {
//                Uri contentURI = data.getData();
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
//                BitmapDrawable background = new BitmapDrawable(bitmap);
//
//
//                compresedImagePath = resizePickedImage.resizeAndCompressImageBeforeSend
//                        (this, realePath, "image"+requestCode);
//               mPath=compresedImagePath;
//
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    }
//    String mPath;
//    private void save_info(TextView name) {
//
//        if (name.getText().toString().isEmpty()){
//            name.setError(name.getHint());
//            return;
//        }
//
//        Level level=new Level();
//        level.setName(name.getText().toString());
//
//        //////*************************Loding*****************************/
//        final ProgressDialog dialog=new ProgressDialog(this);
//        dialog.setTitle("Loading");
//        final String msg="wait a minute please ...!";
//        dialog.setMessage(msg);
//        dialog.show();
//
//
//FirebaseDatabase database=FirebaseDatabase.getInstance();
//        database = FirebaseDatabase.getInstance();
//      final DatabaseReference  myRef = database.getReference()
//                .child("Levels")
//                ;
//      FirebaseStorage storage = FirebaseStorage.getInstance();
//        final String key = myRef.push().getKey();
//        myRef.child(key).setValue(level);
//                System.out.println("Start upload images");
//                InputStream stream = null;
//                try {
//                    stream = new FileInputStream(new File(mPath));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                final StorageReference ref = storage.getReference().child("levelsImage").child(key).child(level.getImg() + ".jpg");
//
//                final UploadTask uploadTask = ref.putStream(stream);
//
//                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
////
//                        double progress = (-100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//
//                        Log.d(TAG, "onProgress: " + progress);
//                        System.out.println("Upload is " + progress + "% done");
//                        dialog.setMessage(msg+"\n"+
//                                " Uploaded " + progress + "% ");
//
//                    }
//                });
//                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        // GET THE IMAGE DOWNLOAD URL
//
//                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                System.out.println("uri " + uri.toString());
////                                uploadingImagePB.setProgress(0);
////                                progressBarLayout.setVisibility(View.GONE);
//
//                                Log.d(TAG, "onSuccess: the image uploaded " + uri.toString());
//                                Map<String, Object> map = new HashMap<>();
//                                map.put("imagesURL", uri.toString());
//                                myRef.child(key).updateChildren(map);
//                                /********************** finsh****************/
//                                dialog.dismiss();
//                                Toast.makeText(Add_new.this,
//                                        "Done with Successfully  ..!"
//                                        , Toast.LENGTH_LONG).show();
////
//
//                            }
////                            System.out.println(" END OF THE ON OnSuccessListener");
//                        });
//
//                    }
//                });
//
//
//
//    }
//    Bitmap bitmap=null;
//    String TAG="uploaded";
//    private static final int PICK_IMAGE_REQUEST =1 ;
//    public void NewLevel_pop(final Context mcontext, final String orderID, final Listenner listenner){
//
//        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mcontext);
//        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(LAYOUT_INFLATER_SERVICE);
//        final View sheetView = inflater.inflate(R.layout.popup_addlevel, null);
//        mBottomSheetDialog.setContentView(sheetView);
//        mBottomSheetDialog.show();
//        final EditText text=sheetView.findViewById(R.id.name);
//        final EditText upload=sheetView.findViewById(R.id.uploadimg);
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (text.getText().toString().isEmpty()){
//                    text.setError(text.getHint());
//                    return;
//                }
//
//
//
//            }
//        });
//
//        Button save=sheetView.findViewById(R.id.save_btn);
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (text.getText().toString().isEmpty()){
//                    text.setError(text.getHint());
//                    return;
//                }
//
//
//
//            }
//        });
//        Button close=sheetView.findViewById(R.id.close_btn);
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               mBottomSheetDialog.cancel();
//
//
//
//            }
//        });
//
//
//    }
}
