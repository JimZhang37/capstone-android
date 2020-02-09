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
import com.dong.edu.util.AdapterDaysList;
import com.firebase.ui.auth.AuthUI;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private ListenerRegistration registration;
    private ListenerRegistration registration2;

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

    private void onSignInInitilizer(String name, String id) {
        mUserName = name;
        mUID = id;
        attachDatabaseReadListener();

    }

    private void onSignOutCleanUp() {
        mUID = null;
        mUserName = "";
        detachDatabaseReadListener();
    }


    private void attachDatabaseReadListener() {
        //setup listener for days' adapter
        CollectionReference query = db.collection(mUID)
                .document(mDocumentID)
                .collection("Days");
//                .whereEqualTo("pictureUri", true);
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen failed" + e.getMessage());
                    return;
                }
                List<Day> days = new ArrayList<>();
                List<String> docIds = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    docIds.add(doc.getId());
                    days.add(doc.toObject(Day.class));
                }

                Collections.reverse(days);
                Collections.reverse(docIds);
                adapter.setupData(days, docIds);

                Log.d(TAG, "Listen Successed");
            }
        });

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
                    dataBinding.setSprint(mSprint);
                    switch (mSprint.getmStatus()) {
                        case 1:
                            dataBinding.materialButton.setVisibility(View.VISIBLE);
                            dataBinding.btnEvaluate.setVisibility(View.GONE);
                            return;
                        case 3:
                            dataBinding.materialButton.setVisibility(View.GONE);
                            dataBinding.btnEvaluate.setVisibility(View.GONE);
                            return;
                        case 4:
                            dataBinding.materialButton.setVisibility(View.GONE);
                            dataBinding.btnEvaluate.setVisibility(View.GONE);
                            return;

                        case 2:
                            dataBinding.materialButton.setVisibility(View.GONE);
                            dataBinding.btnEvaluate.setVisibility(View.VISIBLE);
                            return;
                        default:
                            dataBinding.materialButton.setVisibility(View.VISIBLE);
                            dataBinding.btnEvaluate.setVisibility(View.GONE);
                            return;
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    private void detachDatabaseReadListener() {
        registration.remove();
        registration2.remove();
    }

    /**
     * Go to DayAddActivity to add a new day for Today
     *
     * @param view
     */
    public void openAddDay(View view) {
        if (mSprint == null || mSprint.dayNumber() == null) {
            Snackbar.make(dataBinding.coordinatorSprintDetail, "Please wait", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DayAddActivity.class);
        intent.putExtra(INTENT_SPRINT_ID_DETAIL, mDocumentID);
        intent.putExtra(INTENT_SPRINT_DAY_NUM, mSprint.dayNumber());
        startActivity(intent);
    }

    /**
     * Go to DayDetailActivity to show the details of the chosen day
     *
     * @param position
     */
    @Override
    public void onDayItemClick(int position) {
        Intent intent = new Intent(this, DayDetailActivity.class);
        intent.putExtra(INTENT_SPRINT_ID_DETAIL, mDocumentID);
        intent.putExtra(INTENT_INT_POSITION, position);
        startActivity(intent);
    }

    public void evaluateSprint(View view){
        Intent intent = new Intent(this, SprintEvaluateActivity.class);
        intent.putExtra(INTENT_SPRINT_ID_DETAIL,mDocumentID);
        startActivity(intent);
    }
}
