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
import com.appx.admin.panel.models.DeveloperModel;

import java.util.List;

public class VerifyDeveloperAdapter extends RecyclerView.Adapter<VerifyDeveloperAdapter.ViewHolder> {

    public interface OnVerifyActionListener {
        void onToggleVerify(DeveloperModel dev, boolean currentlyVerified);
    }

    private final List<DeveloperModel> developers;
    private final OnVerifyActionListener listener;

    public VerifyDeveloperAdapter(List<DeveloperModel> developers, OnVerifyActionListener listener) {
        this.developers = developers;
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
        DeveloperModel dev = developers.get(position);
        holder.tvName.setText(dev.getName() != null ? dev.getName() : "Unknown");
        holder.tvPackage.setText(dev.getId() != null ? "ID: " + dev.getId() : "");
        holder.tvDeveloper.setText(dev.getEmail() != null ? dev.getEmail() : "");
        holder.tvApprovalStatus.setText(dev.getPublishedApps() + " published apps");

        if (dev.getAvatar() != null && !dev.getAvatar().isEmpty()) {
            Glide.with(holder.itemView).load(dev.getAvatar()).placeholder(R.mipmap.ic_launcher).circleCrop().into(holder.ivIcon);
        } else {
            holder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        }

        boolean verified = dev.isDeveloperVerified();
        holder.ivVerifiedBadge.setVisibility(verified ? View.VISIBLE : View.GONE);
        holder.btnToggleVerify.setText(verified ? "Remove Badge" : "Verify Developer");
        holder.btnToggleVerify.setBackgroundTintList(
                ContextCompat.getColorStateList(holder.itemView.getContext(),
                        verified ? R.color.error : R.color.primary));

        holder.btnToggleVerify.setOnClickListener(v -> {
            if (listener != null) listener.onToggleVerify(dev, verified);
        });
    }

    @Override
    public int getItemCount() {
        return developers.size();
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
