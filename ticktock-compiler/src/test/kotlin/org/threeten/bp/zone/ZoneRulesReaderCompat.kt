package org.threeten.bp.zone

import java.io.DataInput

object ZoneRulesReaderCompat {
  fun readZoneRules(input: DataInput?): ZoneRules {
    return StandardZoneRules.readExternal(input)
  }
}