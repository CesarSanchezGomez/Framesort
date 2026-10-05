plugins {
    java
}

group = property("group") as String
version = property("version") as String

fun v(name: String): String = property(name) as String

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc" }
}

dependencies {
    // Platform (provided at runtime, never shaded)
    compileOnly("io.papermc.paper:paper-api:${v("paper_api_version")}")

    // Tests use the Paper API types (YAML, Adventure), but never a running server.
    testImplementation("io.papermc.paper:paper-api:${v("paper_api_version")}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val targetJava = v("java_release").toInt()

java {
    sourceCompatibility = JavaVersion.toVersion(targetJava)
    targetCompatibility = JavaVersion.toVersion(targetJava)
    // Only force a toolchain when the running JVM is older than the target.
    if (JavaVersion.current() < JavaVersion.toVersion(targetJava)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJava))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJava)
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    val props = mapOf("version" to project.version.toString())
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") { expand(props) }
}
