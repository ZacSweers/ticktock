/*
 * Copyright (c) 2020 Zac Sweers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.zacsweers.ticktock.runtime;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.time.zone.ZoneRules;

/**
 * The shared serialization delegate for this package.
 *
 * <h4>Implementation notes</h4>
 *
 * This class is mutable and should be created once per serialization.
 *
 * @serial include
 */
final class SerCompat {

  /** Serialization version. */
  private static final long serialVersionUID = -8885321777449118786L;

  /** Type for StandardZoneRules. */
  static final byte SZR = 1;
  /** Type for ZoneOffsetTransition. */
  static final byte ZOT = 2;
  /** Type for ZoneOffsetTransition. */
  static final byte ZOTRULE = 3;

  static Object read(DataInput in) throws IOException {
    byte type = in.readByte();
    return readInternal(type, in);
  }

  private static Object readInternal(byte type, DataInput in) throws IOException {
    switch (type) {
      case SZR:
        return StandardZoneRules.readExternal(in);
      case ZOT:
        return readExternalFor(ZoneOffsetTransition.class, in);
      case ZOTRULE:
        return readExternalFor(ZoneOffsetTransitionRule.class, in);
      default:
        throw new StreamCorruptedException("Unknown serialized type");
    }
  }

  // Reflection is necessary for compatibility with D8
  static Object readExternalFor(Class<?> clazz, DataInput in) {
    Method method;
    try {
      method = clazz.getDeclaredMethod("readExternal", DataInput.class);
      method.setAccessible(true);
      return method.invoke(null, in);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Reads the state from the stream.
   *
   * @param in the input stream, not null
   * @return the created object, not null
   * @throws IOException if an error occurs
   */
  static ZoneOffset readOffset(DataInput in) throws IOException {
    int offsetByte = in.readByte();
    return (offsetByte == 127
        ? ZoneOffset.ofTotalSeconds(in.readInt())
        : ZoneOffset.ofTotalSeconds(offsetByte * 900));
  }

  /**
   * Reads the state from the stream.
   *
   * @param in the input stream, not null
   * @return the epoch seconds, not null
   * @throws IOException if an error occurs
   */
  static long readEpochSec(DataInput in) throws IOException {
    int hiByte = in.readByte() & 255;
    if (hiByte == 255) {
      return in.readLong();
    } else {
      int midByte = in.readByte() & 255;
      int loByte = in.readByte() & 255;
      long tot = ((hiByte << 16) + (midByte << 8) + loByte);
      return (tot * 900) - 4575744000L;
    }
  }
}
