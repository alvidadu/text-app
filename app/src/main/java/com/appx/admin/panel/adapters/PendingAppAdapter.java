package com.appx.admin.panel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.appx.admin.panel.R;
import com.appx.admin.panel.models.AppModel;

import java.util.List;

public class PendingAppAdapter extends RecyclerView.Adapter<PendingAppAdapter.ViewHolder> {

    public interface OnAppClickListener {
        void onAppClick(AppModel app);
    }

    private final List<AppModel> apps;
    private final OnAppClickListener listener;

    public PendingAppAdapter(List<AppModel> apps, OnAppClickListener listener) {
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = apps.get(position);
        holder.tvName.setText(app.getName() != null ? app.getName() : "Unknown");
        holder.tvDeveloper.setText(app.getDeveloperName() != null ? app.getDeveloperName() : "");
        holder.tvStatus.setText(app.getStatus() != null ? app.getStatus().toUpperCase() : "");
        if (app.getIcon() != null && !app.getIcon().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(app.getIcon())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.ivIcon);
        } else {
            holder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAppClick(app);
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvDeveloper, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivAppIcon);
            tvName = itemView.findViewById(R.id.tvAppName);
            tvDeveloper = itemView.findViewById(R.id.tvDeveloper);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
