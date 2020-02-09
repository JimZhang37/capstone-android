package com.dong.edu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dong.edu.data.Day;
import com.dong.edu.databinding.ActivityAddDayBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DayAddActivity extends AppCompatActivity {
    private static final String TAG = DayAddActivity.class.getSimpleName();
    private String mDocumentId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    static final int REQUEST_IMAGE_CAPTURE = 3;
    private String mUserName;
    private String mUID;
    private ActivityAddDayBinding dataBinding;
    private Day mDay;
    private String mDayNumber;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_day);
        mDay = new Day();
        dataBinding.setMyDay(mDay);

        mDocumentId = getIntent().getStringExtra(SprintDetailActivity.INTENT_SPRINT_ID_DETAIL);
        mDayNumber = getIntent().getStringExtra(SprintDetailActivity.INTENT_SPRINT_DAY_NUM);
        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();


        setSupportActionBar(dataBinding.toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Create a New Day");

        dataBinding.spinner.setVisibility(View.GONE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            mAuthStateListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuthStateListener == null) {
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Toast.makeText(DayAddActivity.this, "user is signed in!", Toast.LENGTH_SHORT).show();

                        onSignInInitilizer(user.getDisplayName(), user.getUid());

                    } else {
                        onSignOutCleanUp();
                        // Choose authentication providers
                        List<AuthUI.IdpConfig> providers = Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build());

                        // Create and launch sign-in intent
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setAvailableProviders(providers)
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            };
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "loged in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "loggedin canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == RC_PHOTO_PICKER ) {
            if (resultCode == RESULT_OK) {
                //TODO progress bar, start
                dataBinding.spinner.setVisibility(View.VISIBLE);


                Uri selectedImage = data.getData();
                dataBinding.imageToUpload.setImageURI(selectedImage);
                StorageReference photoRef = storageReference.child(selectedImage.getLastPathSegment());
                photoRef.putFile(selectedImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            mDay.setPictureUri(downloadUri.toString());


                            //TODO progress bar stop, save downloaduri, handle activity lifecycle, etc.
                            dataBinding.spinner.setVisibility(View.GONE);

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE ) {
            if (resultCode == RESULT_OK) {
                //TODO progress bar, start
                dataBinding.spinner.setVisibility(View.VISIBLE);

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                dataBinding.imageToUpload.setImageBitmap(imageBitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data_image = baos.toByteArray();


                StorageReference photoRef = storageReference.child(String.valueOf(Calendar.getInstance().getTime().getTime()));

                photoRef.putBytes(data_image).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            mDay.setPictureUri(downloadUri.toString());


                            //TODO progress bar stop, save downloaduri, handle activity lifecycle, etc.
                            dataBinding.spinner.setVisibility(View.GONE);

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            }
        }

    }

    private void onSignInInitilizer(String name, String id) {
        mUserName = name;
        mUID = id;
        storageReference = mFirebaseStorage.getReference().child(mUID);
    }

    private void onSignOutCleanUp() {
        mUID = null;
        mUserName = "";
    }


    public void saveDayToCloud(View view) {
//        Log.d(TAG, "value of mySprint is " + mSprint.getSprintName() + mSprint.getSprintTime());
        if(mDay.getDescription() == "" || mDay.getPictureUri()==null
        || mDay.getPictureUri()==""|| mDay.getDescription()==null){
            Snackbar.make(dataBinding.coordinatorDay,"Please input values", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(DayAddActivity.this,"Pleae input",Toast.LENGTH_SHORT);
            return;
        }

        db.collection(mUID)
                .document(mDocumentId)
                .collection("Days")
                .document(mDayNumber)
                .set(mDay)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        finish();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void selectPicture(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);

    }

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
