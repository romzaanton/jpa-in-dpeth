plugins {
	java
	antlr
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "io.code.art.jpa.in"
version = "0.0.1-SNAPSHOT"

val antlrVersion = "4.13.1"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Common dependencies
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	antlr("org.antlr:antlr4:$antlrVersion")
	implementation("org.antlr:antlr4:$antlrVersion")
	implementation("org.antlr:antlr4-runtime:$antlrVersion")

	// Database dependencies
	annotationProcessor("org.hibernate:hibernate-jpamodelgen:6.1.7.Final")
	implementation("org.postgresql:postgresql")


	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sourceSets {
	create("analytics") {
		java.srcDir("src/generated/java")
	}
}
