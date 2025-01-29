plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.saysimple"
version = "0.0.1-SNAPSHOT"

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
    // lombok
    implementation("org.axonframework:axon-spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.axonframework.extensions.mongo:axon-mongo")
    implementation("io.projectreactor:reactor-core:3.6.0")
    implementation("io.projectreactor.addons:reactor-extra")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.axonframework:axon-test")
    testImplementation("org.springframework:spring-test")
    testImplementation("io.projectreactor:reactor-test:3.6.0")
    testImplementation("org.awaitility:awaitility")
    testImplementation("org.springframework:spring-webflux")
    testImplementation("io.projectreactor.netty:reactor-netty-http")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb:1.17.6")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

dependencyManagement {
    imports {
        mavenBom("org.axonframework:axon-bom:4.9.3")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
