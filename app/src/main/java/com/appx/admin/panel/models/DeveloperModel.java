package com.appx.admin.panel.models;

public class DeveloperModel {
    private String id;
    private String name;
    private String email;
    private String avatar;
    private String description;
    private String website;
    private boolean developerVerified;
    private String verifiedBy;
    private long verifiedAt;
    private String status; // active, suspended
    private int totalApps;
    private int publishedApps;
    private int pendingApps;
    private int rejectedApps;
    private long totalDownloads;
    private long createdAt;
    private long updatedAt;

    public DeveloperModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public boolean isDeveloperVerified() { return developerVerified; }
    public void setDeveloperVerified(boolean developerVerified) { this.developerVerified = developerVerified; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public long getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(long verifiedAt) { this.verifiedAt = verifiedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalApps() { return totalApps; }
    public void setTotalApps(int totalApps) { this.totalApps = totalApps; }

    public int getPublishedApps() { return publishedApps; }
    public void setPublishedApps(int publishedApps) { this.publishedApps = publishedApps; }

    public int getPendingApps() { return pendingApps; }
    public void setPendingApps(int pendingApps) { this.pendingApps = pendingApps; }

    public int getRejectedApps() { return rejectedApps; }
    public void setRejectedApps(int rejectedApps) { this.rejectedApps = rejectedApps; }

    public long getTotalDownloads() { return totalDownloads; }
    public void setTotalDownloads(long totalDownloads) { this.totalDownloads = totalDownloads; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
