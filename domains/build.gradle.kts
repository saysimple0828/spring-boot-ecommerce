plugins {
    java
}

subprojects {
    group = "com.saysimple"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

    apply(plugin = "java")

    dependencies {
        implementation(project(":common"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
