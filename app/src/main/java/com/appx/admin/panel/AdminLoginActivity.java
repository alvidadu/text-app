package com.appx.admin.panel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.appx.admin.panel.utils.FirebaseHelper;
import com.appx.admin.panel.utils.SessionManager;

public class AdminLoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvError;
    private FirebaseAuth auth;
    private SessionManager sessionManager;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard();
            return;
        }

        auth = FirebaseHelper.getInstance().getAuth();
        firebaseHelper = FirebaseHelper.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        tvError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(email)) {
            showError("Please enter email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError("Please enter password");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        setLoading(true);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            verifyAdminRole(user.getUid(), email);
                        } else {
                            setLoading(false);
                            showError("Authentication failed");
                        }
                    } else {
                        setLoading(false);
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";
                        showError(msg != null ? msg : "Login failed");
                    }
                });
    }

    private void verifyAdminRole(String uid, String email) {
        // Backend role verification: check admin_users node
        firebaseHelper.getAdminUsersRef().child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        setLoading(false);
                        if (snapshot.exists()) {
                            Boolean isActive = snapshot.child("active").getValue(Boolean.class);
                            String role = snapshot.child("role").getValue(String.class);
                            if (Boolean.TRUE.equals(isActive) && ("admin".equals(role) || "superadmin".equals(role))) {
                                sessionManager.createLoginSession(uid, email);
                                navigateToDashboard();
                            } else {
                                auth.signOut();
                                showError("This account is not authorized as an administrator or has been suspended.");
                            }
                        } else {
                            auth.signOut();
                            showError("This account is not registered as an administrator.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        setLoading(false);
                        auth.signOut();
                        showError("Unable to verify admin status. Please try again.");
                    }
                });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
