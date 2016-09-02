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

package org.uze.binary.format.api;

import java.io.IOException;

/**
 * Implementers of this interface are used as helpers to print classes which do not implement {@link Printable}
 *
 * Created by Y.Kiselev on 03.07.2016.
 */
public interface ExternalPrinter<T> {

    void print(T object, WritableMedia media) throws IOException;
}
