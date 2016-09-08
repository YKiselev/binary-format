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

import com.github.ykiselev.binary.format.ReadableMedia;

import java.io.IOException;

/**
 * Created by Y.Kiselev on 03.09.2016.
 */
public interface UserTypeInput {

    /**
     * @param media the media to read user type from
     * @param clazz the class of user type or {@code null} if other mechanism used to distinguish types (for example - user type id)
     * @param <T> the user type
     * @return the de-serialized instance of user type
     * @throws IOException if I/O error occurred
     */
    <T> T read(ReadableMedia media, Class<T> clazz) throws IOException;
}
