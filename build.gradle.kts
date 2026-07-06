// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    // Clean, explicit modern declaration:
    id("com.google.gms.google-services") version libs.versions.googleGmsGoogleServices.get() apply false
}