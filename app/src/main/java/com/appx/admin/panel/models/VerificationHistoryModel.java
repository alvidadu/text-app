package com.appx.admin.panel.models;

public class VerificationHistoryModel {
    private String id;
    private String targetType; // APP or DEVELOPER
    private String targetId;
    private String targetName;
    private boolean previousStatus;
    private boolean newStatus;
    private String adminId;
    private String adminEmail;
    private String action; // APP_VERIFIED, APP_UNVERIFIED, DEVELOPER_VERIFIED, DEVELOPER_UNVERIFIED
    private long timestamp;
    private String reason;

    public VerificationHistoryModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public boolean isPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(boolean previousStatus) { this.previousStatus = previousStatus; }

    public boolean isNewStatus() { return newStatus; }
    public void setNewStatus(boolean newStatus) { this.newStatus = newStatus; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
