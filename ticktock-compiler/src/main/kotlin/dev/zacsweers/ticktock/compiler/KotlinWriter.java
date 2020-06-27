package dev.zacsweers.ticktock.compiler;

import com.squareup.kotlinpoet.CodeBlock;
import com.squareup.kotlinpoet.CodeBlocks;
import com.squareup.kotlinpoet.FileSpec;
import com.squareup.kotlinpoet.KModifier;
import com.squareup.kotlinpoet.ParameterizedTypeName;
import com.squareup.kotlinpoet.PropertySpec;
import com.squareup.kotlinpoet.TypeName;
import com.squareup.kotlinpoet.TypeNames;
import com.squareup.kotlinpoet.TypeSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class KotlinWriter implements RulesWriter {

  private final Path outputDir;

  KotlinWriter(Path outputDir) {
    this.outputDir = outputDir;
  }

  @Override public void writeZoneIds(String packageName, String version, Set<String> zoneIds)
      throws IOException {
    TypeSpec typeSpec = TypeSpec.objectBuilder("LazyZoneRules")
        .addModifiers(KModifier.INTERNAL)
        .addProperty(version(version))
        .addProperty(regionId(zoneIds))
        .build();

    if (!Files.exists(outputDir)) {
      Files.createDirectories(outputDir);
    }
    FileSpec.get(packageName, typeSpec)
        .writeTo(outputDir);
  }

  private PropertySpec version(String version) {
    return PropertySpec.builder("VERSION", TypeNames.STRING, KModifier.CONST)
        .initializer("%S", version)
        .build();
  }

  private PropertySpec regionId(Set<String> allRegionIds) {
    CodeBlock.Builder builder = CodeBlock.builder()
        .add("listOf(\n⇥⇥");
    List<CodeBlock> blocks = allRegionIds.stream()
        .map(id -> CodeBlock.of("%S", id))
        .collect(Collectors.toList());

    CodeBlock joined = CodeBlocks.joinToCode(blocks, ",\n");
    builder.add(joined)
        .add("⇤⇤\n)");
    TypeName listType = ParameterizedTypeName.get(TypeNames.LIST, TypeNames.STRING);
    return PropertySpec.builder("REGION_IDS", listType)
        .initializer(builder.build())
        .build();
  }
}