package com.dong.edu.util;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dong.edu.NewAppWidget;
import com.dong.edu.data.Sprint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FetchDataFromFirebaseService extends IntentService {

    public static final String ACTION_FETCH_DATA = "com.yy.action.fetch.data";

    public FetchDataFromFirebaseService() {
        super("FetchDataFromFirebaseService");
    }

    public static void startActionFetchData(Context context){
        Intent intent = new Intent(context,FetchDataFromFirebaseService.class);
        intent.setAction(ACTION_FETCH_DATA);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            String action = intent.getAction();
            if(action.equals(ACTION_FETCH_DATA)){
                fun();
            }
        }

    }

    private void fun(){
        String mUID;
        try{
            long currentTime = Calendar.getInstance().getTimeInMillis();
            mUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Query query = db.collection(mUID)
                    .whereLessThan("mStartDate", currentTime)
                    .orderBy("mStartDate")
                    .orderBy("mEndDate").limit(3);
            ListenerRegistration registration = query
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {

                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d("II", "Listen failed");
                                return;
                            }
                            List<Sprint> sprints = new ArrayList<>();
                            List<String> docIds = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                sprints.add(doc.toObject(Sprint.class));
                                docIds.add(doc.getId());

                            }
                            int message = 0;
                            if(sprints.size()<3)  message = 3-sprints.size();
                            for(int i = 0; i < message; i++)
                            {
                                sprints.add(new Sprint());
                            }
                            Log.d("ii", "Listen Successed");
                            Context context = getApplicationContext();
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
                            NewAppWidget.updateWidget(context,appWidgetManager,sprints,appWidgetIds);

                        }
                    });
        }
        catch (Exception e){
            Log.e("Get Current User", e.toString());
        }


    }
}
