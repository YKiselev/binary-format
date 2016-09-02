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

package org.uze.binary.format.media;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Y.Kiselev on 26.06.2016.
 */
public abstract class AbstractInputStreamMedia extends AbstractReadableMedia {

    private final InputStream is;

    public AbstractInputStreamMedia(InputStream is) {
        this.is = is;
    }

    @Override
    protected int read() throws IOException {
        final int result = this.is.read();
        if (result == -1) {
            throw new IOException("Unexpected end of stream!");
        }
        return result;
    }

    @Override
    protected void read(byte[] buffer, int length) throws IOException {
        final int read = this.is.read(buffer, 0, length);
        if (read != length) {
            throw new IOException("Partial read: only " + read + " of " + length + " bytes!");
        }
    }

}
