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

package com.github.ykiselev.binary.format.output;

import java.io.IOException;

/**
 * Created by Y.Kiselev on 01.09.2016.
 */
public final class DefaultPrimitiveBinaryOutput implements PrimitiveBinaryOutput {

    private final BinaryOutput out;

    public DefaultPrimitiveBinaryOutput(BinaryOutput out) {
        this.out = out;
    }

    @Override
    public void write(int value) throws IOException {
        this.out.write(value);
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        this.out.write(data, offset, length);
    }

    /**
     * Store packed <b>positive</b> integer.
     * <p>
     * Stores value as 1-4 bytes depending on magnitude
     *
     * @param length the value to store. Must be positive.
     */
    @Override
    public void writeLength(int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be positive: " + length);
        }
        int l = length;
        write(l & 0xff);
        l >>>= 7;
        if (l != 0) {
            write(l & 0xff);
            l >>>= 7;
            if (l != 0) {
                write(l & 0xff);
                l >>>= 7;
                if (l != 0) {
                    write(l & 0xff);
                }
            }
        }
    }

    @Override
    public void writeInt16(int value) throws IOException {
        write(value & 0xff);
        write((value >>> 8) & 0xff);
    }

    @Override
    public void writeInt32(int value) throws IOException {
        write(value & 0xff);
        write((value >>> 8) & 0xff);
        write((value >>> 16) & 0xff);
        write((value >>> 24) & 0xff);
    }

    @Override
    public void writeInt64(long value) throws IOException {
        writeInt32((int) value);
        writeInt32((int) (value >>> 32));
    }

    @Override
    public void writeFloat(float value) throws IOException {
        writeInt32(Float.floatToRawIntBits(value));
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writeInt64(Double.doubleToRawLongBits(value));
    }

}
