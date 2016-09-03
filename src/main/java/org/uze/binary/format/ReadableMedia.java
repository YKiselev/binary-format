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

import java.io.IOException;

/**
 * Created by Y.Kiselev on 26.06.2016.
 */
public interface ReadableMedia {

    String readString() throws IOException;

    byte readByte() throws IOException;

    char readChar() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    <T> T readObject(Class<T> type) throws IOException;

    byte[] readByteArray() throws IOException;

    char[] readCharArray() throws IOException;

    short[] readShortArray() throws IOException;

    int[] readIntArray() throws IOException;

    long[] readLongArray() throws IOException;

    float[] readFloatArray() throws IOException;

    double[] readDoubleArray() throws IOException;

    <T> T[] readObjectArray(Class<T> itemType) throws IOException;

}
