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

package com.github.ykiselev.binary.format;

import java.io.IOException;

/**
 * Writable media
 * <p>
 * Created by Y.Kiselev on 20.06.2016.
 */
public interface WritableMedia {

    /**
     * Stores <b>positive</b> integer as a variable length (1-5) sequence of bytes.
     *
     * @param value the value to store. Must be positive.
     * @throws IOException if I/O error occurred.
     */
    void writePackedInteger(int value) throws IOException;

    void writeString(String value) throws IOException;

    void writeByte(byte value) throws IOException;

    void writeChar(char value) throws IOException;

    void writeShort(short value) throws IOException;

    void writeInt(int value) throws IOException;

    void writeLong(long value) throws IOException;

    void writeFloat(float value) throws IOException;

    void writeDouble(double value) throws IOException;

    <T> void writeObject(T value) throws IOException;

    void writeByteArray(byte[] value) throws IOException;

    void writeCharArray(char[] value) throws IOException;

    void writeShortArray(short[] value) throws IOException;

    void writeIntArray(int[] value) throws IOException;

    void writeLongArray(long[] value) throws IOException;

    void writeFloatArray(float[] value) throws IOException;

    void writeDoubleArray(double[] value) throws IOException;

    <T> void writeObjectArray(T[] value) throws IOException;

    /**
     * Writes byte blob of unknown data from next versions of model.
     * If used, this method should be called after all the <i>known</i> fields of user type are written.
     *
     * @param blob  the data to write
     * @param count the actual length of data in supplied blob
     * @throws IOException if I/O error occurred.
     */
    void writeRest(byte[] blob, int count) throws IOException;

}
