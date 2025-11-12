package com.example.hikingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> hike_id, hike_name, hike_location, hike_date, hike_length, hike_level, hike_available, hike_description;
    CustomAdapter(Context context, ArrayList<String> hike_id, ArrayList<String> hike_name,
                  ArrayList<String> hike_location, ArrayList<String> hike_date,
                  ArrayList<String> hike_length, ArrayList<String> hike_level,
                  ArrayList<String> hike_available, ArrayList<String> hike_description) {

        this.context = context;
        this.hike_id = hike_id;
        this.hike_name = hike_name;
        this.hike_location = hike_location;
        this.hike_date = hike_date;
        this.hike_length = hike_length;
        this.hike_level = hike_level;
        this.hike_available = hike_available;
        this.hike_description = hike_description;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hike, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.hike_name_txt.setText(hike_name.get(position));
        holder.hike_location_txt.setText(hike_location.get(position));
        holder.hike_date_txt.setText(hike_date.get(position));
        holder.hike_length_txt.setText(hike_length.get(position));
        holder.hike_level_txt.setText(hike_level.get(position));
        holder.hike_available_txt.setText(hike_available.get(position));

        String desc = hike_description.get(position);

        if (desc == null || desc.trim().isEmpty() || desc.equalsIgnoreCase("null")) {
            holder.hike_description_txt.setVisibility(View.GONE);
        } else {
            holder.hike_description_txt.setVisibility(View.VISIBLE);
            holder.hike_description_txt.setText(desc);
        }
        String level = hike_level.get(position);

        holder.hike_level_txt.setText(level);
        switch (level.toLowerCase()) {
            case "easy":
                holder.hike_level_txt.setBackground(createLevelBackground(Color.parseColor("#C8E6C9")));
                holder.hike_level_txt.setTextColor(Color.parseColor("#1B5E20"));
                break;
            case "moderate":
                holder.hike_level_txt.setBackground(createLevelBackground(Color.parseColor("#FFF3CD")));
                holder.hike_level_txt.setTextColor(Color.parseColor("#856404"));
                break;
            case "very hard":
                holder.hike_level_txt.setBackground(createLevelBackground(Color.parseColor("#F8D7DA")));
                holder.hike_level_txt.setTextColor(Color.parseColor("#721C24"));
                break;
            default:
                holder.hike_level_txt.setBackground(createLevelBackground(Color.parseColor("#E0E0E0")));
                holder.hike_level_txt.setTextColor(Color.BLACK);
                break;
        }
        holder.btnDeleteHike.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                String id = hike_id.get(holder.getAdapterPosition());
                deleteClickListener.onDeleteClick(id);
            }
        });
        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("id", hike_id.get(position));
            intent.putExtra("name", hike_name.get(position));
            intent.putExtra("location", hike_location.get(position));
            intent.putExtra("date", hike_date.get(position));
            intent.putExtra("length", hike_length.get(position));
            intent.putExtra("level", hike_level.get(position));
            intent.putExtra("available", hike_available.get(position));
            intent.putExtra("description", hike_description.get(position));
            context.startActivity(intent);
        });
    }
    private GradientDrawable createLevelBackground(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(backgroundColor);
        shape.setCornerRadius(20f);
        return shape;
    }

    @Override
    public int getItemCount() {
        return hike_id.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView hike_name_txt, hike_location_txt, hike_date_txt, hike_length_txt,
                hike_level_txt, hike_available_txt, hike_description_txt;
        MaterialButton btnDeleteHike, btnViewDetails;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            hike_name_txt = itemView.findViewById(R.id.hike_name_txt);
            hike_location_txt = itemView.findViewById(R.id.hike_location_txt);
            hike_date_txt = itemView.findViewById(R.id.hike_date_txt);
            hike_length_txt = itemView.findViewById(R.id.hike_length_txt);
            hike_level_txt = itemView.findViewById(R.id.hike_level_txt);
            hike_available_txt = itemView.findViewById(R.id.hike_available_txt);
            hike_description_txt = itemView.findViewById(R.id.hike_description_txt);

            btnDeleteHike = itemView.findViewById(R.id.btnDeleteHike);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
    public void updateData(ArrayList<String> id,
                           ArrayList<String> name,
                           ArrayList<String> location,
                           ArrayList<String> date,
                           ArrayList<String> length,
                           ArrayList<String> level,
                           ArrayList<String> available,
                           ArrayList<String> description) {
        this.hike_id.clear();
        this.hike_id.addAll(id);

        this.hike_name.clear();
        this.hike_name.addAll(name);

        this.hike_location.clear();
        this.hike_location.addAll(location);

        this.hike_date.clear();
        this.hike_date.addAll(date);

        this.hike_length.clear();
        this.hike_length.addAll(length);

        this.hike_level.clear();
        this.hike_level.addAll(level);

        this.hike_available.clear();
        this.hike_available.addAll(available);

        this.hike_description.clear();
        this.hike_description.addAll(description);

        notifyDataSetChanged();
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(String hikeId);
    }
    private OnDeleteClickListener deleteClickListener;
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

}
