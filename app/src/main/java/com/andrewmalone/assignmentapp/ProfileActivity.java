package com.andrewmalone.assignmentapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.data;
import static android.R.attr.name;


public class ProfileActivity extends AppCompatActivity {

    private static final int SELECTED_IMG = 1;
    ImageView imgView;
    Button btn;
    EditText name;

    FirebaseUser user;


    private static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};


    static final int CAMERA_REQUEST = 1;
    private Uri downloadUrl;

//    final FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference ref = database.getReference("server/saving-data/AssignmentApp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        name = (EditText) findViewById(R.id.nameText);

        if (user.getPhotoUrl() != null) {
            downloadUrl = user.getPhotoUrl();
        }

        if (user.getDisplayName() != null) {
            name.setText(user.getDisplayName());
        }


        //loading the image from internal storage
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir/profile.jpg
        File directory = cw.getDir("image_dir", Context.MODE_PRIVATE);

        String pathabs = directory.getAbsolutePath();
        loadImageFromStorage(pathabs);

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
                    public void onClick(final View view) {
//                        if (downloadUrl == null) {
//                            Toast.makeText(ProfileActivity.this, "Please add a picture.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        //Setting the users Display name and image
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name.getText().toString())
                                .setPhotoUri(Uri.parse(downloadUrl.toString()))
                                .build();

                        //updating the users profile
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Intent myIntent = new Intent(view.getContext(), ListActivity.class);
                                            startActivity(myIntent);

                                            Log.d("", "User profile updated.");
                                        }
                                    }
                                });
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        user = FirebaseAuth.getInstance().getCurrentUser();

        name.setText(user.getDisplayName());
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
            final Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            saveToInternalStorage(imageBitmap);

            imgView.setImageBitmap(imageBitmap);

            //Saving image to firebase storage referenced from firebase docs.
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference to "mountains.jpg"
            StorageReference imageRef = storageRef.child("mountains.jpg");

            // Create a reference to 'images/mountains.jpg'
            StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

            // While the file names are the same, the references point to different files
            imageRef.getName().equals(mountainImagesRef.getName());    // true
            imageRef.getPath().equals(mountainImagesRef.getPath());    // false
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("", downloadUrl.toString());

                }
            });
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir/profile.jpg
        File directory = cw.getDir("image_dir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imgView);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }


}
