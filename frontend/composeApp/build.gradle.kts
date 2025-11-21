import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(sjy.plugins.buildlogic.multiplatform.app)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":features:onboarding"))
            implementation(project(":features:fraud-detection"))
            implementation(project(":features:chatbot"))
            implementation(project(":features:menu"))
        }
    }
}

android {
    namespace = "io.healthkathon.bpjs.jkb"
    defaultConfig {
        applicationId = "io.healthkathon.bpjs.jkb"
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.healthkathon.bpjs.jkb.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.healthkathon.bpjs.jkb"
            packageVersion = "1.0.0"
        }
    }
}
