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

package org.uze.binary.format;

//import org.apache.commons.io.output.ByteArrayOutputStream;
//import org.apache.commons.lang3.ArrayUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.uze.binary.format.api.ExternalPrinter;
import org.uze.binary.format.api.Printable;
import org.uze.binary.format.api.Types;
import org.uze.binary.format.api.WritableMedia;
import org.uze.binary.format.media.AbstractOutputStreamMedia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by Y.Kiselev on 26.06.2016.
 */
public class AbstractOutputStreamMediaTest {

    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    private final WritableMedia media = new AbstractOutputStreamMedia(bos) {
        @Override
        protected <T> ExternalPrinter<T> resolve(Class<T> clazz) {
            throw new UnsupportedOperationException("not implemented");
        }
    };

    private static byte type(int type, int subType) {
        return (byte) (type + (subType << 4));
    }

    @Test
    public void shouldPrintByte() throws Exception {
        this.media.putByte((byte) 127);
        assertArrayEquals(new byte[]{
                Types.BYTE, 127
        }, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintChar() throws Exception {
        this.media.putChar((char) 0xfff);
        assertArrayEquals(new byte[]{
                Types.CHAR, (byte) 0xff, 0x0f
        }, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintShort() throws Exception {
        this.media.putShort((short) 0xfff);
        assertArrayEquals(new byte[]{
                Types.SHORT, (byte) 0xff, 0x0f
        }, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintInt() throws Exception {
        this.media.putInt(0xfffff);
        assertArrayEquals(new byte[]{
                Types.INT, -1, -1, 15, 0
        }, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintLong() throws Exception {
        this.media.putLong(0xfffffffffL);
        assertArrayEquals(new byte[]{
                Types.LONG, -1, -1, -1, -1, 15, 0, 0, 0
        }, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintString() throws Exception {
        this.media.putString("Превед, Медвежуть!");
        assertArrayEquals(new byte[]{
                Types.STRING, 33,
                -48, -97, -47, -128, -48, -75, -48, -78, -48, -75,
                -48, -76, 44, 32, -48, -100, -48, -75, -48, -76,
                -48, -78, -48, -75, -48, -74, -47, -125, -47, -126,
                -47, -116, 33
        }, this.bos.toByteArray());
    }

    @Test
    public void shouldProduceCorrectBytes() throws Exception {
        final Object[] src = new Object[]{
                new Item((byte) 1, (short) 2, 3, 4, "alpha", 3.14f, 3.14),
                null,
                new Item((byte) -1, (short) -2, -3, -4, "бета", 3000000.14f, 300000000000.14)
        };
        this.media.putObjectArray(src);
        assertArrayEquals(
                new byte[]{
                        type(Types.ARRAY, Types.USER_TYPE),
                        3,
                        Types.BYTE, 1,
                        Types.BYTE, 2,
                        Types.BYTE, 3,
                        Types.BYTE, 4,
                        Types.STRING, 5, 'a', 'l', 'p', 'h', 'a',
                        Types.FLOAT, -61, -11, 72, 64,
                        Types.DOUBLE, 31, -123, -21, 81, -72, 30, 9, 64,
                        Types.NULL,
                        Types.BYTE, -1,
                        Types.BYTE, -2,
                        Types.BYTE, -3,
                        Types.BYTE, -4,
                        Types.STRING, 8, -48, -79, -48, -75, -47, -126, -48, -80,
                        Types.FLOAT, 1, 27, 55, 74,
                        Types.DOUBLE, -10, 8, 0, 46, 89, 118, 81, 66

                },
                bos.toByteArray()
        );
    }

    @Test
    public void shouldPrintBytes() throws Exception {
        final byte[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9, 100, Byte.MIN_VALUE, Byte.MAX_VALUE};
        this.media.putByteArray(src);
        final byte[] header = new byte[]{
                type(Types.ARRAY, Types.BYTE),
                12
        };
        assertArrayEquals(ArrayUtils.addAll(header, src), this.bos.toByteArray());
    }

    @Test
    public void shouldPrintChars() throws Exception {
        final char[] src = {'A', 'B', 'C', Character.MIN_VALUE, Character.MAX_VALUE};
        this.media.putCharArray(src);
        final byte[] expected = new byte[]{
                type(Types.ARRAY, Types.CHAR),
                5,
                65, 0,
                66, 0,
                67, 0,
                0, 0,
                (byte) 0xff, (byte) 0xff
        };
        assertArrayEquals(expected, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintShorts() throws Exception {
        final short[] src = {1, 2, 3, Short.MIN_VALUE, Short.MAX_VALUE};
        this.media.putShortArray(src);
        final byte[] expected = new byte[]{
                type(Types.ARRAY, Types.SHORT),
                5,
                1, 0,
                2, 0,
                3, 0,
                0, (byte) 0x80,
                (byte) 0xff, 0x7f
        };
        assertArrayEquals(expected, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintInts() throws Exception {
        final int[] src = {1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE};
        this.media.putIntArray(src);
        final byte[] expected = new byte[]{
                type(Types.ARRAY, Types.INT),
                5,
                1, 0, 0, 0,
                2, 0, 0, 0,
                3, 0, 0, 0,
                0, 0, 0, (byte) 0x80,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x7f
        };
        assertArrayEquals(expected, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintLongs() throws Exception {
        final long[] src = {1, 2, 3, Long.MIN_VALUE, Long.MAX_VALUE};
        this.media.putLongArray(src);
        final byte[] expected = new byte[]{
                type(Types.ARRAY, Types.LONG),
                5,
                1, 0, 0, 0, 0, 0, 0, 0,
                2, 0, 0, 0, 0, 0, 0, 0,
                3, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, (byte) 0x80,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x7f
        };
        assertArrayEquals(expected, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintFloats() throws Exception {
        final float[] src = {1f, 2f, 3f, Float.MIN_VALUE, Float.MAX_VALUE};
        this.media.putFloatArray(src);
        final byte[] expected = new byte[]{
                type(Types.ARRAY, Types.FLOAT),
                5,
                0, 0, -128, 63,
                0, 0, 0, 64,
                0, 0, 64, 64,
                1, 0, 0, 0,
                -1, -1, 0x7f, 0x7f
        };
        assertArrayEquals(expected, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintDouble() throws Exception {
        final double[] src = {1f, 2f, 3f, Double.MIN_VALUE, Double.MAX_VALUE};
        this.media.putDoubleArray(src);
        final byte[] expected = new byte[]{
                type(Types.ARRAY, Types.DOUBLE),
                5,
                0, 0, 0, 0, 0, 0, -16, 63,
                0, 0, 0, 0, 0, 0, 0, 64,
                0, 0, 0, 0, 0, 0, 8, 64,
                1, 0, 0, 0, 0, 0, 0, 0,
                -1, -1, -1, -1, -1, -1, -17, 0x7f
        };
        assertArrayEquals(expected, this.bos.toByteArray());
    }

    @Test
    public void shouldPrintObject() throws Exception {
        this.media.putObject(new Item(Byte.MAX_VALUE, Short.MAX_VALUE, 1, 2L, "name", 1f, 2.0));
        assertArrayEquals(
                new byte[]{
                        Types.USER_TYPE,
                        Types.BYTE, Byte.MAX_VALUE,
                        Types.SHORT, (byte) (Short.MAX_VALUE & 0xff), Short.MAX_VALUE >>> 8,
                        Types.BYTE, 1,
                        Types.BYTE, 2,
                        Types.STRING, 4, 'n', 'a', 'm', 'e',
                        Types.FLOAT, 0, 0, -128, 63,
                        Types.DOUBLE, 0, 0, 0, 0, 0, 0, 0, 64,
                },
                this.bos.toByteArray()
        );
    }
}

class Item implements Printable {

    private final byte b;

    private final short s;

    private final int id;

    private final long l;

    private final String name;

    private final float f;

    private final double d;

    public Item(byte b, short s, int id, long l, String name, float f, double d) {
        this.b = b;
        this.s = s;
        this.id = id;
        this.l = l;
        this.name = name;
        this.f = f;
        this.d = d;
    }

    @Override
    public void print(WritableMedia writableMedia) throws IOException {
        writableMedia.putByte(this.b);
        writableMedia.putShort(this.s);
        writableMedia.putInt(this.id);
        writableMedia.putLong(this.l);
        writableMedia.putString(this.name);
        writableMedia.putFloat(this.f);
        writableMedia.putDouble(this.d);
    }
}