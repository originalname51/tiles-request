/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.request.reflect;

import static org.junit.Assert.*;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.apache.tiles.request.reflect.CannotInstantiateObjectException;
import org.apache.tiles.request.reflect.ClassUtil;
import org.junit.Test;

/**
 * Tests {@link ClassUtil}.
 *
 * @version $Rev$ $Date$
 */
public class ClassUtilTest {

    /**
     * The size of descriptor map.
     */
    private static final int MAP_SIZE = 3;

    /**
     * Test method for {@link org.apache.tiles.request.reflect.ClassUtil#collectBeanInfo(java.lang.Class, java.util.Map)}.
     */
    @Test
    public void testCollectBeanInfo() {
        Map<String, PropertyDescriptor> name2descriptor = new HashMap<String, PropertyDescriptor>();
        ClassUtil.collectBeanInfo(TestInterface.class, name2descriptor);
        assertEquals(MAP_SIZE, name2descriptor.size());
        PropertyDescriptor descriptor = name2descriptor.get("value");
        assertEquals("value", descriptor.getName());
        assertEquals(int.class, descriptor.getPropertyType());
        assertNotNull(descriptor.getReadMethod());
        assertNotNull(descriptor.getWriteMethod());
        descriptor = name2descriptor.get("value2");
        assertEquals("value2", descriptor.getName());
        assertEquals(long.class, descriptor.getPropertyType());
        assertNotNull(descriptor.getReadMethod());
        assertNull(descriptor.getWriteMethod());
        descriptor = name2descriptor.get("value3");
        assertEquals("value3", descriptor.getName());
        assertEquals(String.class, descriptor.getPropertyType());
        assertNull(descriptor.getReadMethod());
        assertNotNull(descriptor.getWriteMethod());
    }

    /**
     * Test method for {@link org.apache.tiles.request.reflect.ClassUtil#getClass(String, Class)}.
     * @throws ClassNotFoundException If something goes wrong.
     */
    @Test
    public void testGetClass() throws ClassNotFoundException {
        assertEquals(TestInterface.class, ClassUtil.getClass(
                TestInterface.class.getName(), Object.class));
    }

    /**
     * Test method for {@link org.apache.tiles.request.reflect.ClassUtil#getClass(String, Class)}.
     * @throws ClassNotFoundException If something goes wrong.
     */
    @Test(expected=ClassNotFoundException.class)
    public void testGetClassException() throws ClassNotFoundException {
        ClassUtil.getClass("this.class.does.not.Exist", Object.class);
    }

    /**
     * Test method for {@link org.apache.tiles.request.reflect.ClassUtil#instantiate(String, boolean)}.
     * @throws ClassNotFoundException If something goes wrong.
     */
    @Test
    public void testInstantiate() {
        assertNotNull(ClassUtil.instantiate(TestClass.class.getName(), true));
        assertNull(ClassUtil.instantiate("this.class.does.not.Exist", true));
    }

    /**
     * Test method for {@link org.apache.tiles.request.reflect.ClassUtil#instantiate(String, boolean)}.
     * @throws ClassNotFoundException If something goes wrong.
     */
    @Test
    public void testInstantiateOneParameter() {
        assertNotNull(ClassUtil.instantiate(TestClass.class.getName()));
    }

    /**
     * Test method for {@link org.apache.tiles.request.reflect.ClassUtil#instantiate(String)}.
     * @throws ClassNotFoundException If something goes wrong.
     */
    @Test(expected=CannotInstantiateObjectException.class)
    public void testInstantiateOneParameterException() {
        assertNotNull(ClassUtil.instantiate("this.class.does.not.Exist"));
    }

    /**
     * Test method for {@link org.apache.tiles.request.reflect.ClassUtil#instantiate(String)}.
     * @throws ClassNotFoundException If something goes wrong.
     */
    @Test(expected=CannotInstantiateObjectException.class)
    public void testInstantiateInstantiationException() {
        ClassUtil.instantiate(TestInterface.class.getName());
    }

    /**
     * Test method for {@link org.apache.tiles.request.reflect.ClassUtil#instantiate(String)}.
     * @throws ClassNotFoundException If something goes wrong.
     */
    @Test(expected=CannotInstantiateObjectException.class)
    public void testInstantiateIllegalAccessException() {
        ClassUtil.instantiate(TestPrivateClass.class.getName());
    }

    /**
     * Interface to be used as test.
     *
     * @version $Rev$ $Date$
     */
    public static interface TestInterface {

        /**
         * The value.
         *
         * @return The value.
         */
        int getValue();

        /**
         * The value.
         *
         * @param value The value.
         */
        void setValue(int value);

        /**
         * The value.
         *
         * @return The value.
         */
        long getValue2();

        /**
         * The value.
         *
         * @param value3 The value.
         */
        void setValue3(String value3);
    }

    public static class TestClass {
    }

    private static class TestPrivateClass {
    }
}
