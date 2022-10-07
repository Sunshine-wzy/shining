import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    val kotlinVersion = "1.7.10"
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.42"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jetbrains.dokka") version kotlinVersion
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
    
    install("platform-bukkit")
    classifier = null
    version = "6.0.9-111"
    
    relocate("com.zaxxer.hikari.", "com.zaxxer.hikari_5_0_1.")
    
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
    maven("https://repo.papermc.io/repository/maven-public/")
}

val kotlinVersion: String by project
val exposedVersion: String by project
val jacksonVersion: String by project
dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly(kotlin("stdlib"))

    compileOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.0")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    compileOnly("com.google.guava:guava:21.0")
    compileOnly("org.yaml:snakeyaml:1.28")

    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    compileOnly("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    compileOnly("com.zaxxer:HikariCP:5.0.1")

    compileOnly(fileTree("libs"))


    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
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