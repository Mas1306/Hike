package com.example.hikingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> obs_id, obs_name, obs_date, obs_comment;
    public ObservationAdapter(Context context,
                              ArrayList<String> obs_id,
                              ArrayList<String> obs_name,
                              ArrayList<String> obs_date,
                              ArrayList<String> obs_comment) {

        this.context = context;
        this.obs_id = obs_id;
        this.obs_name = obs_name;
        this.obs_date = obs_date;
        this.obs_comment = obs_comment;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_observation, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.observation_name.setText(obs_name.get(position));
        holder.observation_time.setText(obs_date.get(position));
        String cmt = obs_comment.get(position);
        if (cmt == null || cmt.trim().isEmpty() || cmt.equalsIgnoreCase("null")) {
            holder.comment.setVisibility(View.GONE);
        } else {
            holder.comment.setVisibility(View.VISIBLE);
            holder.comment.setText(cmt);
        }
        holder.btnEditObservation.setOnClickListener(v -> {
            if (editClickListener != null) {
                String id = obs_id.get(holder.getAdapterPosition());
                editClickListener.onEditClick(
                        id,
                        obs_name.get(holder.getAdapterPosition()),
                        obs_date.get(holder.getAdapterPosition()),
                        obs_comment.get(holder.getAdapterPosition())
                );
            }
        });
        holder.btnDeleteObservation.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                String id = obs_id.get(holder.getAdapterPosition());
                deleteClickListener.onDeleteClick(id);
            }
        });
    }
    @Override
    public int getItemCount() {
        return obs_id.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView observation_name, observation_time, comment;
        MaterialButton btnEditObservation, btnDeleteObservation;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            observation_name = itemView.findViewById(R.id.observations_name);
            observation_time = itemView.findViewById(R.id.observations_time);
            comment = itemView.findViewById(R.id.comment);
            btnEditObservation = itemView.findViewById(R.id.btnViewDetails);
            btnDeleteObservation = itemView.findViewById(R.id.btnDeleteObservation);
        }
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(String obsId);
    }
    private OnDeleteClickListener deleteClickListener;
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }
    public interface OnEditClickListener {
        void onEditClick(String obsId, String name, String date, String comment);
    }
    private OnEditClickListener editClickListener;
    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }
}
