package org.threeten.bp.zone;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

public final class ZoneRulesCompat {

    private final TzdbZoneRulesCompiler compiler;

    public ZoneRulesCompat(
            String version, List<File> sourceFiles, File leapSecondsFile, boolean verbose) {
        this.compiler = new TzdbZoneRulesCompiler(version, sourceFiles, leapSecondsFile, verbose);
        compiler.setDeduplicateMap(new HashMap<>());
    }

    public SortedMap<String, ZoneRules> compile() throws Exception {
        compiler.compile();
        return compiler.getZones();
    }

    public static void writeZoneRules(ZoneRules rules, DataOutputStream stream) throws IOException {
        ((StandardZoneRules) rules).writeExternal(stream);
    }
}