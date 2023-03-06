/*
 * Copyright 2013 The Apache Software Foundation.
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
 */

package org.apache.juddi.api.impl;

import javax.xml.ws.WebServiceContext;

/**
 * This is used 
 * @author Alex O'Ree
 */
public class UDDISecurityImplExt extends UDDISecurityImpl{
 /**
     * used for unit tests only
     *
     * @param ctx
     */
    protected UDDISecurityImplExt(WebServiceContext ctx) {
        super();
        this.ctx = ctx;
    }
}
