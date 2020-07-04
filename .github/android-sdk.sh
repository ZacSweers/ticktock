#!/usr/bin/env bash
set -e

# The default android version ENV is a mess on GH Actions, so just install it ourselves.
# Adapted workaround from https://github.com/actions/virtual-environments/issues/60

echo "Setting up Actions Android SDK"

echo "Installing sdkmanager"
wget --quiet --output-document=android-sdk.zip https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}.zip
# unpacking into cmdline-tools/ because of https://issuetracker.google.com/issues/150942306
mkdir -p $ANDROID_HOME/cmdline-tools/
unzip -o android-sdk.zip -d $ANDROID_HOME/cmdline-tools
# rename folder to latest to match Android Studio/sdkmanager behavior
mv $ANDROID_HOME/cmdline-tools/tools $ANDROID_HOME/cmdline-tools/latest

echo "Installing required Android tools"
echo "y" | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

echo "Done!"
