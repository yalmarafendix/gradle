package org.gradle.kotlin.dsl.support

import org.gradle.kotlin.dsl.embeddedKotlinVersion

import org.gradle.kotlin.dsl.fixtures.AbstractKotlinIntegrationTest

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat

import org.junit.Test


class EmbeddedKotlinProviderTest : AbstractKotlinIntegrationTest() {

    @Test
    fun `no extra dependencies are added to the buildscript classpath`() {

        val result = build("buildEnvironment")

        assertThat(result.output, containsString("No dependencies"))
    }

    @Test
    fun `embedded kotlin dependencies are pinned to the embedded version`() {

        withBuildScript("""
            buildscript {
                $repositoriesBlock
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-stdlib:1.0")
                    classpath("org.jetbrains.kotlin:kotlin-reflect:1.0")
                }
            }
        """)

        val result = build("buildEnvironment")

        listOf("stdlib", "reflect").forEach { module ->
            assertThat(result.output, containsString("org.jetbrains.kotlin:kotlin-$module:1.0 -> $embeddedKotlinVersion"))
        }
    }

    @Test
    fun `stdlib and reflect are pinned to the embedded kotlin version for requested plugins`() {
        withBuildScript("""
            plugins {
                kotlin("jvm") version "1.3.31"
            }
        """)

        val result = build("buildEnvironment")

        listOf("stdlib", "reflect").forEach { module ->
            assertThat(result.output, containsString("org.jetbrains.kotlin:kotlin-$module:1.3.31 -> $embeddedKotlinVersion"))
        }
    }

    @Test
    fun `compiler-embeddable is not pinned`() {
        withBuildScript("""
            buildscript {
                repositories {
                    gradlePluginPortal()
                }
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.31")
                }
            }
        """)

        val result = build("buildEnvironment")

        assertThat(result.output, containsString("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.31"))
        assertThat(result.output, not(containsString("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.31 ->")))
    }
}
