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

/**
 * Array factory implementation which lazily creates byte array and tries to re-use it.
 * This class is not thread safe.
 *
 * @author Y.Kiselev.
 */
public final class SimpleArrayFactory implements ArrayFactory {

    private final int minLength;

    private byte[] array;

    /**
     * @param minLength the minimal length of array
     */
    public SimpleArrayFactory(int minLength) {
        this.minLength = minLength;
    }

    /**
     * @param capacity the required capacity of array
     * @return the refined length of array
     */
    private int refine(int capacity) {
        int result = this.minLength;
        while (result < capacity) {
            result *= 2;
        }
        return result;
    }

    @Override
    public byte[] get(int capacity) {
        if (this.array == null || this.array.length < capacity) {
            this.array = new byte[refine(capacity)];
        }
        return this.array;
    }
}
