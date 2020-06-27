package dev.zacsweers.ticktock.compiler;

import static com.google.common.truth.Truth.assertThat;

import com.google.devtools.common.options.Options;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class) public class CompilerOptionsTest {

  private CompilerOptions options;

  @Before public void setup() throws Exception {
    options = Options.getDefaults(CompilerOptions.class);
    options.version = "2012d";
    options.srcDir = Files.createTempDirectory(null);
    for (String name : options.tzdbFileNames) {
      Files.createFile(options.srcDir.resolve(name));
    }
    options.tzdbOutputDir = Files.createTempDirectory(null);
    options.codeOutputDir = Files.createTempDirectory(null);
  }

  @Test public void tzdbFiles() {
    assertThat(options.tzdbFiles()).hasSize(options.tzdbFileNames.size());
    for (File file : options.tzdbFiles()) {
      assertThat(file.exists()).isTrue();
    }
  }

  @Test public void tzdbFilesOnlyExistingFiles() {
    List<String> names = new ArrayList<>(options.tzdbFileNames);
    names.add("blah");
    options.tzdbFileNames = names;
    assertThat(options.tzdbFiles()).hasSize(options.tzdbFileNames.size() - 1);
    for (File file : options.tzdbFiles()) {
      assertThat(file.exists()).isTrue();
    }
  }

  @Test public void noLeapSecondFile() {
    assertThat(options.leapSecondFile()).isNull();
  }

  @Test public void leapSecondFile() throws Exception {
    Files.createFile(options.srcDir.resolve(options.leapSecondFileName));
    assertThat(options.leapSecondFile()).isNotNull();
  }

  @Test public void allValid() {
    assertThat(options.validate()).isTrue();
  }

  @Test public void validateEmptyVersion() {
    options.version = "";
    assertThat(options.validate()).isFalse();
  }

  @Test public void validateNullSrcDir() {
    options.srcDir = null;
    assertThat(options.validate()).isFalse();
  }

  @Test public void validateNullCodeDir() {
    options.codeOutputDir = null;
    assertThat(options.validate()).isFalse();
  }

  @Test public void validateNullTzdbDir() {
    options.tzdbOutputDir = null;
    assertThat(options.validate()).isFalse();
  }
}