# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK.

# Keep Compose and Room related classes if needed
-keep class androidx.compose.** { *; }
-keep class androidx.room.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements java.io.Serializable { *; }

# AUTONOMOUS 20-LOOP IMPROVEMENT: Proguard rules reviewed and expanded for all components (UI, Services, AI).
-keep class com.emh.app.** { *; }