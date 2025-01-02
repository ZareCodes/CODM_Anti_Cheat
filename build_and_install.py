import os
import subprocess
import shutil
import sys

# Define paths
SRC_DIR = "src"
BUILD_DIR = "build"
APK_NAME = "CODMAntiCheat.apk"
KEYSTORE_PATH = "/path/to/your/my-release-key.keystore"
ANDROID_JAR_PATH = "/path/to/android.jar"

# Ensure that required tools are installed
def check_tool_installed(tool_name):
    try:
        subprocess.check_call([tool_name, "--version"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        print(f"{tool_name} is installed.")
    except subprocess.CalledProcessError:
        print(f"Error: {tool_name} is not installed.")
        sys.exit(1)

# Check if all required tools are installed
required_tools = ['javac', 'dx', 'aapt', 'apksigner', 'adb']
for tool in required_tools:
    check_tool_installed(tool)

# Clean previous build
if os.path.exists(BUILD_DIR):
    shutil.rmtree(BUILD_DIR)
os.makedirs(BUILD_DIR)

# Step 1: Compile Java files
print("Compiling Java files...")
javac_command = [
    'javac', '-d', BUILD_DIR, os.path.join(SRC_DIR, 'com', 'example', 'codmanticheat', '*.java')
]
subprocess.check_call(javac_command)

# Step 2: Convert to DEX
print("Converting to DEX format...")
dx_command = [
    'dx', '--dex', '--output=' + os.path.join(BUILD_DIR, 'classes.dex'), BUILD_DIR
]
subprocess.check_call(dx_command)

# Step 3: Package APK
print("Packaging APK...")
aapt_command = [
    'aapt', 'package', '-F', os.path.join(BUILD_DIR, APK_NAME), '-M', 'AndroidManifest.xml',
    '-I', ANDROID_JAR_PATH, '-S', 'res', '-f'
]
subprocess.check_call(aapt_command)

# Add DEX file to APK
print("Adding DEX file to APK...")
aapt_add_command = [
    'aapt', 'add', os.path.join(BUILD_DIR, APK_NAME), os.path.join(BUILD_DIR, 'classes.dex')
]
subprocess.check_call(aapt_add_command)

# Step 4: Sign the APK
print("Signing APK...")
apksigner_command = [
    'apksigner', 'sign', '--ks', KEYSTORE_PATH, '--out', APK_NAME, os.path.join(BUILD_DIR, APK_NAME)
]
subprocess.check_call(apksigner_command)

# Step 5: Install APK
print("Installing APK on device...")
adb_command = ['adb', 'install', '-r', APK_NAME]
subprocess.check_call(adb_command)

print("Build and installation complete!")
