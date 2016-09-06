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

package com.github.ykiselev.binary.format.media;

import com.github.ykiselev.binary.format.ReadableMedia;
import com.github.ykiselev.binary.format.Types;
import com.github.ykiselev.binary.format.input.PrimitiveBinaryInput;
import com.github.ykiselev.binary.format.input.UserTypeInput;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;

/**
 * Created by Y.Kiselev on 01.09.2016.
 */
public final class SimpleReadableMedia implements ReadableMedia {

    private final PrimitiveBinaryInput input;

    private final UserTypeInput userTypeInput;

    public SimpleReadableMedia(PrimitiveBinaryInput input, UserTypeInput userTypeInput) {
        this.input = input;
        this.userTypeInput = userTypeInput;
    }

    private int read() throws IOException {
        return this.input.read();
    }

    @Override
    public String readString() throws IOException {
        final String result;
        final int type = read();
        if (type == Types.NULL) {
            result = null;
        } else if (type == Types.STRING) {
            final int length = this.input.readLength();
            if (length == 0) {
                result = "";
            } else {
                final byte[] tmp = new byte[length];
                this.input.read(tmp, length);
                result = new String(tmp, Charset.forName("UTF-8"));
            }
        } else {
            throw new IOException("Not a string: " + type);
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
                result = (char) this.input.readInt16();
                break;

            default:
                throw new IOException("Not a char-compatible type: " + type);
        }
        return result;
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
                result = this.input.readInt16();
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
                result = this.input.readInt16();
                break;

            case Types.INT:
                result = this.input.readInt32();
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
                result = this.input.readInt16();
                break;

            case Types.INT:
                result = this.input.readInt32();
                break;

            case Types.LONG:
                result = this.input.readInt64();
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
        return this.input.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        final int type = read();
        if (type != Types.DOUBLE) {
            throw new IOException("Not a double: " + type);
        }
        return this.input.readDouble();
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
        if (!Types.isArray(value)) {
            throw new IOException("Not an array: " + value);
        }
        if (Types.subType(value) != expectedSubType) {
            throw new IOException("Array item type mismatch: " + value);
        }
    }

    @Override
    public byte[] readByteArray() throws IOException {
        ensureArray(read(), Types.BYTE);
        final int length = this.input.readLength();
        final byte[] result = new byte[length];
        this.input.read(result, length);
        return result;
    }

    @Override
    public char[] readCharArray() throws IOException {
        ensureArray(read(), Types.CHAR);
        final int length = this.input.readLength();
        final char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = (char) this.input.readInt16();
        }
        return result;
    }

    @Override
    public short[] readShortArray() throws IOException {
        ensureArray(read(), Types.SHORT);
        final int length = this.input.readLength();
        final short[] result = new short[length];
        for (int i = 0; i < length; i++) {
            result[i] = this.input.readInt16();
        }
        return result;
    }

    @Override
    public int[] readIntArray() throws IOException {
        ensureArray(read(), Types.INT);
        final int length = this.input.readLength();
        final int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = this.input.readInt32();
        }
        return result;
    }

    @Override
    public long[] readLongArray() throws IOException {
        ensureArray(read(), Types.LONG);
        final int length = this.input.readLength();
        final long[] result = new long[length];
        for (int i = 0; i < length; i++) {
            result[i] = this.input.readInt64();
        }
        return result;
    }

    @Override
    public float[] readFloatArray() throws IOException {
        ensureArray(read(), Types.FLOAT);
        final int length = this.input.readLength();
        final float[] result = new float[length];
        for (int i = 0; i < length; i++) {
            result[i] = this.input.readFloat();
        }
        return result;
    }

    @Override
    public double[] readDoubleArray() throws IOException {
        ensureArray(read(), Types.DOUBLE);
        final int length = this.input.readLength();
        final double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = this.input.readDouble();
        }
        return result;
    }

    @Override
    public <T> T[] readObjectArray(Class<T> itemType) throws IOException {
        ensureArray(read(), Types.USER_TYPE);
        final int length = this.input.readLength();
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) Array.newInstance(itemType, length);
        for (int i = 0; i < length; i++) {
            result[i] = readObjectValue(itemType);
        }
        return result;
    }

    @Override
    public byte[] readRest() throws IOException {
        byte[] result = null;
//        byte[] buffer = null;
//        int type;
//        while ((type = read()) != Types.END_MARKER) {
//            if (type == Types.NULL) {
//                //todo
//                continue;
//            }
//            if (isArray(type)) {
//                final int subType = subType(type);
//                //todo
//            } else {
//                //todo
//            }
//        }
        return result;
    }
}
