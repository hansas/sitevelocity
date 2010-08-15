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

package org.sitevelocity.exceptions;

/**
 * No View or Action classes found.
 * @author javafuns
 */
public class ActionNotFoundException extends Exception {
    private static final long serialVersionUID = -2814384072030439770L;

    public ActionNotFoundException() {
    }

    public ActionNotFoundException(Throwable cause) {
        super("The Service is not found", cause);
    }

    public ActionNotFoundException(String msg) {
        super(msg);
    }

    public ActionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
