# AppX Store Admin Panel

Native Java Android administration application for the AppX Store platform.

## Technology Stack

- **Language**: Java
- **UI**: Native XML + Material Design 3
- **Min SDK**: 23 | **Target SDK**: 34
- **Backend**: Firebase Authentication + Realtime Database
- **Image Loading**: Glide
- **Architecture**: Activities, Adapters, Models, Helpers

## Project Structure

```
app/src/main/java/com/appx/admin/panel/
├── AdminLoginActivity.java          # Secure admin login + role verification
├── AdminDashboardActivity.java      # Live dashboard with stats
├── VerifiedBadgesActivity.java      # CORE: Verify/unverify apps & developers
├── AppManagementActivity.java       # Scaffold
├── PendingAppsActivity.java         # Scaffold
├── UserManagementActivity.java      # Scaffold
├── DeveloperManagementActivity.java # Scaffold
├── models/                          # AppModel, DeveloperModel, etc.
├── adapters/                        # RecyclerView adapters
└── utils/                           # FirebaseHelper, SessionManager, Constants
```

## Setup Instructions

### 1. Firebase Configuration

The project already includes `google-services.json` for project `appx-store-a7292`.

### 2. Required Firebase Realtime Database Structure

Create these nodes (security rules must restrict write access):

```
admin_users/
  {uid}/
    email: string
    role: "admin" | "superadmin"
    active: true
    createdAt: number

apps/
  {appId}/
    name, icon, developerId, developerName, category, description,
    packageName, versionName, versionCode, size, apkUrl, screenshots[],
    rating, reviewCount, downloadCount, status,
    featured, editorChoice, trending,
    appVerified: boolean,
    verifiedBy: string | null,
    verifiedAt: number | null,
    createdAt, updatedAt, ...

developers/
  {devId}/
    name, email, avatar, description, website,
    developerVerified: boolean,
    verifiedBy, verifiedAt,
    status, totalApps, publishedApps, ...

users/
verification_history/
admin_logs/
categories/
featured_apps/
reviews/
reports/
notifications/
store_settings/
stats/
```

### 3. Create an Admin Account

1. Create a user in Firebase Authentication (Email/Password).
2. Manually add a document under `admin_users/{uid}`:

```json
{
  "email": "admin@example.com",
  "role": "admin",
  "active": true,
  "createdAt": 1710000000000
}
```

Only users present in `admin_users` with `active: true` and role `admin`/`superadmin` can log in.

### 4. Security Rules (Example)

```json
{
  "rules": {
    "admin_users": {
      ".read": "auth != null && root.child('admin_users').child(auth.uid).child('active').val() === true",
      ".write": false
    },
    "apps": {
      ".read": "auth != null",
      ".write": "auth != null && root.child('admin_users').child(auth.uid).child('active').val() === true"
    },
    "developers": {
      ".read": "auth != null",
      ".write": "auth != null && root.child('admin_users').child(auth.uid).child('active').val() === true"
    },
    "verification_history": {
      ".read": "auth != null && root.child('admin_users').child(auth.uid).exists()",
      ".write": "auth != null && root.child('admin_users').child(auth.uid).child('active').val() === true"
    },
    "admin_logs": {
      ".read": "auth != null && root.child('admin_users').child(auth.uid).exists()",
      ".write": "auth != null && root.child('admin_users').child(auth.uid).child('active').val() === true"
    }
  }
}
```

**Never allow clients to write `appVerified` / `developerVerified` without admin checks.**

### 5. Build & Run

```bash
./gradlew assembleDebug
# or open in Android Studio and Run
```

Application ID: `com.appx.admin.panel`

## Implemented Features

- ✅ Secure Admin Login with Firebase Auth + backend role verification
- ✅ Session management with timeout
- ✅ Premium dark Material 3 theme
- ✅ Navigation drawer
- ✅ Live Dashboard stats (apps, users, developers, verified counts, downloads, reports)
- ✅ **Verified Badges (CORE FEATURE)**
  - Search apps / developers
  - Give / Remove Verified Badge for any app
  - Give / Remove Verified Badge for any developer
  - Independent appVerified vs developerVerified
  - Confirmation dialogs
  - Writes to verification_history and admin_logs
- ✅ Pending apps preview on dashboard
- ⬜ Full App Management, Pending Approval, Updates, Users, Developers, Categories, Featured, Reviews, Reports, Analytics, Settings, Activity Log (scaffolded – extend using the same Firebase patterns)

## Shared Data Model Notes

Use the exact field names `appVerified` and `developerVerified` in the User Panel so badges stay in sync.

The User Panel must read these authoritative backend flags only (never local or name-based checks).

## Important

- Admin privileges are **never** granted by installing this APK.
- All permission checks happen on the backend (`admin_users` node).
- Do not hardcode credentials or service account keys in the APK.
