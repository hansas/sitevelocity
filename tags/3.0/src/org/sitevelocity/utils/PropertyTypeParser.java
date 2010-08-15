/*
 *  Copyright (c) 2010, Baturu.COM. All rights reserved.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.sitevelocity.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * parse a given class to find all its fields' Type, not including its inherited fields.
 * 
 * @author javafuns
 */
public class PropertyTypeParser {

    private Map<String, Class> mappings = new HashMap<String, Class>();

    private PropertyTypeParser() {
    }

    /**
     * parse a given class.
     * 
     * @param <T>
     * @param t
     * @return
     */
    public static <T> PropertyTypeParser parse(Class<T> t) {
        PropertyTypeParser typper = new PropertyTypeParser();
        Field[] fields = t.getDeclaredFields();
        for (Field field : fields) {
            typper.mappings.put(field.getName(), field.getType());
        }

        return typper;
    }

    /**
     * get a filed's type.
     * 
     * @param field
     * @return
     */
    public Class getType(String field) {
        return (mappings.get(field) == null ? String.class : mappings.get(field));
    }

    /**
     * cast a string to a given prime type wrapper class.
     * 
     * @param propertyType
     * @param value
     * @return
     */
    public Object cast(Class propertyType, String value) {
        if (value == null) {
            return null;
        }
        if ("java.lang.String".equals(propertyType.getName())) {
            return value;
        }
        if ("int".equals(propertyType.getName()) || "java.lang.Integer".equals(propertyType.getName())) {
            return Integer.parseInt(value);
        }
        if ("short".equals(propertyType.getName()) || "java.lang.Short".equals(propertyType.getName())) {
            return Short.parseShort(value);
        }
        if ("boolean".equals(propertyType.getName()) || "java.lang.Boolean".equals(propertyType.getName())) {
            return Boolean.parseBoolean(value);
        }
        if (("char".equals(propertyType.getName()) || "java.lang.Character".equals(propertyType.getName()))
                && (value.length() == 1)) {
            return value.charAt(0);
        }
        if ("double".equals(propertyType.getName()) || "java.lang.Double".equals(propertyType.getName())) {
            return Double.parseDouble(value);
        }
        if ("float".equals(propertyType.getName()) || "java.lang.Float".equals(propertyType.getName())) {
            return Float.parseFloat(value);
        }
        if ("long".equals(propertyType.getName()) || "java.lang.Long".equals(propertyType.getName())) {
            return Long.parseLong(value);
        }
        if ("java.util.Date".equals(propertyType.getName())) {
            try {
                return new SimpleDateFormat(SiteVelocityConfiguration.getInstance().getDateFromat()).parse(value);
            } catch (ParseException ex) {
                throw new ClassCastException("Failed to cast [" + value
                        + "] to type [" + propertyType.getName() + "]");
            }
        }

        throw new ClassCastException("Failed to cast [" + value
                + "] to type [" + propertyType.getName() + "]");
    }

    public static void main(String[] args) {
        PropertyTypeParser type = new PropertyTypeParser();
        System.out.println(type.cast(java.util.Date.class, "2010-01-25 23:39:40"));
    }
}
