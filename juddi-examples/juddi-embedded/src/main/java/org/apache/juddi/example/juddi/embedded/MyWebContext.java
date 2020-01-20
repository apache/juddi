/*
 * Copyright 2020 The Apache Software Foundation.
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
package org.apache.juddi.example.juddi.embedded;

import java.security.Principal;
import java.util.Set;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.Element;

/**
 *
 * @author Alex
 */
public class MyWebContext implements WebServiceContext {

    String user;
    Set<String> roles;

    public MyWebContext(String username, Set<String> roles) {
        this.user = username;
        this.roles = roles;
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
                return user;
            }
        };
    }

    @Override
    public boolean isUserInRole(String arg0) {
        return roles.contains(arg0);
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
