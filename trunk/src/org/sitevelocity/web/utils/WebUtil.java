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
package org.sitevelocity.web.utils;

import org.sitevelocity.exceptions.NoSuchEntityException;
import org.sitevelocity.exceptions.ServerInternalException;
import org.sitevelocity.utils.SiteVelocityUtil;
import org.sitevelocity.utils.PropertyTypeParser;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

/**
 * extract from request and convert to bean.
 * 
 * @author javafuns
 */
public class WebUtil {

    /**
     * Get the form bean.
     * e.g. param name: OpenPost_title stands for it's 'title' for OpenPost bean.
     */
    public static <T> T get(Class<T> t, HttpServletRequest request)
            throws NoSuchEntityException, ServerInternalException {
        PropertyTypeParser typeParser = PropertyTypeParser.parse(t);

        T bean = null;
        try {
            bean = t.newInstance();
        } catch (InstantiationException ex) {
            throw new NoSuchEntityException(t.getName() + " not exists or can't be instantiated ", ex);
        } catch (IllegalAccessException ex) {
            throw new NoSuchEntityException(t.getName() + " not exists or can't be instantiated ", ex);
        }
        String beanName = SiteVelocityUtil.getClassShortName(t);
        Enumeration<String> paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String key = paramNames.nextElement();
            if (key != null && key.startsWith(beanName)) {
                String property = key.substring((beanName + ".").length());
                Class propertyType = typeParser.getType(property);
                Object value = typeParser.cast(propertyType, request.getParameter(key));
                populate(bean, propertyType, property, value);
            }
        }
        return bean;
    }

    private static <T> void populate(T t, Class propertyType, String property, Object value)
            throws ServerInternalException {
        String methodName = "set" + String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
        Method method = null;
        try {
            method = t.getClass().getDeclaredMethod(methodName, propertyType);
        } catch (NoSuchMethodException ex) {
            throw new ServerInternalException(ex);
        } catch (SecurityException ex) {
            throw new ServerInternalException(ex);
        }

        try {
            method.invoke(t, value);
        } catch (IllegalAccessException ex) {
            throw new ServerInternalException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ServerInternalException(ex);
        } catch (InvocationTargetException ex) {
            throw new ServerInternalException(ex);
        }
    }

    /**
     * translate view path or action path to real view or action class.
     * e.g. /aaa/bb/index or aaa/bb/save.
     * @param viewPathWithoutExtension
     * @return
     */
    public static String uriToClassName(String viewPathWithoutExtension) {
        if (viewPathWithoutExtension.startsWith("/")) {
            viewPathWithoutExtension = viewPathWithoutExtension.substring(1);
        }

        viewPathWithoutExtension = viewPathWithoutExtension.replace('/', '.');
        String prefix = viewPathWithoutExtension.replaceFirst("(\\w+)$", "");
        String suffix = viewPathWithoutExtension.replaceAll("^((\\w+)(\\.+))*", "");

        return (prefix + Character.toUpperCase(suffix.charAt(0)) + suffix.substring(1));
    }

    /**
     * Get full URL, including query string, but without context path.
     * 
     * @param request
     * @return
     */
    public static String fullRequestURL(HttpServletRequest request) {
        StringBuilder fullURL = new StringBuilder(request.getRequestURI());
        fullURL.delete(0, request.getContextPath().length());
        String queryString = request.getQueryString();

        return queryString != null ? fullURL.append("?" + queryString).toString() : fullURL.toString();
    }
}
