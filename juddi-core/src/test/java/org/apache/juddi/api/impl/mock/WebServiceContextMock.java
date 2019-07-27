/*
 * Copyright 2019 The Apache Software Foundation.
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
package org.apache.juddi.api.impl.mock;

import java.security.Principal;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.Element;

/**
 *
 * @author AO
 */
public class WebServiceContextMock implements WebServiceContext {

    public String role;
    public String username;

    public WebServiceContextMock(String username, String role) {
        this.role = role;
        this.username = username;

    }

    @Override
    public MessageContext getMessageContext() {
        return null;
    }

    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };
    }

    @Override
    public boolean isUserInRole(String arg0) {
        return arg0.equals(role);
    }

    @Override
    public EndpointReference getEndpointReference(Element... arg0) {
        return null;
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> arg0, Element... arg1) {
        return null;
    }

}
