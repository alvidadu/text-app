package com.appx.admin.panel;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.appx.admin.panel.utils.SessionManager;

public class AppManagementActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        SessionManager sm = new SessionManager(this);
        if (!sm.isLoggedIn()) { finish(); return; }
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("AppManagementActivity".replace("Activity", "").replaceAll("([A-Z])", " $1").trim());
        toolbar.setNavigationOnClickListener(v -> finish());
        TextView tv = findViewById(R.id.tvPlaceholder);
        tv.setText("This module is scaffolded.\nConnect to Firebase data nodes and implement full CRUD as described in the master prompt.\n\nCore Verified Badges is fully implemented.");
    }
}
