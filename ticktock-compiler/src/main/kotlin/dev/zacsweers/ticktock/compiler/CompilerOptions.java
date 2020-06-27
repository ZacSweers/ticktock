package dev.zacsweers.ticktock.compiler;

import com.google.devtools.common.options.Converter;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParsingException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class CompilerOptions extends OptionsBase {

  @Option(name = "version", help = "Version of the time zone data, e.g. 2017b.", defaultValue = "")
  public String version;

  @Option(name = "srcdir", help = "Directory containing the unpacked leapsecond and tzdb files.",
      defaultValue = "", converter = PathConverter.class)
  public Path srcDir;

  @Option(name = "tzdbfiles", help = "Names of the tzdb files to process.", defaultValue =
      "africa,antarctica,asia,australasia,backward,etcetera,europe,northamerica,southamerica",
      converter = StringListConverter.class)
  public List<String> tzdbFileNames;

  @Option(name = "leapfile", help = "Name of the leapsecond file to process.", defaultValue =
      "leapseconds")
  public String leapSecondFileName;

  @Option(name = "codeoutdir", help = "Output directory for the generated java code.",
      defaultValue = "", converter = PathConverter.class)
  public Path codeOutputDir;

  @Option(name = "tzdboutdir", help = "Output directory for the generated tzdb files.",
      defaultValue = "", converter = PathConverter.class)
  public Path tzdbOutputDir;

  @Option(name = "verbose", help = "Verbose output.", defaultValue = "false") public boolean
      verbose;

  @Option(name = "language", help = "Language output (java or kotlin).", defaultValue = "java",
      converter = LanguageConverter.class)
  public Language language;

  @Option(name = "packageName", help = "Package name to output with.", defaultValue = "com"
      + ".gabrielittner.threetenbp")
  public String packageName;

  private List<File> tzdbFiles;

  List<File> tzdbFiles() {
    if (tzdbFiles == null) {
      tzdbFiles = new ArrayList<>();
      for (String srcFileName : tzdbFileNames) {
        Path path = srcDir.resolve(srcFileName);
        if (Files.exists(path)) {
          tzdbFiles.add(path.toFile());
        }
      }
    }
    return tzdbFiles;
  }

  File leapSecondFile() {
    Path leapSecondsFile = srcDir.resolve(leapSecondFileName);
    if (!Files.exists(leapSecondsFile)) {
      System.out.println("Does not include leap seconds information.");
      return null;
    }
    return leapSecondsFile.toFile();
  }

  boolean validate() {
    boolean valid = true;
    if (version.isEmpty()) {
      required("version");
      valid = false;
    }
    if (codeOutputDir == null) {
      required("codeoutputdir");
      valid = false;
    }
    if (tzdbOutputDir == null) {
      required("tzdboutdir");
      valid = false;
    }
    if (srcDir == null) {
      required("srcdir");
      return false;
    }
    if (tzdbFiles().size() == 0) {
      System.out.println("Did not find any timezone files.");
      valid = false;
    }
    return valid;
  }

  private void required(String arg) {
    System.out.println(String.format("--%s is required.", arg));
  }

  public static final class PathConverter implements Converter<Path> {
    public PathConverter() {}

    @Override public Path convert(String input) throws OptionsParsingException {
      return input.isEmpty() ? null : Paths.get(input);
    }

    @Override public String getTypeDescription() {
      return "File path";
    }
  }

  public static final class StringListConverter implements Converter<List<String>> {
    public StringListConverter() {}

    @Override public List<String> convert(String input) throws OptionsParsingException {
      if (input.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.asList(input.split(","));
    }

    @Override public String getTypeDescription() {
      return "Comma separated list of strings";
    }
  }

  public enum Language {
    JAVA, KOTLIN
  }

  public static final class LanguageConverter implements Converter<Language> {

    @Override public Language convert(String input) {
      String uppercased = input.toUpperCase(Locale.US);
      return Language.valueOf(uppercased);
    }

    @Override public String getTypeDescription() {
      return "The target language to generate ('java' or 'kotlin')";
    }
  }
}