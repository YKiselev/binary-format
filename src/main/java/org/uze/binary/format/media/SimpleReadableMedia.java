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

package org.uze.binary.format.media;

import org.uze.binary.format.ReadableMedia;
import org.uze.binary.format.Types;
import org.uze.binary.format.input.BinaryInput;
import org.uze.binary.format.input.UserTypeInput;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;

/**
 * Created by Y.Kiselev on 01.09.2016.
 */
public final class SimpleReadableMedia implements ReadableMedia {

    private final BinaryInput input;

    private final UserTypeInput userTypeInput;

    public SimpleReadableMedia(BinaryInput input, UserTypeInput userTypeInput) {
        this.input = input;
        this.userTypeInput = userTypeInput;
    }

    private int read() throws IOException {
        return this.input.read();
    }

    private void read(byte[] buffer, int length) throws IOException {
        this.input.read(buffer, length);
    }

    /**
     * Reads packed positive integer (1-4 bytes)
     *
     * @return the length (positive integer)
     */
    private int readLength() throws IOException {
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
    public String readString() throws IOException {
        final String result;
        final int type = read();
        if (type == Types.NULL) {
            result = null;
        } else if (type == Types.STRING) {
            final int length = readLength();
            if (length == 0) {
                result = "";
            } else {
                final byte[] tmp = new byte[length];
                read(tmp, length);
                result = new String(tmp, Charset.forName("UTF-8"));
            }
        } else {
            throw new IOException("Wrong type: " + type);
        }
        return result;
    }

    @Override
    public byte readByte() throws IOException {
        final int type = read();
        if (type != Types.BYTE) {
            throw new IOException("Not a byte: " + type);
        }
        return (byte) read();
    }

    @Override
    public char readChar() throws IOException {
        final char result;
        final int type = read();
        switch (type) {
            case Types.BYTE:
                result = (char) read();
                break;

            case Types.CHAR:
                result = (char) readInt16();
                break;

            default:
                throw new IOException("Not a char-compatible type: " + type);
        }
        return result;
    }

    private short readInt16() throws IOException {
        return (short) (read() + (read() << 8));
    }

    private int readInt32() throws IOException {
        return read() + (read() << 8) + (read() << 16) + (read() << 24);
    }

    private long readInt64() throws IOException {
        return (long) read() + ((long) read() << 8) + ((long) read() << 16) + ((long) read() << 24) +
                ((long) read() << 32) + ((long) read() << 40) + ((long) read() << 48) + ((long) read() << 56);
    }

    @Override
    public short readShort() throws IOException {
        final short result;
        final int type = read();
        switch (type) {
            case Types.BYTE:
                result = (short) read();
                break;

            case Types.SHORT:
                result = readInt16();
                break;

            default:
                throw new IOException("Not a short-compatible type: " + type);
        }
        return result;
    }

    @Override
    public int readInt() throws IOException {
        final int result;
        final int type = read();
        switch (type) {
            case Types.BYTE:
                result = (short) read();
                break;

            case Types.SHORT:
                result = readInt16();
                break;

            case Types.INT:
                result = readInt32();
                break;

            default:
                throw new IOException("Not an int-compatible type: " + type);
        }
        return result;
    }

    @Override
    public long readLong() throws IOException {
        final long result;
        final int type = read();
        switch (type) {
            case Types.BYTE:
                result = (short) read();
                break;

            case Types.SHORT:
                result = readInt16();
                break;

            case Types.INT:
                result = readInt32();
                break;

            case Types.LONG:
                result = readInt64();
                break;

            default:
                throw new IOException("Not an int-compatible type: " + type);
        }
        return result;
    }

    @Override
    public float readFloat() throws IOException {
        final int type = read();
        if (type != Types.FLOAT) {
            throw new IOException("Not a float: " + type);
        }
        return Float.intBitsToFloat(readInt32());
    }

    @Override
    public double readDouble() throws IOException {
        final int type = read();
        if (type != Types.DOUBLE) {
            throw new IOException("Not a double: " + type);
        }
        return Double.longBitsToDouble(readInt64());
    }

    @Override
    public <T> T readObject(Class<T> clazz) throws IOException {
        final int type = read();
        if (type != Types.USER_TYPE) {
            throw new IOException("Not a user-type: " + type);
        }
        return readObjectValue(clazz);
    }

    private <T> T readObjectValue(Class<T> clazz) throws IOException {
        final T result = this.userTypeInput.read(this, clazz);
        final int endMarker = read();
        if (endMarker != Types.END_MARKER) {
            throw new IOException("Expected end marker: " + endMarker);
        }
        return result;
    }

    private void ensureArray(int value, int expectedSubType) throws IOException {
        if ((value & Types.MASK) != Types.ARRAY) {
            throw new IOException("Not an array: " + value);
        }
        if (((value >>> 4) & Types.MASK) != expectedSubType) {
            throw new IOException("Array item type mismatch: " + value);
        }
    }

    @Override
    public byte[] readByteArray() throws IOException {
        ensureArray(read(), Types.BYTE);
        final int length = readLength();
        final byte[] result = new byte[length];
        read(result, length);
        return result;
    }

    @Override
    public char[] readCharArray() throws IOException {
        ensureArray(read(), Types.CHAR);
        final int length = readLength();
        final char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = (char) readInt16();
        }
        return result;
    }

    @Override
    public short[] readShortArray() throws IOException {
        ensureArray(read(), Types.SHORT);
        final int length = readLength();
        final short[] result = new short[length];
        for (int i = 0; i < length; i++) {
            result[i] = readInt16();
        }
        return result;
    }

    @Override
    public int[] readIntArray() throws IOException {
        ensureArray(read(), Types.INT);
        final int length = readLength();
        final int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = readInt32();
        }
        return result;
    }

    @Override
    public long[] readLongArray() throws IOException {
        ensureArray(read(), Types.LONG);
        final int length = readLength();
        final long[] result = new long[length];
        for (int i = 0; i < length; i++) {
            result[i] = readInt64();
        }
        return result;
    }

    @Override
    public float[] readFloatArray() throws IOException {
        ensureArray(read(), Types.FLOAT);
        final int length = readLength();
        final float[] result = new float[length];
        for (int i = 0; i < length; i++) {
            result[i] = Float.intBitsToFloat(readInt32());
        }
        return result;
    }

    @Override
    public double[] readDoubleArray() throws IOException {
        ensureArray(read(), Types.DOUBLE);
        final int length = readLength();
        final double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = Double.longBitsToDouble(readInt64());
        }
        return result;
    }

    @Override
    public <T> T[] readObjectArray(Class<T> itemType) throws IOException {
        ensureArray(read(), Types.USER_TYPE);
        final int length = readLength();
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) Array.newInstance(itemType, length);
        for (int i = 0; i < length; i++) {
            result[i] = readObjectValue(itemType);
        }
        return result;
    }
}
