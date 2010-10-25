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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sitevelocity.exceptions.NotMatchedException;
import org.sitevelocity.utils.SiteVelocityConfiguration;
import org.sitevelocity.utils.SiteVelocityLogger;
import org.sitevelocity.web.filters.navigations.Navigator;
import org.sitevelocity.web.filters.navigations.RouteMapping;
import org.sitevelocity.web.utils.WebUtil;

/**
 * Translate incoming URL to internal path, according to Navigation Rule Configuration.
 *
 * @author javafuns
 */
public class PathTranslateFilter implements Filter {

    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        ServletRequest wrappedRequest = req;

        SiteVelocityLogger.getLogger(this.getClass().getName()).logInfo("The incoming request is : " + request.getRequestURI());
        try {
            if (Navigator.getInstance().getMatchedRule(WebUtil.fullRequestURL(request)) != null) {
                wrappedRequest = new SiteVelocityServletRequest((HttpServletRequest) request);
                RouteMapping route = this.getRouteMapping();
                request.setAttribute(SiteVelocityConfiguration.MAPPED_ROUTE_IN_REQUEST, route);
            }
        } catch (NotMatchedException ex) {
        }

        chain.doFilter(wrappedRequest, response);
    }

    protected RouteMapping getRouteMapping() {
        return new RouteMapping() {

            public String getNextView(String fullURL, String outcome) throws NotMatchedException {
                return Navigator.getInstance().getNextView(Navigator.getInstance().getMatchedRule(fullURL), outcome);
            }
        };
    }

    public void destroy() {
    }

    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("PathTranslateFilter()");
        }
        StringBuffer sb = new StringBuffer("PathTranslateFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }
}
