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

package org.uze.binary.protocol.media;

import org.uze.binary.protocol.api.ExternalPrinter;
import org.uze.binary.protocol.api.Printable;
import org.uze.binary.protocol.api.Types;
import org.uze.binary.protocol.api.WritableMedia;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Y.Kiselev on 01.09.2016.
 */
public abstract class AbstractWritableMedia implements WritableMedia {

    protected abstract void put(int value) throws IOException;

    protected abstract void putBytes(byte[] data, int offset, int length) throws IOException;

    protected abstract <T> ExternalPrinter<T> resolve(Class<T> clazz);

    private void putNull() throws IOException {
        putType(Types.NULL);
    }

    private void putType(byte type) throws IOException {
        putType(type, (byte) 0);
    }

    private void putType(byte type, byte subType) throws IOException {
        final int t = type & Types.MASK;
        final int st = subType & Types.MASK;
        if (t != type) {
            throw new IllegalArgumentException("Subtype in type value: " + type);
        }
        if (st != subType) {
            throw new IllegalArgumentException("Type in subtype value: " + subType);
        }
        put(t + (subType << 4));
    }

    /**
     * Store packed <b>positive</b> integer.
     * <p>
     * Stores value as 1-4 bytes depending on magnitude
     *
     * @param length the value to store. Must be positive.
     */
    private void putLength(int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be positive: " + length);
        }
        int l = length;
        put(l & 0xff);
        l >>>= 7;
        if (l != 0) {
            put(l & 0xff);
            l >>>= 7;
            if (l != 0) {
                put(l & 0xff);
                l >>>= 7;
                if (l != 0) {
                    put(l & 0xff);
                }
            }
        }
    }

    @Override
    public void putString(String value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.STRING);
            if (value.length() == 0) {
                putLength(0);
            } else {
                final byte[] bytes = value.getBytes(Charset.forName("UTF-8"));
                putLength(bytes.length);
                putBytes(bytes, 0, bytes.length);
            }
        }
    }

    @Override
    public void putByte(byte value) throws IOException {
        putType(Types.BYTE);
        put(value);
    }

    @Override
    public void putChar(char value) throws IOException {
        if (value <= Byte.MAX_VALUE) {
            putByte((byte) value);
        } else {
            putType(Types.CHAR);
            putInt16((short) value);
        }
    }

    @Override
    public void putShort(short value) throws IOException {
        if (value > Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            putByte((byte) value);
        } else {
            putType(Types.SHORT);
            putInt16(value);
        }
    }

    private void putInt16(short value) throws IOException {
        put(value & 0xff);
        put((value >>> 8) & 0xff);
    }

    private void putInt32(int value) throws IOException {
        put(value & 0xff);
        put((value >>> 8) & 0xff);
        put((value >>> 16) & 0xff);
        put((value >>> 24) & 0xff);
    }

    private void putInt64(long value) throws IOException {
        putInt32((int) value);
        putInt32((int) (value >>> 32));
    }

    @Override
    public void putInt(int value) throws IOException {
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            putShort((short) value);
        } else {
            putType(Types.INT);
            putInt32(value);
        }
    }

    @Override
    public void putLong(long value) throws IOException {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            putInt((int) value);
        } else {
            putType(Types.LONG);
            putInt64(value);
        }
    }

    @Override
    public void putFloat(float value) throws IOException {
        putType(Types.FLOAT);
        putInt32(Float.floatToRawIntBits(value));
    }

    @Override
    public void putDouble(double value) throws IOException {
        putType(Types.DOUBLE);
        putInt64(Double.doubleToRawLongBits(value));
    }

    private <T> void putValue(T value) throws IOException {
        if (value == null) {
            putNull();
        } else if (value instanceof Printable) {
            ((Printable) value).print(this);
        } else {
            final Class<T> clazz = (Class<T>) value.getClass();
            final ExternalPrinter<? super T> printer = resolve(clazz);//todo - why?
            if (printer == null) {
                throw new IOException("Unable to print class: " + clazz.getName());
            }
            printer.print(value, this);
        }
    }

    @Override
    public <T> void putObject(T value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.USER_TYPE);
            putValue(value);
        }
    }

    @Override
    public void putByteArray(byte[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.BYTE);
            putLength(value.length);
            putBytes(value, 0, value.length);
        }
    }

    @Override
    public void putCharArray(char[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.CHAR);
            putLength(value.length);
            for (char s : value) {
                putInt16((short) s);
            }
        }
    }

    @Override
    public void putShortArray(short[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.SHORT);
            putLength(value.length);
            for (short s : value) {
                putInt16(s);
            }
        }
    }

    @Override
    public void putIntArray(int[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.INT);
            putLength(value.length);
            for (int i : value) {
                putInt32(i);
            }
        }
    }

    @Override
    public void putLongArray(long[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.LONG);
            putLength(value.length);
            for (long l : value) {
                putInt64(l);
            }
        }
    }

    @Override
    public void putFloatArray(float[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.FLOAT);
            putLength(value.length);
            for (float f : value) {
                putInt32(Float.floatToRawIntBits(f));
            }
        }
    }

    @Override
    public void putDoubleArray(double[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.DOUBLE);
            putLength(value.length);
            for (double d : value) {
                putInt64(Double.doubleToRawLongBits(d));
            }
        }
    }

    @Override
    public <T> void putObjectArray(T[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.USER_TYPE);
            putLength(value.length);
            for (T item : value) {
                putValue(item);
            }
        }
    }
}
