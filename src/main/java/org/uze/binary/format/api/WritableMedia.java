/*
 * Copyright 2016 Yuriy Kiselev uze@yandex.ru
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uze.binary.format.api;

import java.io.IOException;

/**
 * Writable media
 *
 * Created by Y.Kiselev on 20.06.2016.
 */
public interface WritableMedia {

    void putString(String value) throws IOException;

    void putByte(byte value) throws IOException;

    void putChar(char value) throws IOException;

    void putShort(short value) throws IOException;

    void putInt(int value) throws IOException;

    void putLong(long value) throws IOException;

    void putFloat(float value) throws IOException;

    void putDouble(double value) throws IOException;

    <T> void putObject(T value) throws IOException;

    void putByteArray(byte[] value) throws IOException;

    void putCharArray(char[] value) throws IOException;

    void putShortArray(short[] value) throws IOException;

    void putIntArray(int[] value) throws IOException;

    void putLongArray(long[] value) throws IOException;

    void putFloatArray(float[] value) throws IOException;

    void putDoubleArray(double[] value) throws IOException;

    <T> void putObjectArray(T[] value) throws IOException;

}
