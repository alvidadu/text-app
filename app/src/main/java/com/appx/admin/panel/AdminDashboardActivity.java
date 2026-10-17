package com.appx.admin.panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.appx.admin.panel.adapters.PendingAppAdapter;
import com.appx.admin.panel.models.AppModel;
import com.appx.admin.panel.models.DashboardStats;
import com.appx.admin.panel.utils.Constants;
import com.appx.admin.panel.utils.FirebaseHelper;
import com.appx.admin.panel.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private SessionManager sessionManager;
    private FirebaseHelper firebaseHelper;
    private PendingAppAdapter pendingAdapter;
    private final List<AppModel> pendingList = new ArrayList<>();

    private TextView tvTotalApps, tvPendingApps, tvTotalUsers, tvTotalDevelopers;
    private TextView tvVerifiedApps, tvVerifiedDevs, tvDownloads, tvReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        firebaseHelper = FirebaseHelper.getInstance();
        initViews();
        setupDrawer();
        setupRecycler();
        loadDashboardData();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        // Bind stat card texts via findView on includes is complex; use helper
        View cardTotalApps = findViewById(R.id.cardTotalApps);
        View cardPendingApps = findViewById(R.id.cardPendingApps);
        View cardTotalUsers = findViewById(R.id.cardTotalUsers);
        View cardTotalDevelopers = findViewById(R.id.cardTotalDevelopers);
        View cardVerifiedApps = findViewById(R.id.cardVerifiedApps);
        View cardVerifiedDevs = findViewById(R.id.cardVerifiedDevs);
        View cardDownloads = findViewById(R.id.cardDownloads);
        View cardReports = findViewById(R.id.cardReports);

        tvTotalApps = cardTotalApps.findViewById(R.id.tvStatValue);
        ((TextView) cardTotalApps.findViewById(R.id.tvStatLabel)).setText("Total Apps");
        tvPendingApps = cardPendingApps.findViewById(R.id.tvStatValue);
        ((TextView) cardPendingApps.findViewById(R.id.tvStatLabel)).setText("Pending Apps");
        tvTotalUsers = cardTotalUsers.findViewById(R.id.tvStatValue);
        ((TextView) cardTotalUsers.findViewById(R.id.tvStatLabel)).setText("Total Users");
        tvTotalDevelopers = cardTotalDevelopers.findViewById(R.id.tvStatValue);
        ((TextView) cardTotalDevelopers.findViewById(R.id.tvStatLabel)).setText("Developers");
        tvVerifiedApps = cardVerifiedApps.findViewById(R.id.tvStatValue);
        ((TextView) cardVerifiedApps.findViewById(R.id.tvStatLabel)).setText("Verified Apps");
        tvVerifiedDevs = cardVerifiedDevs.findViewById(R.id.tvStatValue);
        ((TextView) cardVerifiedDevs.findViewById(R.id.tvStatLabel)).setText("Verified Devs");
        tvDownloads = cardDownloads.findViewById(R.id.tvStatValue);
        ((TextView) cardDownloads.findViewById(R.id.tvStatLabel)).setText("Downloads");
        tvReports = cardReports.findViewById(R.id.tvStatValue);
        ((TextView) cardReports.findViewById(R.id.tvStatLabel)).setText("Pending Reports");

        View header = navigationView.getHeaderView(0);
        TextView tvEmail = header.findViewById(R.id.tvNavAdminEmail);
        tvEmail.setText(sessionManager.getAdminEmail());

        swipeRefresh.setOnRefreshListener(this::loadDashboardData);
        findViewById(R.id.btnViewAllPending).setOnClickListener(v ->
                startActivity(new Intent(this, PendingAppsActivity.class)));
    }

    private void setupDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);
    }

    private void setupRecycler() {
        RecyclerView rv = findViewById(R.id.rvPendingApps);
        rv.setLayoutManager(new LinearLayoutManager(this));
        pendingAdapter = new PendingAppAdapter(pendingList, app -> {
            // Open pending detail - for now toast
            Toast.makeText(this, "Open: " + app.getName(), Toast.LENGTH_SHORT).show();
        });
        rv.setAdapter(pendingAdapter);
    }

    private void loadDashboardData() {
        swipeRefresh.setRefreshing(true);
        DashboardStats stats = new DashboardStats();

        // Load apps
        firebaseHelper.getAppsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = 0, pending = 0, published = 0, rejected = 0, suspended = 0, verified = 0, downloads = 0;
                pendingList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    AppModel app = child.getValue(AppModel.class);
                    if (app == null) continue;
                    app.setId(child.getKey());
                    total++;
                    if (app.getStatus() != null) {
                        switch (app.getStatus()) {
                            case Constants.STATUS_PENDING:
                                pending++;
                                if (pendingList.size() < 5) pendingList.add(app);
                                break;
                            case Constants.STATUS_APPROVED:
                                published++;
                                break;
                            case Constants.STATUS_REJECTED:
                                rejected++;
                                break;
                            case Constants.STATUS_SUSPENDED:
                                suspended++;
                                break;
                        }
                    }
                    if (app.isAppVerified()) verified++;
                    downloads += app.getDownloadCount();
                }
                stats.totalApps = total;
                stats.pendingApps = pending;
                stats.publishedApps = published;
                stats.rejectedApps = rejected;
                stats.suspendedApps = suspended;
                stats.verifiedApps = verified;
                stats.totalDownloads = downloads;

                tvTotalApps.setText(String.valueOf(total));
                tvPendingApps.setText(String.valueOf(pending));
                tvVerifiedApps.setText(String.valueOf(verified));
                tvDownloads.setText(formatCount(downloads));

                pendingAdapter.notifyDataSetChanged();
                TextView tvNoPending = findViewById(R.id.tvNoPending);
                tvNoPending.setVisibility(pendingList.isEmpty() ? View.VISIBLE : View.GONE);

                loadUsersAndDevs(stats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(AdminDashboardActivity.this, "Failed to load apps", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsersAndDevs(DashboardStats stats) {
        firebaseHelper.getUsersRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stats.totalUsers = snapshot.getChildrenCount();
                tvTotalUsers.setText(String.valueOf(stats.totalUsers));

                firebaseHelper.getDevelopersRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        long verifiedDevs = 0;
                        for (DataSnapshot child : ds.getChildren()) {
                            Boolean v = child.child("developerVerified").getValue(Boolean.class);
                            if (Boolean.TRUE.equals(v)) verifiedDevs++;
                        }
                        stats.totalDevelopers = ds.getChildrenCount();
                        stats.verifiedDevelopers = verifiedDevs;
                        tvTotalDevelopers.setText(String.valueOf(stats.totalDevelopers));
                        tvVerifiedDevs.setText(String.valueOf(verifiedDevs));

                        firebaseHelper.getReportsRef().orderByChild("status").equalTo("pending")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot rs) {
                                        stats.pendingReports = rs.getChildrenCount();
                                        tvReports.setText(String.valueOf(stats.pendingReports));
                                        swipeRefresh.setRefreshing(false);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        swipeRefresh.setRefreshing(false);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private String formatCount(long count) {
        if (count >= 1_000_000) return String.format("%.1fM", count / 1_000_000.0);
        if (count >= 1_000) return String.format("%.1fK", count / 1_000.0);
        return String.valueOf(count);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_dashboard) {
            // already here
        } else if (id == R.id.nav_apps) {
            startActivity(new Intent(this, AppManagementActivity.class));
        } else if (id == R.id.nav_pending) {
            startActivity(new Intent(this, PendingAppsActivity.class));
        } else if (id == R.id.nav_verified) {
            startActivity(new Intent(this, VerifiedBadgesActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(this, UserManagementActivity.class));
        } else if (id == R.id.nav_developers) {
            startActivity(new Intent(this, DeveloperManagementActivity.class));
        } else if (id == R.id.nav_logout) {
            logout();
        } else {
            Toast.makeText(this, "Module coming soon / under construction", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void logout() {
        FirebaseHelper.getInstance().getAuth().signOut();
        sessionManager.logout();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, AdminLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
