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

/**
 * Each serialized property is stored as a pair of <b>header</b> and <b>data</b>.<br/>
 * Where <b>header</b> has size of one byte and <b>data</b> can vary from zero to N bytes.
 * Header consist of two parts - lower four bits encode property <b>type</b> (see below)
 * and higher four bits encode <b>element type</b> for arrays.
 * <p>
 * Created by Y.Kiselev on 26.06.2016.
 */
public final class Types {

    /**
     * Type mask, first four bits
     */
    public static final byte MASK = 0xf;

    /**
     * TYPE(1b), VALUE(1b)
     */
    public static final byte BYTE = 1;

    /**
     * TYPE(1b), VALUE(2b)
     */
    public static final byte CHAR = 2;

    /**
     * TYPE(1b), VALUE(2b)
     */
    public static final byte SHORT = 3;

    /**
     * TYPE(1b), VALUE(4b)
     */
    public static final byte INT = 4;

    /**
     * TYPE(1b), VALUE(8b)
     */
    public static final byte LONG = 5;

    /**
     * TYPE(1b), VALUE(4b)
     */
    public static final byte FLOAT = 6;

    /**
     * TYPE(1b), VALUE(8b)
     */
    public static final byte DOUBLE = 7;

    /**
     * UTF-8 string
     * TYPE(1b), LENGTH(1-4b), VALUE(n)
     */
    public static final byte STRING = 8;

    /**
     * TYPE(low four bits), ELEMENT_TYPE(high four bits), LENGTH_IN_ELEMENTS(1-4b), VALUE(n)
     */
    public static final byte ARRAY = 9;

    /**
     * TYPE(1b)
     */
    public static final byte NULL = 14;

    /**
     * TYPE(1b), VALUE(n)
     */
    public static final byte USER_TYPE = 15;

}
