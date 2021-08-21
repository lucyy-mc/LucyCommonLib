/*
 * Copyright © 2021 Lucy Poulton
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

plugins {
    id("fabric-loom") version "0.8-SNAPSHOT"
}

val minecraftVersion = "1.17"
val yarnBuild = 13
val loaderVersion = "0.11.6"
val fabricApiVersion = "0.36.0+1.17"
val fabricPermissionsApiVersion = "0.1-SNAPSHOT"
val adventureFabricVersion = "4.1.0-SNAPSHOT"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings("net.fabricmc:yarn:${minecraftVersion}+build.${yarnBuild}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
    modApi("me.lucko:fabric-permissions-api:${fabricPermissionsApiVersion}")
    modImplementation("net.kyori:adventure-platform-fabric:${adventureFabricVersion}")

    api(include(project(":squirtgun-api")) {
        exclude("net.kyori")
    })
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.remapJar) { builtBy(tasks.remapJar) }
            artifact(tasks.sourcesJar) { builtBy(tasks.remapSourcesJar) }
            artifact(tasks.javadocJar) { builtBy(tasks.javadocJar) }

            pom {
                withXml {
                    val deps = asNode().appendNode("dependencies")
                    project(":squirtgun-api") {
                        val dep = deps.appendNode("dependency")
                        dep.appendNode("groupId", this.group)
                        dep.appendNode("artifactId", this.name)
                        dep.appendNode("version", this.version)
                        dep.appendNode("scope", "compile")
                    }
                }

                description.set("A multipurpose library designed for Minecraft: Java Edition plugins.")
                url.set("https://lucyy.me")
                name.set("squirtgun")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("lucyy-mc")
                        name.set("Lucy Poulton")
                        email.set("lucy@poulton.xyz")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/lucyy-mc/Squirtgun.git")
                    developerConnection.set("scm:git:git://github.com/lucyy-mc/Squirtgun.git")
                    url.set("https://github.com/lucyy-mc/Squirtgun")
                }
            }
        }
    }
}
