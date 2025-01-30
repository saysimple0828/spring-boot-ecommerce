plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

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
    maven("https://packages.confluent.io/maven/")
}

extra["springCloudVersion"] = "2023.0.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.modelmapper:modelmapper:2.4.4")
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("org.redisson:redisson-spring-boot-starter:3.27.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("io.lettuce:lettuce-core")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // https://mvnrepository.com/artifact/io.confluent/kafka-avro-serializer
    implementation("io.confluent:kafka-avro-serializer:7.8.0") // 아프로 시리얼라이저
    // https://mvnrepository.com/artifact/org.apache.avro/avro
    implementation("org.apache.avro:avro:1.12.0")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// build 디렉터리를 프로젝트 내부로 설정
//layout.buildDirectory.set(project.layout.projectDirectory.dir("build"))
//
//tasks.withType<com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask> {
//    setOutputDir(layout.buildDirectory.dir("generated-main-avro-java").get().asFile)
//}
//
//tasks.compileJava {
//    dependsOn("generateAvroJava")
//}

// bootJar 비활성화 및 jar 활성화 설정 추가
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}
