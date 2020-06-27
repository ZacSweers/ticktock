package dev.zacsweers.ticktock.compiler;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjectSubject.assertThat;

import com.google.common.base.Charsets;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import javax.tools.JavaFileObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class) public class JavaWriterTest {

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

  private static final String SOURCE_NAME = "ticktock/LazyZoneRules";

  private Path outputDir;
  private JavaWriter javaWriter;

  @Before public void setup() throws IOException {
    outputDir = tmpFolder.newFolder()
        .toPath();
    javaWriter = new JavaWriter(outputDir);
  }

  private JavaFileObject generatedSource(String version, String... zoneIds) throws Exception {
    javaWriter.writeZoneIds("ticktock",
        version,
        new LinkedHashSet<>(Arrays.asList(zoneIds)));
    Path output = outputDir.resolve(SOURCE_NAME + ".java");
    String sourceString = new String(Files.readAllBytes(output), Charsets.UTF_8);
    return JavaFileObjects.forSourceString(SOURCE_NAME, sourceString);
  }

  @Test public void writeZoneIds() throws Exception {
    JavaFileObject source = generatedSource("2010a", "Europe/Berlin", "UTC", "US/Pacific");
    JavaFileObject expected = JavaFileObjects.forSourceString(SOURCE_NAME,
        ""
            + "package ticktock;"
            + "\n"
            + "import java.lang.String;"
            + "import java.util.Arrays;"
            + "import java.util.List;"
            + "\n"
            + "final class LazyZoneRules {\n"
            + "    static final String VERSION = \"2010a\";\n"
            + "\n"
            + "    static final List<String> REGION_IDS = Arrays.asList(\n"
            + "            \"Europe/Berlin\",\n"
            + "            \"UTC\",\n"
            + "            \"US/Pacific\");\n"
            + "        }");

    Compilation compilation = javac().compile(source);
    assertThat(compilation).succeeded();
    assertThat(source).hasSourceEquivalentTo(expected);
  }
}