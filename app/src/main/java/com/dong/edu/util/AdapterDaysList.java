package com.dong.edu.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dong.edu.R;
import com.dong.edu.data.Day;
import com.dong.edu.databinding.ViewholderDayBinding;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AdapterDaysList extends RecyclerView.Adapter<ViewHolderDay> {

//    private List<DocumentSnapshot> mDocumentList;
    private DayClickListener mListener;

    private List<Day> mDays;
    private List<String> mDocID;

    public void setupData(List<Day> days, List<String> docs){
        mDays = days;
        mDocID = docs;
//        mDocumentList = list;
        notifyDataSetChanged();
    }

    public AdapterDaysList(DayClickListener mListener) {
        this.mListener = mListener;
    }

    public interface DayClickListener{
        void onDayItemClick(int position);
    }

    @NonNull
    @Override
    public ViewHolderDay onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewholderDayBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.viewholder_day,parent,false);
        return new ViewHolderDay(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDay holder, int position) {
        Day day = mDays.get(position);
//        DocumentSnapshot Do = mDocumentList.get(position);
        holder.bindData(day,position,mListener);
    }

    @Override
    public int getItemCount() {
        if(mDays ==null) {return 0;}
        return mDays.size();
    }


}
