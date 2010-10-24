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
package org.sitevelocity.web.filters.navigations;

import org.sitevelocity.exceptions.NotMatchedException;
import org.sitevelocity.utils.SiteVelocityLogger;
import org.sitevelocity.utils.SiteVelocityUtil;
import org.sitevelocity.web.utils.URLRewriter;

/**
 * parse navigation-rule.xml.
 * 
 * @author javafuns
 */
public class Navigator {

    private static final Navigator INSTANCE = new Navigator();
    private URLRewriter defaultURLRewriter;
    private SitevelocityConfigDocument original;

    private Navigator() {
        try {
            this.original = SitevelocityConfigDocument.Factory.parse(Navigator.class.getClassLoader().getResourceAsStream("navigation-rule.xml"));
        } catch (Throwable ex) {
            SiteVelocityLogger.getLogger(Navigator.class.getName()).logError("Error when parsing navigation-rule.xml", ex);
            throw new RuntimeException("Can't parse navigation-rule.xml.", ex);
        }

        String urlRewriter = this.original.getSitevelocityConfig().getUrlWriter();
        try {
            this.defaultURLRewriter = (URLRewriter) Navigator.class.getClassLoader().loadClass(urlRewriter).newInstance();
        } catch (Throwable ex) {
            SiteVelocityLogger.getLogger(Navigator.class.getName()).logError("No top level URL Rewriter defined in navigation-rule.xml", ex);
            throw new RuntimeException("Can't instantiate default URLRewriter.", ex);
        }
    }

    public static Navigator getInstance() {
        return INSTANCE;
    }

    /**
     * get NavigationRule xml element according to request URL.
     * @param requestURL incoming request url of client
     * @return
     */
    public NavigationRule getMatchedRule(String requestURL) throws NotMatchedException {
        if (SiteVelocityUtil.isBlank(requestURL)) {
            throw new NotMatchedException("No any navigation rule configured for this URL");
        }

        if (this.defaultURLRewriter != null && this.original != null) {
            NavigationRule[] rules = this.original.getSitevelocityConfig().getNavigationRuleArray();
            for (NavigationRule rule : rules) {
                URLRewriter rewriter = this.getURLRewriter(rule);
                if (rewriter.isMatched(rule.getFromViewId().toString(), requestURL)) {
                    return rule;
                }
            }
        }
        throw new NotMatchedException("No any navigation rule configured for this URL");
    }

    /**
     * get next view page according to View or Action's result.
     * @param NavigationRule mappedNav
     * @param outcome result of view or action execution
     * @return
     * @throws NotMatchedException
     */
    public String getNextView(NavigationRule mappedNav, String outcome) throws NotMatchedException {
        if (mappedNav == null) {
            throw new NotMatchedException("No Next View can be found for this URL ");
        }

        NavigationCase[] cases = mappedNav.getNavigationCaseArray();
        NavigationCase defaultCase = null;
        if (cases != null && cases.length > 0) {
            for (NavigationCase c : cases) {
                if (c.getFromOutcome().trim().equalsIgnoreCase(outcome)) {
                    return c.getToViewId();
                }
                if ("default".equalsIgnoreCase(c.getFromOutcome().trim())) {
                    defaultCase = c;
                }
            }
        }
        if (defaultCase != null) {
            return defaultCase.getToViewId();
        }
        throw new NotMatchedException("No Next View configured for this URL : "
                + mappedNav.getFromViewId().toString());
    }

    private URLRewriter getURLRewriter(NavigationRule nav) {
        return this.defaultURLRewriter;
    }

    /**
     * translate incoming url of client to internal one.
     *
     * @param mappedNav
     * @param fromURL incoming url of client
     * @return
     * @throws NotMatchedException
     */
    public String getPathTranslated(NavigationRule mappedNav, String fromURL) throws NotMatchedException {
        URLRewriter rewriter = this.getURLRewriter(mappedNav);

        return rewriter.translate(mappedNav.getFromViewId().toString(), mappedNav.getFromViewId().getMappingTo(), fromURL);
    }

    /**
     * get original xml object.
     * @return
     */
    protected SitevelocityConfigDocument getXmlObject() {
        return this.original;
    }
}
