plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":Token"))
    implementation(project(":Parser"))
    implementation(project(":Lexer"))
    implementation(project(":ByteCodeGenerator"))
}

tasks.test {
    useJUnitPlatform()
}