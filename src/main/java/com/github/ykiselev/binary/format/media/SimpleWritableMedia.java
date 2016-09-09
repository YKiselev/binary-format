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

import com.github.ykiselev.binary.format.Types;
import com.github.ykiselev.binary.format.WritableMedia;
import com.github.ykiselev.binary.format.output.BinaryOutput;
import com.github.ykiselev.binary.format.output.UserTypeOutput;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Y.Kiselev on 01.09.2016.
 */
public final class SimpleWritableMedia implements WritableMedia {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final BinaryOutput out;

    private final UserTypeOutput userTypeOutput;

    public SimpleWritableMedia(BinaryOutput out, UserTypeOutput userTypeOutput) {
        this.out = out;
        this.userTypeOutput = userTypeOutput;
    }

    private void write(int value) throws IOException {
        this.out.write(value);
    }

    private void write(byte[] data, int offset, int length) throws IOException {
        this.out.write(data, offset, length);
    }

    /**
     * Store packed <b>positive</b> integer.
     * <p>
     * Stores value as 1-4 bytes depending on magnitude
     *
     * @param length the value to store. Must be positive.
     */
    private void writeLength(int length) throws IOException {
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

    private void writeInt16(int value) throws IOException {
        write(value & 0xff);
        write((value >>> 8) & 0xff);
    }

    private void writeInt32(int value) throws IOException {
        write(value & 0xff);
        write((value >>> 8) & 0xff);
        write((value >>> 16) & 0xff);
        write((value >>> 24) & 0xff);
    }

    private void writeInt64(long value) throws IOException {
        writeInt32((int) value);
        writeInt32((int) (value >>> 32));
    }

    private void writeFloat32(float value) throws IOException {
        writeInt32(Float.floatToRawIntBits(value));
    }

    private void writeFloat64(double value) throws IOException {
        writeInt64(Double.doubleToRawLongBits(value));
    }

    private void writeNull() throws IOException {
        writeType(Types.NULL);
    }

    private void writeType(byte type) throws IOException {
        write(type);
    }

//    private void writeType(byte type, byte subType) throws IOException {
//        final int t = type & Types.MASK;
//        final int st = subType & Types.MASK;
//        if (t != type) {
//            throw new IllegalArgumentException("Subtype in type value: " + type);
//        }
//        if (st != subType) {
//            throw new IllegalArgumentException("Type in subtype value: " + subType);
//        }
//        this.out.write(t + (subType << 4));
//    }

    @Override
    public void writeString(String value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.STRING);
            if (value.length() == 0) {
                writeLength(0);
            } else {
                final byte[] bytes = value.getBytes(UTF_8);
                writeLength(bytes.length);
                write(bytes, 0, bytes.length);
            }
        }
    }

    @Override
    public void writeByte(byte value) throws IOException {
        writeType(Types.BYTE);
        write(value);
    }

    @Override
    public void writeChar(char value) throws IOException {
        if (value <= Byte.MAX_VALUE) {
            writeByte((byte) value);
        } else {
            writeType(Types.CHAR);
            writeInt16(value);
        }
    }

    @Override
    public void writeShort(short value) throws IOException {
        if (value > Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            writeByte((byte) value);
        } else {
            writeType(Types.SHORT);
            writeInt16(value);
        }
    }

    @Override
    public void writeInt(int value) throws IOException {
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            writeShort((short) value);
        } else {
            writeType(Types.INT);
            writeInt32(value);
        }
    }

    @Override
    public void writeLong(long value) throws IOException {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            writeInt((int) value);
        } else {
            writeType(Types.LONG);
            writeInt64(value);
        }
    }

    @Override
    public void writeFloat(float value) throws IOException {
        writeType(Types.FLOAT);
        writeFloat32(value);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writeType(Types.DOUBLE);
        writeFloat64(value);
    }

    private <T> void writeValue(T value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            this.userTypeOutput.put(this, value);
            writeType(Types.END_MARKER);
        }
    }

    @Override
    public <T> void writeObject(T value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.USER_TYPE);
            writeValue(value);
        }
    }

    @Override
    public void writeByteArray(byte[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.array(Types.BYTE));
            writeLength(value.length);
            write(value, 0, value.length);
        }
    }

    @Override
    public void writeCharArray(char[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.array(Types.CHAR));
            writeLength(value.length);
            for (char s : value) {
                writeInt16((short) s);
            }
        }
    }

    @Override
    public void writeShortArray(short[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.array(Types.SHORT));
            writeLength(value.length);
            for (short s : value) {
                writeInt16(s);
            }
        }
    }

    @Override
    public void writeIntArray(int[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.array(Types.INT));
            writeLength(value.length);
            for (int i : value) {
                writeInt32(i);
            }
        }
    }

    @Override
    public void writeLongArray(long[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.array(Types.LONG));
            writeLength(value.length);
            for (long l : value) {
                writeInt64(l);
            }
        }
    }

    @Override
    public void writeFloatArray(float[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.array(Types.FLOAT));
            writeLength(value.length);
            for (float f : value) {
                writeFloat32(f);
            }
        }
    }

    @Override
    public void writeDoubleArray(double[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.array(Types.DOUBLE));
            writeLength(value.length);
            for (double d : value) {
                writeFloat64(d);
            }
        }
    }

    @Override
    public <T> void writeObjectArray(T[] value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            writeType(Types.array(Types.USER_TYPE));
            writeLength(value.length);
            for (T item : value) {
                writeValue(item);
            }
        }
    }

    @Override
    public void writeRest(byte[] blob) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }
}
