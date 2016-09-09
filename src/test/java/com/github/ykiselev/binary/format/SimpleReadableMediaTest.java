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

import com.github.ykiselev.binary.format.input.UserTypeInput;
import com.github.ykiselev.binary.format.input.InputStreamBinaryInput;
import com.github.ykiselev.binary.format.output.OutputStreamBinaryOutput;
import com.github.ykiselev.binary.format.buffers.SimpleArrayFactory;
import com.github.ykiselev.binary.format.media.SimpleReadableMedia;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Y.Kiselev on 29.06.2016.
 */
public class SimpleReadableMediaTest {

    private ReadableMedia media(byte[] data) {
        return new SimpleReadableMedia(
                new InputStreamBinaryInput(
                        new ByteArrayInputStream(data)
                ),
                new UserTypeInput() {
                    @Override
                    public <T> T read(ReadableMedia media, Class<T> clazz) throws IOException {
                        if (Entity.class.equals(clazz)) {
                            return clazz.cast(new Entity(media.readInt(), media.readString()));
                        }
                        throw new UnsupportedOperationException("Unknown class:" + clazz);
                    }
                }
        );
    }

    private static byte array(byte itemType) {
        return (byte) (Types.ARRAY + (itemType << 4));
    }

    @Test(expected = IOException.class)
    public void shouldFail() throws Exception {
        media(new byte[]{Types.SHORT, 1}).readByte();
    }

    @Test
    public void shouldReadByte() throws Exception {
        assertEquals(4, media(new byte[]{Types.BYTE, 4}).readByte());
    }

    @Test
    public void shouldReadChar() throws Exception {
        assertEquals('a', media(new byte[]{Types.CHAR, 97, 0}).readChar());
    }

    @Test
    public void shouldReadShort() throws Exception {
        assertEquals(1_000, media(new byte[]{Types.SHORT, -24, 0x03}).readShort());
    }

    @Test
    public void shouldReadInt() throws Exception {
        assertEquals(100_000, media(new byte[]{Types.INT, (byte) 0xa0, (byte) 0x86, 0x01, 0}).readInt());
    }

    @Test
    public void shouldReadLong() throws Exception {
        assertEquals(1_000_000_000_000L, media(new byte[]{Types.LONG, 0, 0x10, (byte) 0xa5, (byte) 0xd4, (byte) 0xe8, 0, 0, 0}).readLong());
    }

    @Test
    public void shouldReadFloat() throws Exception {
        assertEquals(3.14f, media(new byte[]{Types.FLOAT, -61, -11, 72, 64}).readFloat(), 0.001f);
    }

    @Test
    public void shouldReadDouble() throws Exception {
        assertEquals(3.14, media(new byte[]{Types.DOUBLE, 31, -123, -21, 81, -72, 30, 9, 64}).readDouble(), 0.001);
    }

    @Test
    public void shouldReadString() throws Exception {
        assertEquals(
                "Hello Колобок!",
                media(
                        new byte[]{
                                Types.STRING,
                                21,
                                72, 101, 108, 108, 111, 32, -48,
                                -102, -48, -66, -48, -69, -48, -66,
                                -48, -79, -48, -66, -48, -70, 33
                        }
                ).readString()
        );
    }

    @Test
    public void shouldReadByteArray() throws Exception {
        assertArrayEquals(
                new byte[]{1, -2, 3},
                media(new byte[]{array(Types.BYTE), 3, 1, -2, 3}).readByteArray()
        );
    }

    @Test
    public void shouldReadCharArray() throws Exception {
        assertArrayEquals(
                new char[]{'A', 'B', 'C'},
                media(new byte[]{array(Types.CHAR), 3, 65, 0, 66, 0, 67, 0}).readCharArray()
        );
    }

    @Test
    public void shouldReadShortArray() throws Exception {
        assertArrayEquals(
                new short[]{1, -2, 3},
                media(new byte[]{array(Types.SHORT), 3, 1, 0, (byte) 0xfe, (byte) 0xff, 3, 0}).readShortArray()
        );
    }

