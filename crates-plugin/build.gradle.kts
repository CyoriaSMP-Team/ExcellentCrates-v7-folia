import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {

}

dependencies {
    implementation(project(":crates-api"))
    implementation(project(":crates-core"))
    implementation(project(":integration-itemsadder"))
    implementation(project(":integration-nexo"))
    implementation(project(":integration-packetevents"))
    implementation(project(":integration-papi"))
    implementation("com.github.Anon8281:UniversalScheduler:0.1.7")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("thin") 
}

// Configure the shadowJar task to bundle the necessary dependencies.
tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("ExcellentCrates")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")
    relocate(
        "com.github.Anon8281.universalScheduler",
        "su.nightexpress.excellentcrates.universalScheduler"
    )
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    processResources {
        // Replicates maven <filtering>true</filtering> for plugin.yml
        // Replaces ${version} with the project version.
        val currentVersion = project.version.toString()

        filesMatching("*plugin.yml") {
            expand(mapOf("version" to currentVersion))
        }
    }
}

// Equivalent of the maven-source-plugin.
//java {
//    withSourcesJar()
//    withJavadocJar()
//}

// Ensure that building the project runs the shadowJar task.
tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}