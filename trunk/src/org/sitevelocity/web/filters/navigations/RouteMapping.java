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

package org.sitevelocity.web.filters.navigations;

import org.sitevelocity.exceptions.NotMatchedException;

/**
 * Calculate next page by analysising incoming URL.
 * 
 * @author javafuns
 */
public interface RouteMapping {

    /**
     * get next page.
     * @param requestURL incoming url that is from client.
     * @param outcome result of view or action's execution
     * @return
     * @throws NotMatchedException
     */
    String getNextView(String requestURL, String outcome) throws NotMatchedException;

}
