// build.gradle.kts

plugins {
    java
    id("org.springframework.boot") version "3.2.4" apply false // 🔽 루트 프로젝트에는 apply false
    id("io.spring.dependency-management") version "1.1.4"
}

// 모든 프로젝트 공통 설정
allprojects {
    group = "com.saysimple"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

    // 공통으로 적용할 플러그인 (예: java)
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    // Java 버전 설정
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// 하위 프로젝트 설정 (모듈별 build.gradle.kts 대신 여기서 통합 관리)
subprojects {
    apply(plugin = "org.springframework.boot") // 🔽 하위 프로젝트에 Spring Boot 적용

    dependencies {
        // 모든 하위 프로젝트에 공통으로 추가할 의존성
        implementation("org.springframework.boot:spring-boot-starter-aop")
        implementation("org.aspectj:aspectjrt:1.9.7")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("io.lettuce:lettuce-core")
    }
}

// Spring Cloud 버전 관리 (전체 프로젝트 적용)
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.1")
    }
}