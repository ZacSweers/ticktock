package dev.zacsweers.ticktock.compiler;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class JavaWriter implements RulesWriter {

  private final Path outputDir;

  JavaWriter(Path outputDir) {
    this.outputDir = outputDir;
  }

  @Override public void writeZoneIds(String packageName, String version, Set<String> zoneIds)
      throws IOException {
    TypeSpec typeSpec = TypeSpec.classBuilder("LazyZoneRules")
        .addModifiers(FINAL)
        .addField(version(version))
        .addField(regionId(zoneIds))
        .build();

    if (!Files.exists(outputDir)) {
      Files.createDirectories(outputDir);
    }
    JavaFile.builder(packageName, typeSpec)
        .build()
        .writeTo(outputDir);
  }

  private FieldSpec version(String version) {
    return FieldSpec.builder(String.class, "VERSION", STATIC, FINAL)
        .initializer("$S", version)
        .build();
  }

  private FieldSpec regionId(Set<String> allRegionIds) {
    CodeBlock.Builder builder = CodeBlock.builder()
        .add("$T.asList(\n$>$>", Arrays.class);
    Iterator<String> iterator = allRegionIds.iterator();
    while (iterator.hasNext()) {
      builder.add("$S", iterator.next());
      if (iterator.hasNext()) {
        builder.add(",\n");
      }
    }
    builder.add("$<$<)");
    TypeName listType = ParameterizedTypeName.get(List.class, String.class);
    return FieldSpec.builder(listType, "REGION_IDS", STATIC, FINAL)
        .initializer(builder.build())
        .build();
  }
}