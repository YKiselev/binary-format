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

package com.github.ykiselev.binary.format.input;

import java.io.IOException;

/**
 * Created by Y.Kiselev on 05.09.2016.
 */
public final class DefaultPrimitiveBinaryInput implements PrimitiveBinaryInput {

    private final BinaryInput input;

    public DefaultPrimitiveBinaryInput(BinaryInput input) {
        this.input = input;
    }

    @Override
    public int read() throws IOException {
        return this.input.read();
    }

    @Override
    public void read(byte[] buffer, int length) throws IOException {
        this.input.read(buffer, length);
    }

    /**
     * Reads packed positive integer (1-4 bytes)
     *
     * @return the length (positive integer)
     */
    @Override
    public int readLength() throws IOException {
        int result = 0;
        int b = read();
        result += b;
        if ((b & 0x80) != 0) {
            b = read();
            result += b << 7;
            if ((b & 0x80) != 0) {
                b = read();
                result += b << 7;
                if ((b & 0x80) != 0) {
                    b = read();
                    result += b << 7;
                }
            }
        }
        return result;
    }

    @Override
    public short readInt16() throws IOException {
        return (short) (read() + (read() << 8));
    }

    @Override
    public int readInt32() throws IOException {
        return read() + (read() << 8) + (read() << 16) + (read() << 24);
    }

    @Override
    public long readInt64() throws IOException {
        return (long) read() + ((long) read() << 8) + ((long) read() << 16) + ((long) read() << 24) +
                ((long) read() << 32) + ((long) read() << 40) + ((long) read() << 48) + ((long) read() << 56);
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt32());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readInt64());
    }

}
