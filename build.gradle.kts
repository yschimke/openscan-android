plugins {
    // Declared here (apply false) so each plugin's classes load once into the
    // root classloader and subprojects can apply them without version skew.
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.ktfmt) apply false
}

subprojects {
    apply(plugin = rootProject.libs.plugins.ktfmt.get().pluginId)
    extensions.configure<com.ncorti.ktfmt.gradle.KtfmtExtension> {
        googleStyle()
    }
}

// Wires .githooks/ as the repository's hooks directory. One-time bootstrap:
// `./gradlew installGitHooks`.
tasks.register<Exec>("installGitHooks") {
    group = "git hooks"
    description = "Configure git to use the .githooks directory in this repo."
    workingDir = rootDir
    commandLine("git", "config", "core.hooksPath", ".githooks")
}
