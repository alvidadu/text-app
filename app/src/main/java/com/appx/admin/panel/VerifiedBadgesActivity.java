package com.appx.admin.panel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.appx.admin.panel.adapters.VerifyAppAdapter;
import com.appx.admin.panel.adapters.VerifyDeveloperAdapter;
import com.appx.admin.panel.models.AppModel;
import com.appx.admin.panel.models.DeveloperModel;
import com.appx.admin.panel.models.VerificationHistoryModel;
import com.appx.admin.panel.utils.Constants;
import com.appx.admin.panel.utils.FirebaseHelper;
import com.appx.admin.panel.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VerifiedBadgesActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TextInputEditText etSearch;
    private TextView tvEmpty;
    private SessionManager sessionManager;
    private FirebaseHelper firebaseHelper;

    private final List<AppModel> allApps = new ArrayList<>();
    private final List<AppModel> filteredApps = new ArrayList<>();
    private final List<DeveloperModel> allDevelopers = new ArrayList<>();
    private final List<DeveloperModel> filteredDevelopers = new ArrayList<>();

    private VerifyAppAdapter appAdapter;
    private VerifyDeveloperAdapter developerAdapter;
    private int currentTab = 0; // 0 = Apps, 1 = Developers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified_badges);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }
        firebaseHelper = FirebaseHelper.getInstance();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        etSearch = findViewById(R.id.etSearch);
        tvEmpty = findViewById(R.id.tvEmpty);

        tabLayout.addTab(tabLayout.newTab().setText("Verified Apps"));
        tabLayout.addTab(tabLayout.newTab().setText("Verified Developers"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appAdapter = new VerifyAppAdapter(filteredApps, this::onToggleAppVerify);
        developerAdapter = new VerifyDeveloperAdapter(filteredDevelopers, this::onToggleDeveloperVerify);
        recyclerView.setAdapter(appAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                etSearch.setText("");
                if (currentTab == 0) {
                    recyclerView.setAdapter(appAdapter);
                    filterApps("");
                } else {
                    recyclerView.setAdapter(developerAdapter);
                    filterDevelopers("");
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String q = s != null ? s.toString() : "";
                if (currentTab == 0) filterApps(q);
                else filterDevelopers(q);
            }
        });

        swipeRefresh.setOnRefreshListener(this::loadData);
        loadData();
    }

    private void loadData() {
        swipeRefresh.setRefreshing(true);
        loadApps();
        loadDevelopers();
    }

    private void loadApps() {
        firebaseHelper.getAppsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allApps.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    AppModel app = child.getValue(AppModel.class);
                    if (app != null) {
                        app.setId(child.getKey());
                        allApps.add(app);
                    }
                }
                filterApps(etSearch.getText() != null ? etSearch.getText().toString() : "");
                if (currentTab == 0) swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(VerifiedBadgesActivity.this, "Failed to load apps", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDevelopers() {
        firebaseHelper.getDevelopersRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allDevelopers.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    DeveloperModel dev = child.getValue(DeveloperModel.class);
                    if (dev != null) {
                        dev.setId(child.getKey());
                        allDevelopers.add(dev);
                    }
                }
                filterDevelopers(etSearch.getText() != null ? etSearch.getText().toString() : "");
                if (currentTab == 1) swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void filterApps(String query) {
        filteredApps.clear();
        String q = query.toLowerCase(Locale.US).trim();
        for (AppModel app : allApps) {
            if (q.isEmpty()
                    || (app.getName() != null && app.getName().toLowerCase(Locale.US).contains(q))
                    || (app.getPackageName() != null && app.getPackageName().toLowerCase(Locale.US).contains(q))
                    || (app.getDeveloperName() != null && app.getDeveloperName().toLowerCase(Locale.US).contains(q))) {
                filteredApps.add(app);
            }
        }
        appAdapter.notifyDataSetChanged();
        updateEmpty();
    }

    private void filterDevelopers(String query) {
        filteredDevelopers.clear();
        String q = query.toLowerCase(Locale.US).trim();
        for (DeveloperModel dev : allDevelopers) {
            if (q.isEmpty()
                    || (dev.getName() != null && dev.getName().toLowerCase(Locale.US).contains(q))
                    || (dev.getEmail() != null && dev.getEmail().toLowerCase(Locale.US).contains(q))
                    || (dev.getId() != null && dev.getId().toLowerCase(Locale.US).contains(q))) {
                filteredDevelopers.add(dev);
            }
        }
        developerAdapter.notifyDataSetChanged();
        updateEmpty();
    }

    private void updateEmpty() {
        boolean empty = currentTab == 0 ? filteredApps.isEmpty() : filteredDevelopers.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void onToggleAppVerify(AppModel app, boolean currentlyVerified) {
        String title = currentlyVerified ? "Remove Verified Badge" : "Give Verified Badge";
        String message = currentlyVerified
                ? "Are you sure you want to remove the verified badge from \"" + app.getName() + "\"?"
                : "Are you sure you want to verify \"" + app.getName() + "\"?";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, (d, w) -> performAppVerification(app, !currentlyVerified))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void performAppVerification(AppModel app, boolean newStatus) {
        String adminId = sessionManager.getAdminId();
        String adminEmail = sessionManager.getAdminEmail();
        long now = System.currentTimeMillis();

        DatabaseReference appRef = firebaseHelper.getAppsRef().child(app.getId());
        Map<String, Object> updates = new HashMap<>();
        updates.put("appVerified", newStatus);
        updates.put("verifiedBy", newStatus ? adminId : null);
        updates.put("verifiedAt", newStatus ? now : null);
        updates.put("updatedAt", now);

        appRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    // Write verification history
                    VerificationHistoryModel history = new VerificationHistoryModel();
                    history.setTargetType(Constants.TARGET_APP);
                    history.setTargetId(app.getId());
                    history.setTargetName(app.getName());
                    history.setPreviousStatus(app.isAppVerified());
                    history.setNewStatus(newStatus);
                    history.setAdminId(adminId);
                    history.setAdminEmail(adminEmail);
                    history.setAction(newStatus ? Constants.ACTION_APP_VERIFIED : Constants.ACTION_APP_UNVERIFIED);
                    history.setTimestamp(now);

                    DatabaseReference histRef = firebaseHelper.getVerificationHistoryRef().push();
                    history.setId(histRef.getKey());
                    histRef.setValue(history);

                    // Admin log
                    writeAdminLog(newStatus ? Constants.ACTION_APP_VERIFIED : Constants.ACTION_APP_UNVERIFIED,
                            Constants.TARGET_APP, app.getId(), app.getName(), null);

                    app.setAppVerified(newStatus);
                    app.setVerifiedBy(newStatus ? adminId : null);
                    app.setVerifiedAt(newStatus ? now : 0);
                    appAdapter.notifyDataSetChanged();
                    Toast.makeText(this, newStatus ? "App verified" : "Verification removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void onToggleDeveloperVerify(DeveloperModel dev, boolean currentlyVerified) {
        String title = currentlyVerified ? "Remove Developer Verification" : "Verify Developer";
        String message = currentlyVerified
                ? "Are you sure you want to remove verification from \"" + dev.getName() + "\"?"
                : "Are you sure you want to verify developer \"" + dev.getName() + "\"?";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, (d, w) -> performDeveloperVerification(dev, !currentlyVerified))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void performDeveloperVerification(DeveloperModel dev, boolean newStatus) {
        String adminId = sessionManager.getAdminId();
        String adminEmail = sessionManager.getAdminEmail();
        long now = System.currentTimeMillis();

        DatabaseReference devRef = firebaseHelper.getDevelopersRef().child(dev.getId());
        Map<String, Object> updates = new HashMap<>();
        updates.put("developerVerified", newStatus);
        updates.put("verifiedBy", newStatus ? adminId : null);
        updates.put("verifiedAt", newStatus ? now : null);
        updates.put("updatedAt", now);

        devRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    VerificationHistoryModel history = new VerificationHistoryModel();
                    history.setTargetType(Constants.TARGET_DEVELOPER);
                    history.setTargetId(dev.getId());
                    history.setTargetName(dev.getName());
                    history.setPreviousStatus(dev.isDeveloperVerified());
                    history.setNewStatus(newStatus);
                    history.setAdminId(adminId);
                    history.setAdminEmail(adminEmail);
                    history.setAction(newStatus ? Constants.ACTION_DEVELOPER_VERIFIED : Constants.ACTION_DEVELOPER_UNVERIFIED);
                    history.setTimestamp(now);

                    DatabaseReference histRef = firebaseHelper.getVerificationHistoryRef().push();
                    history.setId(histRef.getKey());
                    histRef.setValue(history);

                    writeAdminLog(newStatus ? Constants.ACTION_DEVELOPER_VERIFIED : Constants.ACTION_DEVELOPER_UNVERIFIED,
                            Constants.TARGET_DEVELOPER, dev.getId(), dev.getName(), null);

                    dev.setDeveloperVerified(newStatus);
                    dev.setVerifiedBy(newStatus ? adminId : null);
                    dev.setVerifiedAt(newStatus ? now : 0);
                    developerAdapter.notifyDataSetChanged();
                    Toast.makeText(this, newStatus ? "Developer verified" : "Verification removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void writeAdminLog(String actionType, String targetType, String targetId, String targetName, String reason) {
        Map<String, Object> log = new HashMap<>();
        log.put("adminId", sessionManager.getAdminId());
        log.put("adminEmail", sessionManager.getAdminEmail());
        log.put("actionType", actionType);
        log.put("targetType", targetType);
        log.put("targetId", targetId);
        log.put("targetName", targetName);
        log.put("timestamp", System.currentTimeMillis());
        log.put("result", "success");
        log.put("reason", reason);
        firebaseHelper.getAdminLogsRef().push().setValue(log);
    }
}
