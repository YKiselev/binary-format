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

import com.github.ykiselev.binary.format.input.PrimitiveBinaryInput;

import java.io.IOException;

/**
 * Created by Y.Kiselev on 04.09.2016.
 */
public final class ObjectBytes {

    private static final byte[] EMPTY_ARRAY = new byte[0];

    private final PrimitiveBinaryInput input;

    private byte[] buffer = EMPTY_ARRAY;

    private byte[] result = EMPTY_ARRAY;

    private int depth;

    public ObjectBytes(PrimitiveBinaryInput input) {
        this.input = input;
    }

    private static int refine(int capacity) {
        if (capacity < 1024) {
            capacity = 1024;
        }
        return capacity;
    }

    private void ensureBuffer(int capacity) {
        if (this.buffer.length < capacity) {
            this.buffer = new byte[refine(capacity)];
        }
    }

    private void ensureResult(int capacity) {
        if (this.result.length < capacity) {
            this.result = new byte[refine(capacity)];
        }
    }

    private void append(byte[] data, int count) {
        // todo
    }

    private void append(int data) {
        // todo
    }

    private void copy(int length) throws IOException {
        ensureBuffer(length);
        this.input.read(this.buffer, length);
        append(this.buffer, length);
    }

    private void copyValue(int type) throws IOException {
        switch (type) {
            case Types.NULL:
                append(type);
                break;

            case Types.STRING:
                append(type);
                final int length = this.input.readLength();
                //?????????
                //todo
                break;

            case Types.BYTE:
                append(type);
                append(this.input.read());
                break;

            case Types.CHAR:
            case Types.SHORT:
                append(type);
                copy(2);
                break;

            case Types.USER_TYPE:
                this.depth++;
                // todo
                break;

            case Types.END_MARKER:
                this.depth--;
                break;

            default:
                if (Types.isArray(type)) {
                    final int subType = Types.subType(type);
                    switch (subType) {

                    }
                } else {
                    throw new IOException("Invalid type byte: " + type);
                }
        }
    }

    public byte[] read() throws IOException {
        int type;
        do {
            type = this.input.read();
            copyValue(type);
        } while (type != Types.END_MARKER || this.depth > 0);
        try {
            return this.result;
        } finally {
            this.result = EMPTY_ARRAY;
        }
    }
}
