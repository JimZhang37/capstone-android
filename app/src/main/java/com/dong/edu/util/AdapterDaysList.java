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

    private List<DocumentSnapshot> mDocumentList;


    public void setupData(List<DocumentSnapshot> list){
        mDocumentList = list;
        notifyDataSetChanged();
        Day day = list.get(0).toObject(Day.class);
    }

    @NonNull
    @Override
    public ViewHolderDay onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewholderDayBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.viewholder_day,parent,false);
        return new ViewHolderDay(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDay holder, int position) {
        Day day = mDocumentList.get(position).toObject(Day.class);
        holder.bindData(day);
    }

    @Override
    public int getItemCount() {
        if(mDocumentList ==null) {return 0;}
        return mDocumentList.size();
    }


}
