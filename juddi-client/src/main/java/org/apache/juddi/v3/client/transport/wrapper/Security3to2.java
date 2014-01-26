/*
 * Copyright 2014 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.transport.wrapper;

import java.rmi.RemoteException;
import java.util.Map;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.soap.SOAPFaultException;
import org.apache.juddi.v3.client.UDDIServiceV2;
import org.apache.juddi.v3.client.mapping.MapUDDIv2Tov3;
import org.apache.juddi.v3.client.mapping.MapUDDIv3Tov2;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v2_service.DispositionReport;
import org.uddi.v2_service.Publish;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class provides a wrapper to enable UDDIv3 clients to talk to UDDIv2
 * servers via JAXWS Transport. It handles all translations for Security 
 * service methods.
 *
 * @author <a href="alexoree@apache.org">Alex O'Ree</a>
 * @since 3.2
 */
public class Security3to2 implements UDDISecurityPortType, BindingProvider {

        Publish publishService = null;

        public Security3to2() {

                UDDIServiceV2 service = new UDDIServiceV2();
                publishService = service.getPublish();

        }

        @Override
        public void discardAuthToken(DiscardAuthToken body) throws DispositionReportFaultMessage, RemoteException {
                try {
                        org.uddi.api_v2.DiscardAuthToken discardAuthToken = new org.uddi.api_v2.DiscardAuthToken();
                        discardAuthToken.setAuthInfo(body.getAuthInfo());
                        discardAuthToken.setGeneric(MapUDDIv3Tov2.VERSION);
                        org.uddi.api_v2.DispositionReport discardAuthToken1 = publishService.discardAuthToken(discardAuthToken);
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                } catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        @Override
        public AuthToken getAuthToken(GetAuthToken body) throws DispositionReportFaultMessage, RemoteException {
                org.uddi.api_v2.GetAuthToken authToken = new org.uddi.api_v2.GetAuthToken();
                authToken.setGeneric(MapUDDIv3Tov2.VERSION);
                authToken.setCred(body.getCred());
                authToken.setUserID(body.getUserID());
                try {
                        org.uddi.api_v2.AuthToken authToken1 = publishService.getAuthToken(authToken);
                        AuthToken r = new AuthToken();
                        r.setAuthInfo(authToken1.getAuthInfo());
                        return r;
                } catch (DispositionReport ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }  catch (SOAPFaultException ex) {
                        throw MapUDDIv2Tov3.MapException(ex);
                }
        }

        
        @Override
        public Map<String, Object> getRequestContext() {
                return ((BindingProvider)publishService).getRequestContext();
        }

        @Override
        public Map<String, Object> getResponseContext() {
                return ((BindingProvider)publishService).getResponseContext();
        }

        @Override
        public Binding getBinding() {
                return ((BindingProvider)publishService).getBinding();
        }

        @Override
        public EndpointReference getEndpointReference() {
                return ((BindingProvider)publishService).getEndpointReference();
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz) {
                return ((BindingProvider)publishService).getEndpointReference(clazz);
        }
}
