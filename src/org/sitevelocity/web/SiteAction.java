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

package org.sitevelocity.web;

import org.apache.velocity.context.Context;
import org.sitevelocity.exceptions.ServerInternalException;
import javax.servlet.http.HttpServletRequest;

/**
 * SiteAction should be extended by view and action classes, so as to
 * fill context for view or do CRUD operations.
 *
 * @author javafuns
 */
public interface SiteAction {

    /**
     * fill context or do CRUD operations.
     *
     * @param request
     * @param response
     * @param context
     * @return outcome
     */
    String execute(HttpServletRequest request, Context context) throws ServerInternalException;
}
