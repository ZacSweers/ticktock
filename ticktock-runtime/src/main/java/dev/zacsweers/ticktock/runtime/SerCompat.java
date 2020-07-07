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

import java.io.DataInput;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;

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
