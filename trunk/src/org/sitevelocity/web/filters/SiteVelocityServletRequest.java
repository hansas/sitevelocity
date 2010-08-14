/*
 * Copyright (c) 2010, Baturu.COM. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package org.sitevelocity.web.filters;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.sitevelocity.exceptions.NotMatchedException;
import org.sitevelocity.web.filters.navigations.Navigator;
import org.sitevelocity.web.utils.WebUtil;

/**
 * Translate incoming request to meet internal requirements.
 *
 * @author javafuns
 */
public class SiteVelocityServletRequest extends HttpServletRequestWrapper {

    protected Hashtable localParams = null;
    private String newFullURL = "";

    public SiteVelocityServletRequest(HttpServletRequest request) throws ServletException {
        super(request);
        String fullURL = WebUtil.fullRequestURL(request);
        try {
            this.newFullURL = Navigator.getInstance().getPathTranslated(Navigator.getInstance().getMatchedRule(fullURL), fullURL);
        } catch (NotMatchedException ex) {
            throw new ServletException(ex);
        }

        // Copy the parameters from the underlying request.
        this.localParams = this.parseQueryString(this.newFullURL);
        Map wrappedParams = this.getRawRequest().getParameterMap();
        Set keySet = wrappedParams.keySet();
        for (Iterator it = keySet.iterator(); it.hasNext();) {
            Object key = it.next();
            Object value = wrappedParams.get(key);
            localParams.put(key, value);
        }
    }

    private Hashtable parseQueryString(String s) {
        String valArray[] = null;

        if (s == null) {
            throw new IllegalArgumentException();
        }
        Hashtable ht = new Hashtable();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = (String) st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                throw new IllegalArgumentException("Wrong key & value pairs.");
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1, pair.length()), sb);
            if (ht.containsKey(key)) {
                String oldVals[] = (String[]) ht.get(key);
                valArray = new String[oldVals.length + 1];
                for (int i = 0; i < oldVals.length; i++) {
                    valArray[i] = oldVals[i];
                }
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
            ht.put(key, valArray);
        }
        return ht;
    }

    /**
     * Parse a name in the query string.
     */
    private String parseName(String s, StringBuffer sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Not an illegal number.");
                    } catch (StringIndexOutOfBoundsException e) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() == 2) {
                            i++;
                        }
                    }

                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public HttpServletRequest getRawRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    @Override
    public String getContextPath() {
        return this.getRawRequest().getContextPath();
    }

    @Override
    public String getPathInfo() {
        int index = this.newFullURL.indexOf("?");
        return index > 0 ? this.newFullURL.substring(0, this.newFullURL.indexOf("?")) : this.newFullURL;
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getQueryString() {
        int index = this.newFullURL.indexOf("?");
        return index > 0 ? this.newFullURL.substring(index + 1) : null;
    }

    @Override
    public String getRequestURI() {
        return this.getContextPath() + this.getPathInfo();
    }

    @Override
    public StringBuffer getRequestURL() {
        HttpServletRequest req = this.getRawRequest();
        StringBuffer requestURL = new StringBuffer(req.getScheme());
        requestURL.append("://").append(req.getServerName());
        if (("http".equals(req.getScheme()) && req.getServerPort() != 80)
                || ("https".equals(req.getScheme()) && req.getServerPort() != 443)) {
            requestURL.append(':').append(req.getServerPort());
        }
        requestURL.append(this.getRequestURI());
        return requestURL;
    }

    @Override
    public String getServletPath() {
        return "";
    }

    public void setParameter(String name, String[] values) {
        localParams.put(name, values);
    }

    @Override
    public String getParameter(String name) {
        if (localParams == null) {
            return this.getRawRequest().getParameter(name);
        }
        Object val = localParams.get(name);
        if (val instanceof String) {
            return (String) val;
        }
        if (val instanceof String[]) {
            String[] values = (String[]) val;
            return values[0];
        }
        return (val == null ? null : val.toString());
    }

    @Override
    public String[] getParameterValues(String name) {
        if (localParams == null) {
            return this.getRawRequest().getParameterValues(name);
        }
        return (String[]) localParams.get(name);
    }

    @Override
    public Enumeration getParameterNames() {
        if (localParams == null) {
            return this.getRawRequest().getParameterNames();
        }
        return localParams.keys();
    }

    @Override
    public Map getParameterMap() {
        if (localParams == null) {
            return this.getRawRequest().getParameterMap();
        }
        return localParams;
    }

}
