package com.dong.edu.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dong.edu.R;
import com.dong.edu.data.Day;
import com.dong.edu.databinding.ViewholderDayBinding;
import com.dong.edu.databinding.ViewholderDetailDayBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;


public class AdapterPager2 extends RecyclerView.Adapter<ViewHolderDetailDay> {

    private List<DocumentSnapshot> mDocumentList;

    public void updateData(List<DocumentSnapshot> documents) {
        mDocumentList = documents;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDetailDay holder, int position) {
        Day day = mDocumentList.get(position).toObject(Day.class);
        holder.bindData(day);
    }

    @NonNull
    @Override
    public ViewHolderDetailDay onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderDetailDayBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.viewholder_detail_day, parent, false);
        return new ViewHolderDetailDay(binding);
    }

    @Override
    public int getItemCount() {
        if(mDocumentList == null ){return 0;}
        return mDocumentList.size();
    }
}
