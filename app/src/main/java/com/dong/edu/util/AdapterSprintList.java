package com.dong.edu.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dong.edu.R;
import com.dong.edu.data.Sprint;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.ListIterator;

public class AdapterSprintList extends RecyclerView.Adapter<ViewHolderSprint> {

    public interface SprintClickListener{
        void onSprintItemClick(String key);
    }

//    private List<DocumentSnapshot> mList;
    private SprintClickListener mListener;

    private List<Sprint> mSprintList;
    private List<String> mDocList;

    public AdapterSprintList(SprintClickListener listener){
        mListener = listener;
    }

    public void setupData(List<Sprint> sprints, List<String> docIDs) {
        mSprintList = sprints;
        mDocList = docIDs;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSprint holder, int position) {

        String sprintID = mDocList.get(position);
        Sprint sprint = mSprintList.get(position);
        holder.bind(sprint, sprintID, mListener);
    }

    @NonNull
    @Override
    public ViewHolderSprint onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView layout = (MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sprint, parent, false);
        ViewHolderSprint holder = new ViewHolderSprint(layout);
        return holder;
    }

    @Override
    public int getItemCount() {
        if (mSprintList == null) {
            return 0;
        }
        return mSprintList.size();
    }
}
