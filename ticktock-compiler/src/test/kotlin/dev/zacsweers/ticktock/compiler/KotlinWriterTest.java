package dev.zacsweers.ticktock.compiler;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.tschuchort.compiletesting.KotlinCompilation;
import com.tschuchort.compiletesting.SourceFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.intellij.lang.annotations.Language;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class) public class KotlinWriterTest {

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

  private static final String SOURCE_NAME = "ticktock/LazyZoneRules.kt";

  private Path outputDir;
  private KotlinWriter kotlinWriter;

  @Before public void setup() throws IOException {
    outputDir = tmpFolder.newFolder()
        .toPath();
    kotlinWriter = new KotlinWriter(outputDir);
  }

  private static SourceFile kotlinFile(String name, @Language("kotlin") String source) {
    return SourceFile.Companion.kotlin(name, source, false);
  }

  private String generatedSource(String version, String... zoneIds) throws Exception {
    kotlinWriter.writeZoneIds("ticktock",
        version,
        new LinkedHashSet<>(Arrays.asList(zoneIds)));
    Path output = outputDir.resolve(SOURCE_NAME);
    @Language("kotlin") String sourceString =
        new String(Files.readAllBytes(output), Charsets.UTF_8);
    return sourceString;
  }

  @Test public void writeZoneIds() throws Exception {
    String source = generatedSource("2010a", "Europe/Berlin", "UTC", "US/Pacific");

    @Language("kotlin") String expectedSource = ""
        + "package ticktock\n"
        + "\n"
        + "import kotlin.String\n"
        + "import kotlin.collections.List\n"
        + "\n"
        + "internal object LazyZoneRules {\n"
        + "  const val VERSION: String = \"2010a\"\n"
        + "\n"
        + "  val REGION_IDS: List<String> = listOf(\n"
        + "          \"Europe/Berlin\",\n"
        + "          \"UTC\",\n"
        + "          \"US/Pacific\"\n"
        + "      )\n"
        + "}\n";

    assertThat(source).isEqualTo(expectedSource);

    SourceFile expected = kotlinFile("KClass.kt", expectedSource);

    KotlinCompilation compilation = new KotlinCompilation();
    compilation.setSources(ImmutableList.of(expected));
    compilation.setMessageOutputStream(System.out);
    compilation.setInheritClassPath(true);
    compilation.setVerbose(false);
    KotlinCompilation.Result result = compilation.compile();
    assertThat(result.getExitCode()).isEqualTo(KotlinCompilation.ExitCode.OK);
  }
}