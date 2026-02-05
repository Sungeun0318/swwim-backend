plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.zalmuk"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["querydslVersion"] = "5.1.0"
extra["jjwtVersion"] = "0.12.5"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:${property("querydslVersion")}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${property("querydslVersion")}:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jjwtVersion")}")

    // Swagger Annotations (Springdoc UI는 Spring Boot 4.x 호환 문제로 비활성화)
    implementation("io.swagger.core.v3:swagger-annotations:2.2.20")

    // AWS S3
    implementation(platform("software.amazon.awssdk:bom:2.25.0"))
    implementation("software.amazon.awssdk:s3")

    // Jackson (JSON 처리)
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Developer Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// QueryDSL 생성 파일 위치
sourceSets {
    main {
        java {
            srcDir("build/generated/sources/annotationProcessor/java/main")
        }
    }
}
