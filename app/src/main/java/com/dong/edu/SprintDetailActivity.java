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

import com.dong.edu.data.Sprint;
import com.dong.edu.databinding.ActivityDetailBinding;
import com.dong.edu.util.AdapterDaysList;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class SprintDetailActivity extends AppCompatActivity implements AdapterDaysList.DayClickListener {
    private static final String TAG = SprintDetailActivity.class.getSimpleName();
    public static final String INTENT_SPRINT_ID_DETAIL = "SPRINT_UID_DETAIL";
    public static final String INTENT_INT_POSITION = "INTENT_INT_POSITION";
    private String mUID;
    private ActivityDetailBinding dataBinding;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    private String mUserName;
    private FirebaseFirestore db;
    private String mDocumentID;
    private Sprint mSprint;
    private FirebaseUser mCurrentUser;
    public static final String INTENT_SPRINT_DAY_NUM = "INTENT_SPRINT_DAY_NUM";
    private AdapterDaysList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);


        mDocumentID = getIntent().getStringExtra(SprintListActivity.INTENT_SPRINT_ID);
        dataBinding.setDocumentID(mDocumentID);

        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setSupportActionBar(dataBinding.toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Detailed Sprint");

        adapter = new AdapterDaysList(this);
        dataBinding.recyclerDays.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mUID = mCurrentUser.getUid();
        getData();
        getDays();
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
                        Toast.makeText(SprintDetailActivity.this, "user is signed in!", Toast.LENGTH_SHORT).show();

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

    private void getData() {
        if (mUID != null) {
            db.collection(mUID).document(mDocumentID).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    mSprint = document.toObject(Sprint.class);
                                    dataBinding.setSprint(mSprint);
                                } else {

                                    Log.d(TAG, "No such document");
                                }

                            } else {

                                Log.d(TAG, "get failed with ", task.getException());
                            }

                        }
                    });

        }
    }

    public void openAddDay(View view){
        if(mSprint == null || mSprint.getDayNumber() == null){
            Snackbar.make(dataBinding.coordinatorSprintDetail, "Please wait",Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DayAddActivity.class);
        intent.putExtra(INTENT_SPRINT_ID_DETAIL,mDocumentID);
        intent.putExtra(INTENT_SPRINT_DAY_NUM, mSprint.getDayNumber());
        startActivity(intent);
    }

    private void getDays() {
        if (mUID != null) {
            db.collection(mUID)
                    .document(mDocumentID)
                    .collection("Days")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                adapter.setupData(task.getResult().getDocuments());

                            }
                        }
                    });
        }
    }

    @Override
    public void onDayItemClick(int position) {
        Intent intent = new Intent(this, DayDetailActivity.class);
        intent.putExtra(INTENT_SPRINT_ID_DETAIL,mDocumentID);
        intent.putExtra(INTENT_INT_POSITION, position);
        startActivity(intent);
    }
}