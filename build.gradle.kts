import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.42"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
}

taboolib {
    install("common")
    install("common-5")
    install("module-ai")
    install("module-chat")
    install("module-configuration")
    install("module-database")
    install("module-effect")
    install("module-lang")
    install("module-metrics")
    install("module-navigation")
    install("module-nms")
    install("module-nms-util")
    install("module-ui")
    install("module-ui-receptacle")
    install("expansion-command-helper")
    install("expansion-player-database")
    install("expansion-persistent-container")
    
    install("platform-bukkit")
    classifier = null
    version = "6.0.9-58"
    
    description { 
        desc("A core lib made by SunShine Technology.")
        bukkitApi("1.13")
        
        contributors { 
            name("Sunshine_wzy")
        }
        
        links { 
            name("homepage").url("https://www.mcbbs.net/thread-1170416-1-1.html")
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly(kotlin("stdlib"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    compileOnly("org.yaml:snakeyaml:1.28")
    compileOnly("com.google.code.gson:gson:2.8.7")

    compileOnly(fileTree("libs"))
}

tasks {
    
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}