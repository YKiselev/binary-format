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
 * Low-level wrapper of output stream, etc
 *
 * Created by Y.Kiselev on 02.09.2016.
 */
public interface BinaryOutput {

    /**
     * Writes one byte to the output
     *
     * @param value the byte to write (lower 8 bits)
     * @throws IOException if I/O error occurred
     */
    void write(int value) throws IOException;

    /**
     * Writes specified number of bytes to the output
     *
     * @param data the array to read bytes from
     * @param offset the offset in supplied array to start reading from
     * @param length the number of bytes to read (exactly)
     * @throws IOException if I/O error occurred (for example - if number of actually written bytes is not equal to {@code length})
     */
    void write(byte[] data, int offset, int length) throws IOException;

}
