plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

android {
    namespace = "com.remainder.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.remainder.app"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    debugImplementation(composeBom)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material3)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.junit4)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

tasks.register<JacocoReport>("jacocoLogicReport") {
    dependsOn("compileDebugKotlin", "testDebugUnitTest")
    val classOutput = layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")
    val execFile = layout.buildDirectory.file(
        "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
    )
    inputs.dir(classOutput)
    inputs.file(execFile)
    classDirectories.setFrom(
        files(classOutput).asFileTree.matching {
            exclude("**/ComposableSingletons*")
            exclude("**/*ScreenKt*")
            exclude("**/RemainderNavHostKt*")
            exclude("**/RemainderAppKt*")
            exclude("**/ThemeKt*")
            exclude("**/MainActivity*")
            exclude("**/LockScreenAlarmActivity*")
            exclude("**/AlarmUiState*")
            exclude("**/CreateAlarmScreenKt*")
            exclude("**/TwentyFourHourPickersKt*")
            exclude("**/LockScreenNotificationController*")
            exclude("**/LockScreenNotificationChannel*")
            exclude("**/LockScreenNotificationPreferences*")
            exclude("**/LockScreenTapIntent*")
            exclude("**/NotificationPermissionReader*")
            exclude("**/ContextAlarmLauncher*")
        }
    )
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(execFile)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
