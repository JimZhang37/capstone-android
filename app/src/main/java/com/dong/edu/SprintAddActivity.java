package com.dong.edu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.dong.edu.data.Sprint;

import com.dong.edu.databinding.ActivitySprintAddBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class SprintAddActivity extends AppCompatActivity  {
    public static final int RC_SIGN_IN = 1;
    public static final String TAG = SprintAddActivity.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db;
    private String mUserName;
    private String mUID;
    private ActivitySprintAddBinding dataBinding;
    private Sprint mSprint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_sprint_add);
//        setContentView(R.layout.activity_sprint_add);
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mSprint = new Sprint();
        dataBinding.setMySprint(mSprint);
        dataBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "value of mySprint is " + mSprint.getSprintName() + mSprint.getSprintTime());
                db.collection(mUID)
                        .add(mSprint)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                mSprint.setSprintName("");
                                mSprint.setSprintTime("");
                                dataBinding.setMySprint(mSprint);
                                Intent intent = new Intent(SprintAddActivity.this, SprintDetailActivity.class);
                                intent.putExtra(SprintListActivity.INTENT_SPRINT_ID,documentReference.getId());
                                startActivity(intent);
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
        });

        setSupportActionBar(dataBinding.toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Add New Sprint");
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
                        Toast.makeText(SprintAddActivity.this, "user is signed in!", Toast.LENGTH_SHORT).show();

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
    }

    private void onSignInInitilizer(String name, String id) {
        mUserName = name;
        mUID = id;

    }

    private void onSignOutCleanUp() {
        mUID = null;
        mUserName = "";
    }


    public void setupCalendar(View view){
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.dateRangePicker();

        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.show(getSupportFragmentManager(),picker.toString());

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override public void onPositiveButtonClick(Pair<Long,Long> selection) {
                mSprint.setmStartDate(selection.first);
                mSprint.setmEndDate(selection.second);
            }
        });

    }


}

