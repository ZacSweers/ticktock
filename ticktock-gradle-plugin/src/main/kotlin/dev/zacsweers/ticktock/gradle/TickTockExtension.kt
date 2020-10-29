package dev.zacsweers.ticktock.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

/** Configuration for a the ticktock plugin. */
public abstract class TickTockExtension @Inject constructor(
    layout: ProjectLayout,
    objects: ObjectFactory
) {
  /** The IANA timezone data version */
  public val tzVersion: Property<String> = objects.property<String>()
      .convention("2020a")

  /** The output directory to generate tz data to. Defaults to src/main/resources.  */
  public val tzOutputDir: DirectoryProperty = objects.directoryProperty()
      .convention(layout.projectDirectory.dir("src/main/resources"))

  /** Output directory for generated code, if generating for lazy rules. */
  public abstract val codeOutputDir: DirectoryProperty

  /** The language to generate in if generating for lazy rules, either `java` or `kotlin`. */
  public val language: Property<String> = objects.property<String>()
      .convention("java")

  /** The package name to generate in if generating for lazy rules. */
  public val packageName: Property<String> = objects.property<String>()
      .convention("ticktock")
}