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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * handy logger helper class.
 *
 * @author javafuns
 */
public class SiteVelocityLogger {

    private final Logger logger;

    private SiteVelocityLogger(Logger logger) {
        this.logger = logger;
    }

    public static SiteVelocityLogger getLogger(String loggerName) {
        return new SiteVelocityLogger(Logger.getLogger(loggerName));
    }

    public void logError(String msg) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            this.logger.severe(msg);
        }
    }

    public void logError(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            this.logger.log(Level.SEVERE, msg, t);
        }
    }

    public void logWarning(String msg) {
        if (this.logger.isLoggable(Level.WARNING)) {
            this.logger.warning(msg);
        }
    }

    public void logWarning(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.WARNING)) {
            this.logger.log(Level.WARNING, msg, t);
        }
    }

    public void logInfo(String msg) {
        if (this.logger.isLoggable(Level.INFO)) {
            this.logger.info(msg);
        }
    }

    public void logInfo(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.INFO)) {
            this.logger.log(Level.INFO, msg, t);
        }
    }

    public void logAll(String msg) {
        if (this.logger.isLoggable(Level.ALL)) {
            this.logger.info(msg);
        }
    }

    public void logAll(String msg, Throwable t) {
        if (this.logger.isLoggable(Level.ALL)) {
            this.logger.log(Level.ALL, msg, t);
        }
    }

    public Logger getLogger() {
        return this.logger;
    }
}
