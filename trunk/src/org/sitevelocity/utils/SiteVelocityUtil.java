/*
 * Copyright (c) 2009, Baturu.COM. All rights reserved.
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

/**
 * Utility class.
 * @author javafuns
 */
public class SiteVelocityUtil {

    /**
     * get short name of a class.
     * @param clazz
     * @return
     */
    public static String getClassShortName(Class<?> clazz) {
        String className = clazz.getName();
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public static boolean isBlank(String input) {
        return (input == null || "".equals(input.trim()));
    }

    public static boolean isNotBlank(String input) {
        return !isBlank(input);
    }
}
