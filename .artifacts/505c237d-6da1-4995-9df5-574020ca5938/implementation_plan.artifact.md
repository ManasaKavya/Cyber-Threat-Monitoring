# Implementation Plan - Robust Error Handling & RBAC Fix

This plan addresses the application crashes caused by unhandled HTTP 403 Forbidden responses and optimizes API requests to ensure Employees do not call Admin-only endpoints.

## User Review Required

> [!IMPORTANT]
> - **Error Handling**: I will implement a safe API call wrapper or consistent try-catch blocks in all screens to prevent crashes.
> - **RBAC Enforcement**: I will ensure that Admin-only screens like `AlertsScreen` (when used in Admin context) and `AdminDashboard` are not triggered by Employee users.
> - **401 Session Expiry**: If a 401 is received, the app will clear the session and return to the login screen.

## Proposed Changes

### [Networking & Data Layer]

#### [MODIFY] [RetrofitClient.kt](file:///C:/Users/Madhu/AndroidStudioProjects/TrustGuardAI2/app/src/main/java/com/trustguardai/app/data/api/RetrofitClient.kt)
- Standardize the `authInterceptor` to handle token injection. (Existing)
- Add a mechanism to detect 401 and trigger `onSessionExpired`. (Existing)

### [UI & Screen Layer]

#### [MODIFY] [AlertsScreen.kt](file:///C:/Users/Madhu/AndroidStudioProjects/TrustGuardAI2/app/src/main/java/com/trustguardai/app/ui/screens/AlertsScreen.kt)
- Wrap `api.getAlerts()` in a try-catch block.
- Handle `HttpException`:
    - 403: Set an `accessDenied` state and show "Access Denied: Admin privileges required."
    - 401: Let `RetrofitClient` handle session expiry.
    - Other: Show "Unable to load alerts."
- **Conditional Logic**: If the user is an Employee, do not call the API; instead, immediately show the "Demo Data" or an "Access Denied" state depending on the context.

#### [MODIFY] [AdminDashboard.kt](file:///C:/Users/Madhu/AndroidStudioProjects/TrustGuardAI2/app/src/main/java/com/trustguardai/app/ui/screens/AdminDashboard.kt)
- Wrap all `async` API calls in a safe handler.
- Catch `HttpException` (especially 403) and display an error state instead of crashing.

#### [MODIFY] [EmployeeMainScaffold.kt](file:///C:/Users/Madhu/AndroidStudioProjects/TrustGuardAI2/app/src/main/java/com/trustguardai/app/ui/screens/EmployeeMainScaffold.kt)
- Review the `Alerts` tab usage. If Employees shouldn't see Admin alerts, we should either:
    - Point them to a user-specific alerts endpoint (if exists).
    - Or show the Demo Alerts only for Employees without calling the Admin API.

#### [MODIFY] All Scanner Screens (`UrlScannerScreen.kt`, `IpScannerScreen.kt`, `FileScannerScreen.kt`, etc.)
- Ensure all repository calls are wrapped in try-catch to handle network failures (Timeout, 500, etc.) gracefully.

## Verification Plan

### Manual Verification
1. **Employee Login**:
    - Navigate to all tabs.
    - Specifically check the Alerts tab -> Should not crash even if backend returns 403.
2. **Admin Login**:
    - Verify Admin Dashboard loads correctly.
3. **Connectivity Test**:
    - Turn off backend server.
    - Open app -> Should show "Unable to connect to server" instead of crashing.
4. **Auth Expiry**:
    - Manually invalidate token in backend (if possible) or wait for expiry.
    - App should return to Login.
