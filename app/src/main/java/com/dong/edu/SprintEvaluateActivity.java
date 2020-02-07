package com.dong.edu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dong.edu.data.Day;
import com.dong.edu.data.Sprint;
import com.dong.edu.databinding.ActivityDetailBinding;
import com.dong.edu.databinding.ActivitySprintEvaluateBinding;
import com.dong.edu.util.AdapterDaysList;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.dong.edu.SprintDetailActivity.INTENT_SPRINT_ID_DETAIL;

public class SprintEvaluateActivity extends AppCompatActivity {
    private ActivitySprintEvaluateBinding dataBinding;
    private static final String TAG = SprintDetailActivity.class.getSimpleName();

    private String mUID;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    private String mUserName;
    private FirebaseFirestore db;
    private String mDocumentID;
    private Sprint mSprint;
    private FirebaseUser mCurrentUser;
    private AdapterDaysList adapter;
    private ListenerRegistration registration;
    private ListenerRegistration registration2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_sprint_evaluate);

        mDocumentID = getIntent().getStringExtra(INTENT_SPRINT_ID_DETAIL);
        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setSupportActionBar(dataBinding.toolbarEvaluate);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Evaluate Sprint");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(SprintEvaluateActivity.this, "user is signed in!", Toast.LENGTH_SHORT).show();

                    onSignInInitilizer(user.getUid());

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


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
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

    private void onSignInInitilizer(String uid) {
        mUID = uid;
        attachDatabaseReadListener();
    }

    private void onSignOutCleanUp() {
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {


        //setup listener for mSprint;
        DocumentReference docRef = db.collection(mUID).document(mDocumentID);
        registration2 = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    mSprint = snapshot.toObject(Sprint.class);
                    dataBinding.setMySprint(mSprint);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    private void detachDatabaseReadListener() {

        registration2.remove();
    }

    public void evaluateClick(View view){
        if(mSprint.getEvaluation() == "" ){
            Snackbar.make(dataBinding.coordinatorEvaluate,"Please input values", Snackbar.LENGTH_SHORT).show();
            return;
        }
        mSprint.setmStatus(3);
        db.collection(mUID)
                .document(mDocumentID)
                .set(mSprint)
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

}
