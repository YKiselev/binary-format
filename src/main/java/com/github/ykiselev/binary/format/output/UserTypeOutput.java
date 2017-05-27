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

import com.github.ykiselev.binary.format.WritableMedia;

import java.io.IOException;

/**
 * Created by Y.Kiselev on 02.09.2016.
 */
public interface UserTypeOutput {

    /**
     * This method is called by {@link WritableMedia} to serialize user type
     *
     * @param media target media to serialize to
     * @param value the value to serialize (can not be {@code null}!)
     * @throws IOException if I/O error occurred.
     */
    void write(WritableMedia media, Object value) throws IOException;

}
