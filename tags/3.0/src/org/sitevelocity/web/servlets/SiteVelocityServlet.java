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
package org.sitevelocity.web.servlets;

import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;
import org.sitevelocity.exceptions.NotMatchedException;
import org.sitevelocity.utils.SiteVelocityConfiguration;
import org.sitevelocity.web.filters.SiteVelocityServletRequest;
import org.sitevelocity.web.filters.navigations.RouteMapping;
import org.sitevelocity.web.utils.WebUtil;

/**
 * 
 * @author javafuns
 */
@SuppressWarnings("serial")
public abstract class SiteVelocityServlet extends VelocityViewServlet {

    /**
     * convert original PATH of view or action to the proper view or action class.
     * Request: /index.vm -&gt; Index
     * Request: /admin/post.vm -&gt; admin.Post
     * Request: /admin/save.do -&gt; admin.Save
     *
     * @param incomingPath inner path of view or action
     * @return
     */
    protected String getClassName(String incomingPath) {
        String dotvm = SiteVelocityConfiguration.getInstance().getTemplateExtension();
        String dotdo = SiteVelocityConfiguration.getInstance().getActionExtension();
        String inPackage = "";

        if (incomingPath.endsWith(dotvm)) {
            incomingPath = incomingPath.substring(0, incomingPath.lastIndexOf(dotvm));
            inPackage = SiteVelocityConfiguration.getInstance().getViewPackage();
        }

        if (incomingPath.endsWith(dotdo)) {
            incomingPath = incomingPath.substring(0, incomingPath.lastIndexOf(dotdo));
            inPackage = SiteVelocityConfiguration.getInstance().getActionPackage();
        }

        return (inPackage + "." + WebUtil.uriToClassName(incomingPath));
    }

    /**
     * get request URI withou its context path.
     * @param request
     * @return
     */
    protected String getRequestURI(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURI());
        requestURL.delete(0, request.getContextPath().length());

        return requestURL.toString();
    }

    /**
     * get next page view by outcome, which is the result of VIEW or ACTION class's execution.
     * 
     * @param outcome
     * @param request
     * @return
     * @throws NotMatchedException
     */
    protected String getNextView(String outcome, HttpServletRequest request) throws NotMatchedException {
        RouteMapping route = (RouteMapping) request.getAttribute(SiteVelocityConfiguration.MAPPED_ROUTE_IN_REQUEST);

        if (route != null) {
            if (request instanceof SiteVelocityServletRequest) {
                return route.getNextView(WebUtil.fullRequestURL(((SiteVelocityServletRequest) request).getRawRequest()), outcome);
            } else {
                throw new NotMatchedException("PathTranslateFilter is not enabled or not correctly configured.");
            }
        }

        // if navigation is not enabled, then action's outcome should be the pattern of URL.
        if (this instanceof ActionControllerServlet) {
            if (outcome == null) {
                throw new NotMatchedException("No outcoming URL is configured.");
            }
            return outcome;
        }
        return WebUtil.fullRequestURL(request);
    }
}
