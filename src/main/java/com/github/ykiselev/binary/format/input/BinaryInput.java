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
 * Created by Y.Kiselev on 03.09.2016.
 */
public interface BinaryInput {

    /**
     * Reads one byte from underlying storage
     *
     * @return the next byte from storage (in range 0..255)
     * @throws IOException if an I/O error occurs
     */
    int read() throws IOException;

    /**
     * Reads {@code length} bytes from underlying storage
     *
     * @param buffer the buffer to copy bytes to
     * @param length number of bytes to read (exactly)
     * @throws IOException if an I/O error occurs
     */
    void read(byte[] buffer, int length) throws IOException;

}
