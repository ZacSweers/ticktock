package org.threeten.bp.zone;

import java.io.DataInput;

public final class ZoneRulesReaderCompat {
  public static ZoneRules readZoneRules(DataInput input) throws Exception {
    return StandardZoneRules.readExternal(input);
  }
}