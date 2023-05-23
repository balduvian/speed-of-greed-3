import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("com.github.johnrengelman.shadow") version "7.1.2"
	application
	kotlin("jvm") version "1.8.21"
}

group = "com.c1fr1"
version = "1.0.0"

val lwjglVersion = "3.3.1"
val jomlVersion = "1.10.4"

repositories {
	mavenCentral()
	maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
	implementation("org.lwjgl:lwjgl:${lwjglVersion}")
	implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}")
	implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}")
	implementation("org.lwjgl:lwjgl-openal:${lwjglVersion}")

	implementation("org.lwjgl:lwjgl:${lwjglVersion}:natives-windows")
	implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}:natives-windows")
	implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}:natives-windows")
	implementation("org.lwjgl:lwjgl-openal:${lwjglVersion}:natives-windows")

	implementation("org.joml:joml:${jomlVersion}")
	implementation(kotlin("stdlib-jdk8"))
}

sourceSets {
	main {
		java {
			srcDir("src")
		}
	}
}

tasks {
	shadowJar {
		archiveFileName.set("${project.name}.jar")
		manifest {
			attributes(mapOf("Main-Class" to "engineTester/MainGameLoop"))
		}
	}
	jar {
		enabled = false
	}
	build {
		dependsOn(shadowJar)
	}
}

application {
	mainClass.set("game/Main")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "1.8"
}