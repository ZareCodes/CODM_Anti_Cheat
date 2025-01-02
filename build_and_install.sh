#!/bin/bash

# Define project directories
SRC_DIR="src"
BUILD_DIR="build"

# Check prerequisites
if ! command -v javac &> /dev/null || ! command -v dx &> /dev/null || ! command -v aapt &> /dev/null || ! command -v apksigner &> /dev/null; then
  echo "Error: Required tools (javac, dx, aapt, apksigner) are not installed."
  exit 1
fi

# Clean previous build
rm -rf $BUILD_DIR
mkdir $BUILD_DIR

# Compile Java files
javac -d $BUILD_DIR $SRC_DIR/com/example/codmanticheat/*.java

# Convert to DEX
dx --dex --output=$BUILD_DIR/classes.dex $BUILD_DIR

# Package APK
aapt package -F $BUILD_DIR/codmanticheat.apk -M AndroidManifest.xml -I /path/to/android.jar -S res -f

# Add classes.dex
aapt add $BUILD_DIR/codmanticheat.apk $BUILD_DIR/classes.dex

# Sign APK
apksigner sign --ks my-release-key.keystore --out CODMAntiCheat.apk $BUILD_DIR/codmanticheat.apk

# Install APK
adb install -r CODMAntiCheat.apk
