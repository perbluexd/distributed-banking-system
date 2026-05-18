import org.gradle.api.file.DuplicatesStrategy

plugins {
	java
	id("org.springframework.boot") version "3.5.14"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.banking"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.springframework.kafka:spring-kafka")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:kafka")
	testImplementation("org.testcontainers:postgresql")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
	implementation("io.micrometer:micrometer-registry-prometheus")
}

sourceSets {
	create("integrationTest") {
		java.srcDir("src/integrationTest/java")
		resources.srcDir("src/integrationTest/resources")

		compileClasspath += sourceSets["main"].output + configurations["testCompileClasspath"]
		runtimeClasspath += output + compileClasspath + configurations["testRuntimeClasspath"]
	}
}

configurations {
	named("integrationTestImplementation") {
		extendsFrom(configurations["testImplementation"])
	}
	named("integrationTestRuntimeOnly") {
		extendsFrom(configurations["testRuntimeOnly"])
	}
	named("integrationTestCompileOnly") {
		extendsFrom(configurations["testCompileOnly"])
	}
	named("integrationTestAnnotationProcessor") {
		extendsFrom(configurations["testAnnotationProcessor"])
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val integrationTest by tasks.registering(Test::class) {
	description = "Runs integration tests."
	group = "verification"

	testClassesDirs = sourceSets["integrationTest"].output.classesDirs
	classpath = sourceSets["integrationTest"].runtimeClasspath

	shouldRunAfter(tasks.test)

	useJUnitPlatform()
}

tasks.named<ProcessResources>("processIntegrationTestResources") {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.check {
	dependsOn(integrationTest)
}