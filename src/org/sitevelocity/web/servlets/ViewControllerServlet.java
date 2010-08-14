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
import org.sitevelocity.utils.SiteVelocityConfiguration;
import org.sitevelocity.utils.SiteVelocityLogger;
import org.sitevelocity.web.SiteAction;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.util.OutputConverter;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.Template;
import org.sitevelocity.web.utils.WebUtil;

/**
 * invoke appropriate view class according to request.
 * 
 * @author javafuns
 */
@SuppressWarnings("serial")
public class ViewControllerServlet extends SiteVelocityServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    @SuppressWarnings("deprecation")
    public Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws Exception {

        context.put("base", request.getContextPath());
        // For backwards compatability with apps that used the old VelocityDecoratorServlet
        // that extended VelocityServlet instead of VelocityViewServlet
        context.put("req", request);
        context.put("res", response);

        HTMLPage htmlPage = this.getOriginalHTMLPage(request, response, context);
        String template;

        if (htmlPage == null) {
            context.put("title", "Title?");
            context.put("body", "<p>Body?</p>");
            context.put("head", "<!-- head -->");
            template = request.getServletPath();
        } else {
            context.put("title", OutputConverter.convert(htmlPage.getTitle()));
            {
                StringWriter buffer = new StringWriter();
                htmlPage.writeBody(OutputConverter.getWriter(buffer));
                context.put("body", buffer.toString());
            }
            {
                StringWriter buffer = new StringWriter();
                htmlPage.writeHead(OutputConverter.getWriter(buffer));
                context.put("head", buffer.toString());
            }
            context.put("page", htmlPage);
            DecoratorMapper decoratorMapper = getDecoratorMapper();
            Decorator decorator = decoratorMapper.getDecorator(request, htmlPage);
            template = decorator.getPage();
        }

        return getTemplate(template);
    }

    /**
     * invoke view action to fullfill context.
     * @param request
     * @param context
     * @return toview
     */
    protected String invokeView(HttpServletRequest request, Context context)
            throws ServerInternalException {
        String requestPath = this.getRequestURI(request);
        if (requestPath.endsWith("/")) {
            requestPath += SiteVelocityConfiguration.getInstance().getDefaultIndexPage();
        }
        // handle non-view requests.
        String dotvm = SiteVelocityConfiguration.getInstance().getTemplateExtension();
        if (!requestPath.endsWith(dotvm)) {
            return WebUtil.fullRequestURL(request);
        }

        String viewClass = this.getClassName(requestPath);

        SiteVelocityLogger.getLogger(ViewControllerServlet.class.getName()).logInfo("The view [" + requestPath
                + "] is being rendered by : " + viewClass);

        try {
            SiteAction oa = ActionFactory.getAction(viewClass);

            // A vm can either or not have a corresponding action.
            if (oa != null) {
                String outcome = oa.execute(request, context);
                return this.getNextView(outcome, request);
            }
        } catch (NotMatchedException ex) {
            SiteVelocityLogger.getLogger(ViewControllerServlet.class.getName()).logError("No outcoming View ["
                    + "] for this request [" + requestPath + "] :", ex);
        } catch (ActionNotFoundException ex) {
            SiteVelocityLogger.getLogger(ViewControllerServlet.class.getName()).logError("No such View ["
                    + viewClass + "] for this request [" + requestPath + "] :", ex);
        }

        return requestPath;
    }

    /**
     * <p>Gets the requested original template and parse it.</p>
     *
     * @param request client request
     * @param response client response (whose character encoding we'll use)
     * @return Velocity Template object or null
     */
    protected HTMLPage getOriginalHTMLPage(HttpServletRequest request, HttpServletResponse response, Context context)
            throws ResourceNotFoundException, ParseErrorException, Exception {
        String toViewPage = this.invokeView(request, context);

        Template originalTemplate = this.getTemplate(toViewPage);
        CharArrayWriter caw = new CharArrayWriter();
        originalTemplate.merge(context, caw);

//        SiteMeshWebAppContext webAppContext = new SiteMeshWebAppContext(request, response, getServletContext());
        Factory factory = Factory.getInstance(new Config(this.getServletConfig()));
//        PageParser pageParser = factory.getPageParser(new HttpContentType(webAppContext.getContentType()).getType());
        PageParser pageParser = factory.getPageParser("text/html");
        Page page = pageParser.parse(caw.toCharArray());

        Logger logger = SiteVelocityLogger.getLogger(ViewControllerServlet.class.getName()).getLogger();
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Original Page Body for Request [" + request.getRequestURL() + "] is |||||||||| " + page.getBody());
        }

        return (HTMLPage) page;
    }

    private DecoratorMapper getDecoratorMapper() {
        Factory factory = Factory.getInstance(new Config(getServletConfig()));
        DecoratorMapper decoratorMapper = factory.getDecoratorMapper();
        return decoratorMapper;
    }
}
