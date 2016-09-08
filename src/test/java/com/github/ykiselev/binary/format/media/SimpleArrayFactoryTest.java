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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Yuriy Kiselev uze@yandex.ru
 */
public final class SimpleArrayFactoryTest {

    private final ArrayFactory factory = new SimpleArrayFactory(16);

    @Test
    public void shouldAllocate16bytes() throws Exception {
        Assert.assertEquals(16, factory.get(0).length);
    }

    @Test
    public void shouldReUseSameArrayInstance() throws Exception {
        Assert.assertSame(factory.get(5), factory.get(15));
    }

    @Test
    public void shouldAllocate32bytes() throws Exception {
        Assert.assertEquals(32, factory.get(17).length);
    }

}