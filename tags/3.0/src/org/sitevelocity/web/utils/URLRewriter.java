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

package org.sitevelocity.web.utils;

import org.sitevelocity.exceptions.NotMatchedException;

/**
 * Rewrite URLs.
 * 
 * @author javafuns
 */
public interface URLRewriter {

    /**
     * translate oldURL to newURL.
     * for example: /post/@entry@/edit.vm -> /post/edit.vm?entry=@entry@
     * @param fromPattern
     * @param toPattern
     * @param fromURL
     * @return
     */
    String translate(String fromPattern, String toPattern, String fromURL) throws NotMatchedException;

    /**
     * return true if url is matched to the given pattern.
     * 
     * @param stringValue
     * @param requestURL
     * @return
     */
    boolean isMatched(String pattern, String url);
}
