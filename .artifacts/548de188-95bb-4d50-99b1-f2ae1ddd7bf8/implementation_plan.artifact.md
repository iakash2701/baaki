# Implementation Plan - Step 1: User Authentication and Profile

Implement user authentication, registration, session persistence, profile management, and side navigation drawer for Baaki.

## User Review Required

> [!IMPORTANT]
> - **Authentication Strategy**: To maintain Baaki's core "offline-first" architecture without requiring external backend keys/secrets setup out-of-the-box, authentication will be implemented using secure local session persistence (via DataStore Preferences) and robust credential validation, backed by a pre-configured demo user (`Rajesh Sharma`, `rajesh@sharmapackaging.in`) and support for user registration and social auth buttons (Google, Apple, Microsoft).
> - **Session Flow**: On fresh launch, users land on the Welcome/Login screen unless already logged in. Successful login/registration navigates to the Home dashboard.

## Proposed Changes

### [Authentication & Session Management]
#### [NEW] `AuthRepository.kt` & `AuthViewModel.kt`
- Manage user authentication state, session persistence using DataStore Preferences, login validation, registration, and profile updates.

### [UI Screens & Navigation]
#### [NEW] `LoginScreen.kt` & `RegisterScreen.kt` & `ForgotPasswordScreen.kt`
- Premium welcome/login screen matching Baaki's dark forest-green, emerald, and mint theme (`BaakiTheme`).
- Continue with Google, Apple, Microsoft buttons.
- Email/password login with validation and error messaging.

#### [NEW] `ProfileDrawer.kt` / `EditProfileScreen.kt`
- Profile icon in the top-left corner of the Home screen.
- Side navigation drawer showing user name, email, profile photo/initials, business name, Edit Profile, Security, Settings, Help, and Logout.
- Edit Profile screen to update personal and business details.

#### [MODIFY] `MainScreen.kt` & `MainActivity.kt`
- Integrate auth flow check to direct users to Login if unauthenticated, or Home dashboard if authenticated.

## Verification Plan

### Automated Tests
- Unit tests for authentication state validation, password matching, email validation, and profile updates.

### Manual Verification
- Launch app, verify login screen appearance, test login with demo credentials or newly registered account, verify navigation to Home dashboard, test profile icon opening side drawer, update profile details, and test logout.
