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
import com.github.ykiselev.binary.format.output.PrimitiveBinaryOutput;
import com.github.ykiselev.binary.format.output.UserTypeOutput;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Y.Kiselev on 01.09.2016.
 */
public final class SimpleWritableMedia implements WritableMedia {

    private final PrimitiveBinaryOutput out;

    private final UserTypeOutput userTypeOutput;

    public SimpleWritableMedia(PrimitiveBinaryOutput out, UserTypeOutput userTypeOutput) {
        this.out = out;
        this.userTypeOutput = userTypeOutput;
    }

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
        this.out.write(t + (subType << 4));
    }

    @Override
    public void writeString(String value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.STRING);
            if (value.length() == 0) {
                this.out.writeLength(0);
            } else {
                final byte[] bytes = value.getBytes(Charset.forName("UTF-8"));
                this.out.writeLength(bytes.length);
                this.out.write(bytes, 0, bytes.length);
            }
        }
    }

    @Override
    public void writeByte(byte value) throws IOException {
        putType(Types.BYTE);
        this.out.write(value);
    }

    @Override
    public void writeChar(char value) throws IOException {
        if (value <= Byte.MAX_VALUE) {
            writeByte((byte) value);
        } else {
            putType(Types.CHAR);
            this.out.writeInt16(value);
        }
    }

    @Override
    public void writeShort(short value) throws IOException {
        if (value > Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            writeByte((byte) value);
        } else {
            putType(Types.SHORT);
            this.out.writeInt16(value);
        }
    }

    @Override
    public void writeInt(int value) throws IOException {
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            writeShort((short) value);
        } else {
            putType(Types.INT);
            this.out.writeInt32(value);
        }
    }

    @Override
    public void writeLong(long value) throws IOException {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            writeInt((int) value);
        } else {
            putType(Types.LONG);
            this.out.writeInt64(value);
        }
    }

    @Override
    public void writeFloat(float value) throws IOException {
        putType(Types.FLOAT);
        this.out.writeFloat(value);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        putType(Types.DOUBLE);
        this.out.writeDouble(value);
    }

    private <T> void putValue(T value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            this.userTypeOutput.put(this, value);
            putType(Types.END_MARKER);
        }
    }

    @Override
    public <T> void writeObject(T value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.USER_TYPE);
            putValue(value);
        }
    }

    @Override
    public void writeByteArray(byte[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.BYTE);
            this.out.writeLength(value.length);
            this.out.write(value, 0, value.length);
        }
    }

    @Override
    public void writeCharArray(char[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.CHAR);
            this.out.writeLength(value.length);
            for (char s : value) {
                this.out.writeInt16((short) s);
            }
        }
    }

    @Override
    public void writeShortArray(short[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.SHORT);
            this.out.writeLength(value.length);
            for (short s : value) {
                this.out.writeInt16(s);
            }
        }
    }

    @Override
    public void writeIntArray(int[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.INT);
            this.out.writeLength(value.length);
            for (int i : value) {
                this.out.writeInt32(i);
            }
        }
    }

    @Override
    public void writeLongArray(long[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.LONG);
            this.out.writeLength(value.length);
            for (long l : value) {
                this.out.writeInt64(l);
            }
        }
    }

    @Override
    public void writeFloatArray(float[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.FLOAT);
            this.out.writeLength(value.length);
            for (float f : value) {
                this.out.writeFloat(f);
            }
        }
    }

    @Override
    public void writeDoubleArray(double[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.DOUBLE);
            this.out.writeLength(value.length);
            for (double d : value) {
                this.out.writeDouble(d);
            }
        }
    }

    @Override
    public <T> void writeObjectArray(T[] value) throws IOException {
        if (value == null) {
            putNull();
        } else {
            putType(Types.ARRAY, Types.USER_TYPE);
            this.out.writeLength(value.length);
            for (T item : value) {
                putValue(item);
            }
        }
    }

    @Override
    public void writeRest(byte[] blob) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }
}
