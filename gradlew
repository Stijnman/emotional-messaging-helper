#!/usr/bin/env sh

# EMH Android Project - Gradle Wrapper (autonomous dev stub)
# This project was built autonomously. The real gradle-wrapper.jar is intentionally
# not committed so Android Studio can provide the correct one.

echo "EMH Android Project - Gradle Wrapper"
echo "===================================="
echo ""
echo "This is a development project created autonomously (20+ improvement loops)."
echo ""
echo ">>> NEXT STEP (recommended):"
echo "1. Open this folder in Android Studio (2024.3 or newer)"
echo "2. Android Studio will auto-download Gradle + the wrapper jar"
echo "3. Let Gradle Sync finish"
echo "4. Build > Make Project (or run on physical device)"
echo ""
echo "If Android Studio complains about missing wrapper jar:"
echo "  - File > Sync Project with Gradle Files"
echo "  - Or: Build > Clean Project then Rebuild"
echo ""
echo "Manual (advanced - only if you have Gradle installed):"
echo "  gradle wrapper --gradle-version 8.9"
echo "  ./gradlew assembleDebug"
echo ""
echo "Note: Full build + run requires:"
echo "  - Android SDK (via Android Studio)"
echo "  - Physical Android device (emulators have poor accessibility support)"
echo "  - Ollama running with llava (or similar) for vision features"
echo ""

# If the jar somehow exists (e.g. user ran the manual step), delegate to it
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Wrapper jar detected — delegating..."
    exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
else
    echo "Wrapper jar not present. Please open the project in Android Studio."
    exit 1
fi