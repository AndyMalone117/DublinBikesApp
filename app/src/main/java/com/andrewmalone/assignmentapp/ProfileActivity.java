package com.andrewmalone.assignmentapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import static com.andrewmalone.assignmentapp.R.id.btnGoToList;

public class ProfileActivity extends AppCompatActivity {

    private static final int SELECTED_IMG = 1;
    ImageView imgView;
    Button btn;

    private static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};


    static final int CAMERA_REQUEST = 1;

//    final FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference ref = database.getReference("server/saving-data/AssignmentApp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgView = (ImageView) findViewById(R.id.imgView);

        Button btnGoToList = (Button) findViewById(R.id.btnGoToList);

        btn = (Button) findViewById(R.id.selectPictureButton);

        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        File file = getFile();
//                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }

                }
        );

        btnGoToList.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), ListActivity.class);
                        startActivity(myIntent);
                    }
                }
        );
    }

    public static Intent createIntent(Context context) {
        Intent myIntent = new Intent(context, ProfileActivity.class);
        return myIntent;
    }

    public void btnClick(View view) {
        ActivityCompat.requestPermissions(this,
                PERMISSIONS_STORAGE, 0);
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            imgView.setImageBitmap(imageBitmap);
        }
    }
}
