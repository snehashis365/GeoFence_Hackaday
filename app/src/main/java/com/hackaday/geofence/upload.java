package com.hackaday.geofence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class upload extends AppCompatActivity {

    ImageButton camera,browse;

    private static final int PERMISSION_CAMERA_CODE = 1000;
    private static final int PERMISSION_GALLERY_CODE = 1001;
    private static final int IMAGE_CAPTURE_CODE = 1002;
    private static final int GALLERY_SELECT_CODE = 1003;
    ImageView preview;

    Uri image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        camera=findViewById(R.id.btn_camera);
        browse=findViewById(R.id.btn_browse);
        preview=findViewById(R.id.preview);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                                    PackageManager.PERMISSION_DENIED) {
                        //permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
                        //show popup to request permissions
                        requestPermissions(permission, PERMISSION_CAMERA_CODE);
                    } else {
                        openCamera();

                    }
                } else {

                    openCamera();
                }
            }

        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CAMERA_CODE);
                    } else  {

                        selectFromGallery();

                    }
                } else {
                    selectFromGallery();
                }


            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "GeoFence Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private void selectFromGallery() {

        Intent select = new Intent(Intent.ACTION_PICK);
        select.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png"};
        select.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(select, GALLERY_SELECT_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses Allow or Deny from Permission Request Popup
        switch (requestCode){
            case PERMISSION_CAMERA_CODE:{
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera();
                }
                else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case PERMISSION_GALLERY_CODE:{
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){

                    selectFromGallery();
                }
                else {

                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image was captured from camera
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {

            case IMAGE_CAPTURE_CODE:{

                if (resultCode == RESULT_OK){
                    //set the image captured to our ImageView
                    preview.setImageURI(image_uri);
                }
                break;
            }

            case GALLERY_SELECT_CODE:{

                if (resultCode == RESULT_OK){

                    image_uri = data.getData();
                    preview.setImageURI(image_uri);
                }
                break;
            }


        }

    }
}
