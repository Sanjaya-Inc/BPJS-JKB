import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
    alias(sjy.plugins.buildconfig.kmp)
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
        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(sjy.ktor.cio)
        }
    }
}
android {
    namespace = "io.healthkathon.jkb.core"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

buildConfig {
    val localPropertiesFile = rootProject.file("local.properties")
    require(localPropertiesFile.exists()) {
        "local.properties file not found. Please create local.properties file in the root directory."
    }
    
    val localPropertiesContent = localPropertiesFile.readText()
    val apiBaseUrlProperty = localPropertiesContent.lines()
        .find { line -> line.startsWith("api.base.url=") }
        ?.substringAfter("api.base.url=")
        ?.trim()
    
    require(!apiBaseUrlProperty.isNullOrBlank()) {
        "api.base.url property is missing or empty in local.properties. Please add: api.base.url"
    }
    
    val apiMockedProperty = localPropertiesContent.lines()
        .find { line -> line.startsWith("api.mocked=") }
        ?.substringAfter("api.mocked=")
        ?.trim()
        ?.toBoolean() ?: false
    
    buildConfigField(
        "String",
        "BASE_URL",
        "\"$apiBaseUrlProperty\""
    )
    
    buildConfigField(
        "Boolean",
        "API_MOCKED",
        "$apiMockedProperty"
    )
}
