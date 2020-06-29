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
package dev.zacsweers.ticktock.runtime.tzdb;

import dev.zacsweers.ticktock.runtime.ResourcesZoneDataLoader;
import dev.zacsweers.ticktock.runtime.TickTockPlugins;
import dev.zacsweers.ticktock.runtime.ZoneDataLoader;
import dev.zacsweers.ticktock.runtime.internal.SerCompat;
import dev.zacsweers.ticktock.runtime.internal.Suppliers;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Loads time-zone rules for 'TZDB'.
 * <p>
 * This class is public for the service loader to access.
 *
 * <h3>Specification for implementors</h3>
 * This class is immutable and thread-safe.
 */
public final class TzdbZoneRulesProvider extends ZoneRulesProvider {

  /**
   * All the regions that are available.
   */
  private List<String> regionIds;
  /**
   * All the versions that are available.
   */
  private final ConcurrentNavigableMap<String, Version> versions = new ConcurrentSkipListMap<>();

  private final Supplier<ZoneDataLoader> zoneDataLoader = Suppliers.memoize(() -> {
    Supplier<ZoneDataLoader> callable = TickTockPlugins.getZoneRulesLoader();
    if (callable == null) {
      // Default to using resources.
      return new ResourcesZoneDataLoader();
    } else {
      return callable.get();
    }
  });

  private final AtomicBoolean initialized = new AtomicBoolean();

  /**
   * Creates an instance.
   * Created by the {@code ServiceLoader}.
   *
   * @throws ZoneRulesException if unable to load
   */
  public TzdbZoneRulesProvider() {
    super();
    System.out.println("Initializing TzdbZoneRulesProvider");
  }

  private void checkInitialized() {
    if (initialized.compareAndSet(false, true)) {
      try {
        DataInputStream dis = zoneDataLoader.get()
            .openData("j$/time/zone/tzdb.dat");
        load(dis);
      } catch (Exception ex) {
        throw new ZoneRulesException("Unable to load TZDB time-zone rules", ex);
      }
    }
  }

  //-----------------------------------------------------------------------
  @Override protected Set<String> provideZoneIds() {
    checkInitialized();
    return new HashSet<>(regionIds);
  }

  @Override protected ZoneRules provideRules(String zoneId, boolean forCaching) {
    checkInitialized();
    requireNonNull(zoneId, "zoneId");
    ZoneRules rules = versions.lastEntry()
        .getValue()
        .getRules(zoneId);
    if (rules == null) {
      throw new ZoneRulesException("Unknown time-zone ID: " + zoneId);
    }
    return rules;
  }

  @Override protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
    TreeMap<String, ZoneRules> map = new TreeMap<>();
    for (Version version : versions.values()) {
      ZoneRules rules = version.getRules(zoneId);
      if (rules != null) {
        map.put(version.versionId, rules);
      }
    }
    return map;
  }

  //-------------------------------------------------------------------------

  /**
   * Loads the rules from an input stream.
   *
   * @param in the stream to load, not null, not closed after use
   * @throws IOException if an error occurs
   */
  private void load(DataInputStream in) throws IOException {
    Iterable<Version> loadedVersions = loadData(in);
    for (Version loadedVersion : loadedVersions) {
      // see https://github.com/ThreeTen/threetenbp/pull/28 for issue wrt
      // multiple versions of lib on classpath
      Version existing = versions.putIfAbsent(loadedVersion.versionId, loadedVersion);
      if (existing != null && !existing.versionId.equals(loadedVersion.versionId)) {
        throw new ZoneRulesException("Data already loaded for TZDB time-zone rules version: "
            + loadedVersion.versionId);
      }
    }
  }

  /**
   * Loads the rules from an input stream.
   *
   * @param dis the stream to load, not null, not closed after use
   * @throws IOException if an error occurs
   */
  private Iterable<Version> loadData(DataInputStream dis) throws IOException {
    if (dis.readByte() != 1) {
      throw new StreamCorruptedException("File format not recognised");
    }
    // group
    String groupId = dis.readUTF();
    if ("TZDB".equals(groupId) == false) {
      throw new StreamCorruptedException("File format not recognised");
    }
    // versions
    int versionCount = dis.readShort();
    String[] versionArray = new String[versionCount];
    for (int i = 0; i < versionCount; i++) {
      versionArray[i] = dis.readUTF();
    }
    // regions
    int regionCount = dis.readShort();
    String[] regionArray = new String[regionCount];
    for (int i = 0; i < regionCount; i++) {
      regionArray[i] = dis.readUTF();
    }
    regionIds = Arrays.asList(regionArray);
    // rules
    int ruleCount = dis.readShort();
    Object[] ruleArray = new Object[ruleCount];
    for (int i = 0; i < ruleCount; i++) {
      byte[] bytes = new byte[dis.readShort()];
      dis.readFully(bytes);
      ruleArray[i] = bytes;
    }
    AtomicReferenceArray<Object> ruleData = new AtomicReferenceArray<>(ruleArray);
    // link version-region-rules
    Set<Version> versionSet = new HashSet<>(versionCount);
    for (int i = 0; i < versionCount; i++) {
      int versionRegionCount = dis.readShort();
      String[] versionRegionArray = new String[versionRegionCount];
      short[] versionRulesArray = new short[versionRegionCount];
      for (int j = 0; j < versionRegionCount; j++) {
        versionRegionArray[j] = regionArray[dis.readShort()];
        versionRulesArray[j] = dis.readShort();
      }
      versionSet.add(new Version(versionArray[i], versionRegionArray, versionRulesArray, ruleData));
    }
    return versionSet;
  }

  @Override public String toString() {
    return "TZDB";
  }

  //-----------------------------------------------------------------------

  /**
   * A version of the TZDB rules.
   */
  static class Version {
    private final String versionId;
    private final String[] regionArray;
    private final short[] ruleIndices;
    private final AtomicReferenceArray<Object> ruleData;

    Version(String versionId,
        String[] regionIds,
        short[] ruleIndices,
        AtomicReferenceArray<Object> ruleData) {
      this.ruleData = ruleData;
      this.versionId = versionId;
      this.regionArray = regionIds;
      this.ruleIndices = ruleIndices;
    }

    ZoneRules getRules(String regionId) {
      int regionIndex = Arrays.binarySearch(regionArray, regionId);
      if (regionIndex < 0) {
        return null;
      }
      try {
        return createRule(ruleIndices[regionIndex]);
      } catch (Exception ex) {
        throw new ZoneRulesException("Invalid binary time-zone data: TZDB:"
            + regionId
            + ", version: "
            + versionId, ex);
      }
    }

    ZoneRules createRule(short index) throws Exception {
      Object obj = ruleData.get(index);
      if (obj instanceof byte[]) {
        byte[] bytes = (byte[]) obj;
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
        obj = SerCompat.read(dis);
        ruleData.set(index, obj);
      }
      return (ZoneRules) obj;
    }

    @Override public String toString() {
      return versionId;
    }
  }
}