    @Test
    public void shouldReadIntArray() throws Exception {
        assertArrayEquals(
                new int[]{1, -2, 3},
                media(new byte[]{
                        array(Types.INT),
                        3,
                        1, 0, 0, 0,
                        (byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                        3, 0, 0, 0
                }).readIntArray()
        );
    }

    @Test
    public void shouldReadLongArray() throws Exception {
        assertArrayEquals(
                new long[]{1, -2, 3},
                media(new byte[]{
                        array(Types.LONG),
                        3,
                        1, 0, 0, 0, 0, 0, 0, 0,
                        (byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                        (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                        3, 0, 0, 0, 0, 0, 0, 0
                }).readLongArray()
        );
    }

    @Test
    public void shouldReadFloatArray() throws Exception {
        assertArrayEquals(
                new float[]{3.14f, -2f},
                media(new byte[]{
                        array(Types.FLOAT),
                        2,
                        -61, -11, 72, 64,
                        0, 0, 0, (byte) 0xc0
                }).readFloatArray(),
                0.001f
        );
    }

    @Test
    public void shouldReadDoubleArray() throws Exception {
        assertArrayEquals(
                new double[]{3.14, -2.0},
                media(new byte[]{
                        array(Types.DOUBLE),
                        2,
                        31, -123, -21, 81, -72, 30, 9, 64,
                        0, 0, 0, 0, 0, 0, 0, (byte) 0xc0
                }).readDoubleArray(),
                0.001
        );
    }

    @Test
    public void shouldReadObject() throws Exception {
        final ReadableMedia media = media(
                new byte[]{
                        array(Types.USER_TYPE),
                        2,
                        Types.BYTE, 1,
                        Types.STRING, 5, 'f', 'i', 'r', 's', 't',
                        Types.END_MARKER,
                        Types.BYTE, 2,
                        Types.STRING, 6, 's', 'e', 'c', 'o', 'n', 'd',
                        Types.END_MARKER
                }
        );
        assertArrayEquals(
                new Entity[]{
                        new Entity(1, "first"),
                        new Entity(2, "second")
                },
                media.readObjectArray(Entity.class)
        );
    }

    @Test
    public void shouldReadObjectArray() throws Exception {
        final ReadableMedia media = media(
                new byte[]{
                        Types.USER_TYPE,
                        Types.BYTE, 1,
                        Types.STRING, 5, 'f', 'i', 'r', 's', 't',
                        Types.END_MARKER
                }
        );
        assertEquals(
                new Entity(1, "first"),
                media.readObject(Entity.class)
        );
    }

    @Test
    public void shouldReadTheRest() throws Exception {
        final ReadableMedia media = media(
                new byte[]{
                        Types.BYTE, 1,
                        Types.CHAR, 'x', 0,
                        Types.SHORT, 33, 33,
                        Types.INT, 1, 2, 3, 4,
                        Types.LONG, 1, 2, 3, 4, 5, 6, 7, 8,
                        Types.FLOAT, 4, 3, 2, 1,
                        Types.DOUBLE, 1, 1, 1, 1, 2, 2, 2, 2,
                        Types.STRING, 3, 'a', 'b', 'c',
                        array(Types.USER_TYPE), 1,
                        Types.USER_TYPE,
                        Types.BYTE, 127,
                        Types.STRING, 3, 'x', 'y', 'z',
                        Types.END_MARKER,
                        Types.END_MARKER
                }
        );
        assertArrayEquals(
                new byte[]{
                        Types.BYTE, 1,
                        Types.CHAR, 'x', 0,
                        Types.SHORT, 33, 33,
                        Types.INT, 1, 2, 3, 4,
                        Types.LONG, 1, 2, 3, 4, 5, 6, 7, 8,
                        Types.FLOAT, 4, 3, 2, 1,
                        Types.DOUBLE, 1, 1, 1, 1, 2, 2, 2, 2,
                        Types.STRING, 3, 'a', 'b', 'c',
                        array(Types.USER_TYPE), 1,
                        Types.USER_TYPE,
                        Types.BYTE, 127,
                        Types.STRING, 3, 'x', 'y', 'z',
                        Types.END_MARKER
                },
                readRest(media)
        );
    }

    private static byte[] readRest(ReadableMedia media) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        media.readRest(new OutputStreamBinaryOutput(os), new SimpleArrayFactory(16));
        return os.toByteArray();
    }
}

final class Entity {

    private final int id;

    private final String name;

    Entity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id == entity.id &&
                Objects.equals(name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}