package com.appx.admin.panel.models;

import java.util.List;
import java.util.ArrayList;

public class AppModel {
    private String id;
    private String name;
    private String icon;
    private String developerId;
    private String developerName;
    private String category;
    private String description;
    private String packageName;
    private String versionName;
    private long versionCode;
    private long size;
    private String apkUrl;
    private List<String> screenshots;
    private float rating;
    private long reviewCount;
    private long downloadCount;
    private String status; // pending, approved, rejected, suspended
    private boolean featured;
    private boolean editorChoice;
    private boolean trending;
    private boolean appVerified;
    private String verifiedBy;
    private long verifiedAt;
    private long createdAt;
    private long updatedAt;
    private String website;
    private String privacyPolicy;
    private String changelog;
    private String rejectionReason;

    public AppModel() {
        screenshots = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDeveloperId() { return developerId; }
    public void setDeveloperId(String developerId) { this.developerId = developerId; }

    public String getDeveloperName() { return developerName; }
    public void setDeveloperName(String developerName) { this.developerName = developerName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }

    public long getVersionCode() { return versionCode; }
    public void setVersionCode(long versionCode) { this.versionCode = versionCode; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getApkUrl() { return apkUrl; }
    public void setApkUrl(String apkUrl) { this.apkUrl = apkUrl; }

    public List<String> getScreenshots() { return screenshots; }
    public void setScreenshots(List<String> screenshots) { this.screenshots = screenshots; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public long getReviewCount() { return reviewCount; }
    public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }

    public long getDownloadCount() { return downloadCount; }
    public void setDownloadCount(long downloadCount) { this.downloadCount = downloadCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isEditorChoice() { return editorChoice; }
    public void setEditorChoice(boolean editorChoice) { this.editorChoice = editorChoice; }

    public boolean isTrending() { return trending; }
    public void setTrending(boolean trending) { this.trending = trending; }

    public boolean isAppVerified() { return appVerified; }
    public void setAppVerified(boolean appVerified) { this.appVerified = appVerified; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public long getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(long verifiedAt) { this.verifiedAt = verifiedAt; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getPrivacyPolicy() { return privacyPolicy; }
    public void setPrivacyPolicy(String privacyPolicy) { this.privacyPolicy = privacyPolicy; }

    public String getChangelog() { return changelog; }
    public void setChangelog(String changelog) { this.changelog = changelog; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
