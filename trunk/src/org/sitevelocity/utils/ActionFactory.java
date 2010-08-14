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

package org.sitevelocity.utils;

import org.sitevelocity.exceptions.ActionNotFoundException;
import org.sitevelocity.web.SiteAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A Factory that is used to instantiate and cache actions.
 * 
 * @author javafuns
 */
public class ActionFactory {

    private static Map<String, SiteAction> actionsMap = Collections.synchronizedMap(new HashMap<String, SiteAction>());

    private ActionFactory() {
    }

    /**
     * get the instance of action according to the given action name.
     *
     * @param <T>
     * @param actionName
     * @return
     * @throws ActionNotFoundException
     */
    public static SiteAction getAction(String actionName) throws ActionNotFoundException {
        SiteAction ac = actionsMap.get(actionName);
        if (ac == null) {
            try {
                ac = (SiteAction)Thread.currentThread().getContextClassLoader().loadClass(actionName).newInstance();
                actionsMap.put(actionName, ac);
            } catch (InstantiationException e) {
              throw new ActionNotFoundException("Action [" + actionName + "] is not defined correctly", e);
            } catch (IllegalAccessException e) {
              throw new ActionNotFoundException("Action [" + actionName + "] is not defined correctly", e);
            } catch (ClassNotFoundException e) {
              throw new ActionNotFoundException("No such an Action [" + actionName + "] is defined.", e);
            }
        }
        return ac;
    }

}