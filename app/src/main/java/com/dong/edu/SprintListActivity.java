package com.dong.edu;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;

import com.dong.edu.data.Sprint;
import com.dong.edu.databinding.ActivityMainBinding;
import com.dong.edu.util.AdapterSprintList;
import com.firebase.ui.auth.AuthUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class SprintListActivity extends AppCompatActivity implements AdapterSprintList.SprintClickListener {
    public static final int RC_SIGN_IN = 1;
    public static final String INTENT_SPRINT_ID = "SPRINT_UID";
    private static final String TAG = SprintListActivity.class.getSimpleName();

    private ActivityMainBinding dataBinding;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private FirebaseFirestore db;


    private AdapterSprintList adapter;
    private String mUID;
    private ListenerRegistration registration;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        adapter = new AdapterSprintList(this);
        dataBinding.recyclerView.setAdapter(adapter);

        mFirebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(dataBinding.toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Sprint List");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(SprintListActivity.this, "user is signed in!", Toast.LENGTH_SHORT).show();

                    onSignedInInitialize(user.getDisplayName(), user.getUid());

                } else {
                    onSignedOutCleanup();
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


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }


    private void onSignedInInitialize(String name, String id) {

        mUID = id;
//        getData();
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        mUID = null;
        adapter.setupData(null, null);
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        query = db.collection(mUID)
                .whereLessThan("mStartDate", currentTime)
                .orderBy("mStartDate")
                .orderBy("mEndDate");
        registration = query
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Listen failed");
                            return;
                        }
                        List<Sprint> sprints = new ArrayList<>();
                        List<String> docIds = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            sprints.add(doc.toObject(Sprint.class));
                            docIds.add(doc.getId());

                        }
                        adapter.setupData(sprints, docIds);
                        Log.d(TAG, "Listen Successed");
                    }
                });

    }

    private void detachDatabaseReadListener() {
        if (registration != null) {
            registration.remove();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                AuthUI.getInstance().signOut(this);
                break;
            case R.id.menu_add:
                Intent intent = new Intent(this, SprintAddActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onSprintItemClick(String key) {
        Intent intent = new Intent(this, SprintDetailActivity.class);
        intent.putExtra(INTENT_SPRINT_ID, key);
        startActivity(intent);
    }

    public void activateFAB(View view) {
        Intent intent = new Intent(this, SprintAddActivity.class);
        startActivity(intent);
    }
}

