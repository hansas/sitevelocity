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
package org.sitevelocity.web.servlets;

import org.sitevelocity.exceptions.ActionNotFoundException;
import org.sitevelocity.exceptions.NotMatchedException;
import org.sitevelocity.exceptions.ServerInternalException;
import org.sitevelocity.utils.ActionFactory;
import org.sitevelocity.utils.SiteVelocityLogger;
import org.sitevelocity.web.SiteAction;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 * this servlet handles form.
 * 
 * @author javafuns
 */
@SuppressWarnings("serial")
public class ActionControllerServlet extends SiteVelocityServlet {

    @Override
    public void doRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nextPage = "/";
        try {
            String outcome = this.invokeAction(request);
            nextPage = this.getNextView(outcome, request);
        } catch (ActionNotFoundException ex) {
            SiteVelocityLogger.getLogger(ActionControllerServlet.class.getName()).logError("No such Action for request ["
                    + request.getRequestURI() + "] is found.", ex);
            throw new ServletException("No such Action [" + request.getRequestURI() + "]", ex);
        } catch (ServerInternalException ex) {
            SiteVelocityLogger.getLogger(ActionControllerServlet.class.getName()).logError("Action for request ["
                    + request.getRequestURI() + "] was not executed correctly.", ex);
            throw new ServletException("Action for request [" + request.getRequestURI()
                    + "] was not executed correctly", ex);
        } catch (NotMatchedException ex) {
            SiteVelocityLogger.getLogger(ActionControllerServlet.class.getName()).logError("No next view is "
                    + "configured for request [" + request.getRequestURI() + "].", ex);
            SiteVelocityLogger.getLogger(ActionControllerServlet.class.getName()).logError("Skipped this exception "
                    + " execution is going on.");
        }

        response.sendRedirect(nextPage);
    }

    /**
     * invoke action.
     * @param request
     * @param context
     * @return new redirected or forwarded URI
     */
    protected String invokeAction(HttpServletRequest request) throws ActionNotFoundException, ServerInternalException {
        String requestActionURL = this.getRequestURI(request);
        String actionName = this.getClassName(requestActionURL);

        SiteVelocityLogger.getLogger(ActionControllerServlet.class.getName()).logInfo("The action being invoked is : "
                + actionName);

        SiteAction oa = ActionFactory.getAction(actionName);
        String outcome = null;
        if (oa != null) {
            outcome = oa.execute(request, null);
            SiteVelocityLogger.getLogger(ActionControllerServlet.class.getName()).logInfo("The action "
                    + actionName + " was invoked successfully!");
        }

        return outcome;
    }
}
