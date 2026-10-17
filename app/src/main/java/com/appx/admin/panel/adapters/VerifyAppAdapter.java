package com.appx.admin.panel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.appx.admin.panel.R;
import com.appx.admin.panel.models.AppModel;
import com.appx.admin.panel.utils.Constants;

import java.util.List;

public class VerifyAppAdapter extends RecyclerView.Adapter<VerifyAppAdapter.ViewHolder> {

    public interface OnVerifyActionListener {
        void onToggleVerify(AppModel app, boolean currentlyVerified);
    }

    private final List<AppModel> apps;
    private final OnVerifyActionListener listener;

    public VerifyAppAdapter(List<AppModel> apps, OnVerifyActionListener listener) {
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verify_app, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = apps.get(position);
        holder.tvName.setText(app.getName() != null ? app.getName() : "Unknown");
        holder.tvPackage.setText(app.getPackageName() != null ? app.getPackageName() : "");
        holder.tvDeveloper.setText(app.getDeveloperName() != null ? app.getDeveloperName() : "");

        if (app.getIcon() != null && !app.getIcon().isEmpty()) {
            Glide.with(holder.itemView).load(app.getIcon()).placeholder(R.mipmap.ic_launcher).into(holder.ivIcon);
        } else {
            holder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        }

        boolean verified = app.isAppVerified();
        holder.ivVerifiedBadge.setVisibility(verified ? View.VISIBLE : View.GONE);
        holder.btnToggleVerify.setText(verified ? "Remove Badge" : "Give Verified Badge");
        holder.btnToggleVerify.setBackgroundTintList(
                ContextCompat.getColorStateList(holder.itemView.getContext(),
                        verified ? R.color.error : R.color.primary));

        String status = app.getStatus() != null ? app.getStatus() : "";
        holder.tvApprovalStatus.setText(status.toUpperCase());
        int statusColor = R.color.text_secondary;
        if (Constants.STATUS_APPROVED.equals(status)) statusColor = R.color.status_approved;
        else if (Constants.STATUS_PENDING.equals(status)) statusColor = R.color.status_pending;
        else if (Constants.STATUS_REJECTED.equals(status)) statusColor = R.color.status_rejected;
        else if (Constants.STATUS_SUSPENDED.equals(status)) statusColor = R.color.status_suspended;
        holder.tvApprovalStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), statusColor));

        holder.btnToggleVerify.setOnClickListener(v -> {
            if (listener != null) listener.onToggleVerify(app, verified);
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon, ivVerifiedBadge;
        TextView tvName, tvPackage, tvDeveloper, tvApprovalStatus;
        MaterialButton btnToggleVerify;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivVerifiedBadge = itemView.findViewById(R.id.ivVerifiedBadge);
            tvName = itemView.findViewById(R.id.tvName);
            tvPackage = itemView.findViewById(R.id.tvPackage);
            tvDeveloper = itemView.findViewById(R.id.tvDeveloper);
            tvApprovalStatus = itemView.findViewById(R.id.tvApprovalStatus);
            btnToggleVerify = itemView.findViewById(R.id.btnToggleVerify);
        }
    }
}
