package dev.zacsweers.ticktock.compiler;

import com.google.devtools.common.options.OptionsParser;
import org.threeten.bp.zone.ZoneRules;
import org.threeten.bp.zone.ZoneRulesCompat;
import java.util.SortedMap;

import static dev.zacsweers.ticktock.compiler.CompilerOptions.Language.JAVA;

public final class LazyZoneRulesCompiler {

  public static void main(String[] args) {
    OptionsParser parser = OptionsParser.newOptionsParser(CompilerOptions.class);
    parser.parseAndExitUponError(args);
    CompilerOptions options = parser.getOptions(CompilerOptions.class);
    if (options != null && options.validate()) {
      new LazyZoneRulesCompiler(options).run();
    }
  }

  private final String version;
  private final ZoneRulesCompat compiler;
  private final RulesWriter rulesWriter;
  private final ZoneWriter zoneWriter;
  private final String packageName;

  private LazyZoneRulesCompiler(CompilerOptions o) {
    version = o.version;
    compiler = new ZoneRulesCompat(version, o.tzdbFiles(), o.leapSecondFile(), o.verbose);
    rulesWriter =
        o.language == JAVA ? new JavaWriter(o.codeOutputDir) : new KotlinWriter(o.codeOutputDir);
    zoneWriter = new ZoneWriter(o.tzdbOutputDir);
    packageName = o.packageName;
  }

  private void run() {
    try {
      SortedMap<String, ZoneRules> zones = compiler.compile();
      rulesWriter.writeZoneIds(packageName, version, zones.keySet());
      zoneWriter.writeZones(zones);
    } catch (Exception ex) {
      System.out.println("Failed: " + ex.toString());
      ex.printStackTrace();
    }
  }
}