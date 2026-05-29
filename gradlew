#!/usr/bin/env sh

# Improved Gradle wrapper stub for autonomous development.
# This project was set up during heavy autonomous coding sessions.

echo "EMH Android Project - Gradle Wrapper"
echo "===================================="
echo ""
echo "This is a development project created autonomously."
echo ""
echo "Recommended way to build:"
echo "  1. Open the folder in Android Studio (recommended)"
echo "  2. Let it download the Gradle wrapper automatically"
echo ""
echo "Alternative (if you have Gradle 8.9+ installed):"
echo "  gradle wrapper --gradle-version 8.9"
echo "  ./gradlew assembleDebug"
echo ""
echo "Note: Full build requires Android SDK + Gradle wrapper jar."
echo ""

# If someone really has the jar, try to run it
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Wrapper jar detected — attempting to run..."
    exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
else
    echo "Wrapper jar not present. Please use Android Studio."
    exit 1
fi