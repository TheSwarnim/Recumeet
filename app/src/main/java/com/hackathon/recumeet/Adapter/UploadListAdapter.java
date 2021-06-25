package com.hackathon.recumeet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.hackathon.recumeet.R;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

    public List<String> filenameList;
    public List<String> fileDoneList;

    public UploadListAdapter(List<String> filenameList, List<String> fileDoneList) {
        this.filenameList = filenameList;
        this.fileDoneList = fileDoneList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postphoto_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String fileName = filenameList.get(position);
        holder.fileNameView.setText(fileName);
        String fileDone = fileDoneList.get(position);

        if(fileDone.equals("uploading")){
            holder.fileDoneView.setImageResource(R.drawable.ic_dot);
        } else {
            holder.fileDoneView.setImageResource(R.drawable.ic_correct);
        }

    }

    @Override
    public int getItemCount() {
        return filenameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView fileNameView ;
        private final ImageView fileDoneView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileNameView  = itemView.findViewById(R.id.photo_name);
            fileDoneView  = itemView.findViewById(R.id.check);
        }
    }
}
