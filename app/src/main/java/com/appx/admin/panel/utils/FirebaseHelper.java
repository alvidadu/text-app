package com.appx.admin.panel.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private final FirebaseAuth auth;
    private final DatabaseReference rootRef;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        // Use the RTDB URL from google-services.json
        rootRef = FirebaseDatabase.getInstance("https://appx-store-a7292-default-rtdb.firebaseio.com").getReference();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getRootRef() {
        return rootRef;
    }

    public DatabaseReference getAppsRef() {
        return rootRef.child("apps");
    }

    public DatabaseReference getDevelopersRef() {
        return rootRef.child("developers");
    }

    public DatabaseReference getUsersRef() {
        return rootRef.child("users");
    }

    public DatabaseReference getAdminUsersRef() {
        return rootRef.child("admin_users");
    }

    public DatabaseReference getVerificationHistoryRef() {
        return rootRef.child("verification_history");
    }

    public DatabaseReference getAdminLogsRef() {
        return rootRef.child("admin_logs");
    }

    public DatabaseReference getCategoriesRef() {
        return rootRef.child("categories");
    }

    public DatabaseReference getFeaturedRef() {
        return rootRef.child("featured_apps");
    }

    public DatabaseReference getReviewsRef() {
        return rootRef.child("reviews");
    }

    public DatabaseReference getReportsRef() {
        return rootRef.child("reports");
    }

    public DatabaseReference getNotificationsRef() {
        return rootRef.child("notifications");
    }

    public DatabaseReference getStoreSettingsRef() {
        return rootRef.child("store_settings");
    }

    public DatabaseReference getStatsRef() {
        return rootRef.child("stats");
    }
}
