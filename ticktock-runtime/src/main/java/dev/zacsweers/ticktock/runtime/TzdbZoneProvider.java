/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dev.zacsweers.ticktock.runtime;

import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import dev.zacsweers.ticktock.runtime.ZoneDataProvider;
import dev.zacsweers.ticktock.runtime.ZoneIdsProvider;
import dev.zacsweers.ticktock.runtime.SerCompat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public final class TzdbZoneProvider implements ZoneIdsProvider, ZoneDataProvider {

  private String version;
  private List<String> regionIds;
  private List<ZoneRules> rules;
  private short[] ruleIndices;

  private final AtomicBoolean initialized = new AtomicBoolean();

  private final ZoneDataLoader zoneDataLoader;

  public TzdbZoneProvider(ZoneDataLoader zoneDataLoader) {
    this.zoneDataLoader = zoneDataLoader;
  }

  @Override
  public String getVersionId() {
    checkInitialized();
    return version;
  }

  @Override
  public List<String> getZoneIds() {
    checkInitialized();
    return regionIds;
  }

  @Override
  public ZoneRules getZoneRules(String zoneId) {
    checkInitialized();
    int regionIndex = Collections.binarySearch(regionIds, zoneId);
    if (regionIndex < 0) {
      return null;
    }
    int index = ruleIndices[regionIndex];
    return rules.get(index);
  }

  private void checkInitialized() {
    if (initialized.compareAndSet(false, true)) {
      try(DataInputStream dis = zoneDataLoader.openData("j$/time/zone/tzdb.dat")) {
        load(dis);
      } catch (Exception ex) {
        throw new ZoneRulesException("Unable to load TZDB time-zone rules", ex);
      }
    }
  }

  private void load(DataInputStream dis) throws IOException {
    if (dis.readByte() != 1) {
      throw new StreamCorruptedException("File format not recognised");
    }
    // group
    String groupId = dis.readUTF();
    if (!"TZDB".equals(groupId)) {
      throw new StreamCorruptedException("File format not recognised");
    }

    // versions
    int versionCount = dis.readShort();
    if (versionCount == 0) {
      throw new ZoneRulesException("No TZDB time-zone rules version found");
    }
    if (versionCount > 1) {
      throw new ZoneRulesException("Multiple TZDB time-zone rules versions are not supported");
    }
    version = dis.readUTF();

    // regions
    int regionCount = dis.readShort();
    String[] regionArray = new String[regionCount];
    for (int i = 0; i < regionCount; i++) {
      regionArray[i] = dis.readUTF();
    }

    // rules
    int ruleCount = dis.readShort();
    ZoneRules[] ruleArray = new ZoneRules[ruleCount];
    for (int i = 0; i < ruleCount; i++) {
      dis.readShort();
      ruleArray[i] = (ZoneRules) SerCompat.read(dis);
    }
    rules = Arrays.asList(ruleArray);

    // link version-region-rules
    // simplified because we are just support one version
    int versionRegionCount = dis.readShort();
    String[] regionIds = new String[versionRegionCount];
    short[] ruleIndices = new short[versionRegionCount];
    for (int i = 0; i < versionRegionCount; i++) {
      regionIds[i] = regionArray[dis.readShort()];
      ruleIndices[i] = dis.readShort();
    }
    this.regionIds = Arrays.asList(regionIds);
    this.ruleIndices = ruleIndices;
  }
}
