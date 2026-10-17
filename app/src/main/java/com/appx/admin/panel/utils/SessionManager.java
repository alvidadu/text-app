package com.appx.admin.panel.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AppXAdminSession";
    private static final String KEY_ADMIN_ID = "admin_id";
    private static final String KEY_ADMIN_EMAIL = "admin_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final long SESSION_TIMEOUT_MS = 24 * 60 * 60 * 1000L; // 24 hours

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(String adminId, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_ADMIN_ID, adminId);
        editor.putString(KEY_ADMIN_EMAIL, email);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }

    public boolean isLoggedIn() {
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return false;
        }
        long loginTime = prefs.getLong(KEY_LOGIN_TIME, 0);
        if (System.currentTimeMillis() - loginTime > SESSION_TIMEOUT_MS) {
            logout();
            return false;
        }
        return true;
    }

    public String getAdminId() {
        return prefs.getString(KEY_ADMIN_ID, null);
    }

    public String getAdminEmail() {
        return prefs.getString(KEY_ADMIN_EMAIL, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public void updateLoginTime() {
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }
}
