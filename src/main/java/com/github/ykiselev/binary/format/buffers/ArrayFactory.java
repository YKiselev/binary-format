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

package com.github.ykiselev.binary.format.buffers;

/**
 * Created by Y.Kiselev on 07.09.2016.
 */
public interface ArrayFactory {

    /**
     * Provides array of bytes with required capacity. Actual length of returned array may be greater than {@code capacity}
     *
     * @param capacity the required size of array
     * @return the byte array with at least {@code capacity} length.
     */
    byte[] get(int capacity);
}
