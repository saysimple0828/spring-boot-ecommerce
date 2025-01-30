plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

subprojects{
    repositories {
        mavenCentral()
        maven("https://packages.confluent.io/maven/")
    }

    dependencies {
        implementation(project(":common"))
        // spring starter
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        // spring cloud
        implementation("org.springframework.cloud:spring-cloud-starter-config")
        implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
        implementation("org.springframework.cloud:spring-cloud-starter-bus-amqp")
        implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
        implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.0")
        implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
        // spring security
        implementation("org.springframework.security:spring-security-config")
        implementation("org.modelmapper:modelmapper:2.3.8")
        // Spring Cache (캐시 추상화)
        implementation("org.springframework.boot:spring-boot-starter-cache")
        // Caffeine
        implementation("com.github.ben-manes.caffeine:caffeine:3.1.6")
        // Avro
        implementation("io.confluent:kafka-avro-serializer:7.8.0") // 아프로 시리얼라이저
        implementation("org.apache.avro:avro:1.12.0")
        // jwt
        implementation("io.jsonwebtoken:jjwt-api:0.12.3")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
        runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
        // redis
        implementation("org.springframework.session:spring-session-data-redis")
        // kafka
        implementation("org.springframework.kafka:spring-kafka")
        implementation("io.micrometer:micrometer-observation")
//    implementation("io.micrometer:micrometer-tracing-bridge-brave")
        implementation("io.zipkin.reporter2:zipkin-reporter-brave")
        implementation ("org.mariadb.jdbc:mariadb-java-client:2.7.3")
        implementation("org.springframework.hateoas:spring-hateoas")

        developmentOnly("org.springframework.boot:spring-boot-devtools")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // test
        runtimeOnly("com.h2database:h2")
        testImplementation("org.springframework.boot:spring-boot-starter-test")


    }
}

// bootJar 비활성화 및 jar 활성화 설정 추가
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}