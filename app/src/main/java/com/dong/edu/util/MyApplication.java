package com.dong.edu.util;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyApplication extends Application {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser mCurrentUser;
    //TODO if we need a singleton?

}
