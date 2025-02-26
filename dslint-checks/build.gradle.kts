plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.android.lint)
}

apply(from = "$rootDir/versions.gradle")
apply(from = "$rootDir/gradle/mvn_push_java.gradle")

kotlin {
    jvmToolchain(17)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.bundles.lint.api)
}