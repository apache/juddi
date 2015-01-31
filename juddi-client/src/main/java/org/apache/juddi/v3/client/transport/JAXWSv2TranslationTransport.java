/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.v3.client.transport;

import java.util.Map;
import java.util.Properties;

import javax.xml.ws.BindingProvider;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.cryptor.CryptorFactory;
import org.apache.juddi.v3.client.transport.wrapper.Inquiry3to2;
import org.apache.juddi.v3.client.transport.wrapper.Publish3to2;
import org.apache.juddi.v3.client.transport.wrapper.Security3to2;
import org.uddi.v2_service.Inquire;
import org.uddi.v2_service.Publish;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * JAXWS Transport for UDDIv2 bindings. Use this class for accessing UDDIv2
 * server using the UDDIv3 APIs
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * @since 3.2
 */
public class JAXWSv2TranslationTransport extends JAXWSTransport {

        private static Log logger = LogFactory.getLog(JAXWSv2TranslationTransport.class);

        Inquiry3to2 inquiryv2 = null;
        Security3to2 securityv2 = null;
        Publish3to2 publishv2 = null;

        public JAXWSv2TranslationTransport() {
                super();
                this.nodeName = Transport.DEFAULT_NODE_NAME;
        }

        public JAXWSv2TranslationTransport(String nodeName) {
                super();
                this.nodeName = nodeName;
        }

        public JAXWSv2TranslationTransport(String clientName, String nodeName) {
                super();
                this.clientName = clientName;
                this.nodeName = nodeName;
        }

        public String getNodeName() {
                return nodeName;
        }

        public void setNodeName(String nodeName) {
                this.nodeName = nodeName;
        }

        /**
         * Sets the credentials on the RequestContext if the services are
         * protected by Basic Authentication. The username and password are
         * obtained from the uddi.xml.
         *
         * @param requestContext
         * @throws ConfigurationException
         */
        private void setCredentials(Map<String, Object> requestContext) throws ConfigurationException {
                UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                Properties properties = client.getClientConfig().getUDDINode(nodeName).getProperties();
                if (properties != null) {
                        String username = null;
                        String password = null;
                        if (properties.containsKey(Property.BASIC_AUTH_USERNAME)) {
                                username = properties.getProperty(Property.BASIC_AUTH_USERNAME);
                        }
                        if (properties.containsKey(Property.BASIC_AUTH_PASSWORD)) {
                                password = properties.getProperty(Property.BASIC_AUTH_PASSWORD);
                        }
                        String cipher = null;
                        boolean isEncrypted = false;
                        if (properties.containsKey(Property.BASIC_AUTH_PASSWORD_CP)) {
                                cipher = properties.getProperty(Property.BASIC_AUTH_PASSWORD_CP);
                        }
                        if (properties.containsKey(Property.BASIC_AUTH_PASSWORD_IS_ENC)) {
                                isEncrypted = Boolean.parseBoolean(properties.getProperty(Property.BASIC_AUTH_PASSWORD_IS_ENC));
                        }
                        if (username != null && password != null) {
                                requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
                                if (isEncrypted) {
                                        try {
                                                requestContext.put(BindingProvider.PASSWORD_PROPERTY, CryptorFactory.getCryptor(cipher).decrypt(password));
                                        } catch (Exception ex) {
                                                logger.error("Unable to decrypt password!", ex);
                                        }
                                } else {
                                        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
                                }
                        }
                }
        }

        @Override
        public UDDIInquiryPortType getUDDIInquiryService(String endpointURL) throws TransportException {
                try {
                        if (inquiryv2 == null) {
                                inquiryv2 = new Inquiry3to2();

                        }
                        if (endpointURL == null) {
                                UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                endpointURL = client.getClientConfig().getUDDINode(nodeName).getInquiryUrl();
                        }
                        Map<String, Object> requestContext = ((BindingProvider) inquiryv2).getRequestContext();
                        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return inquiryv2;

        }

        @Override
        public UDDISecurityPortType getUDDISecurityService(String endpointURL) throws TransportException {
                try {
                        if (securityv2 == null) {
                                securityv2 = new Security3to2();

                        }

                        if (endpointURL == null) {
                                
                                UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                endpointURL = client.getClientConfig().getUDDINode(nodeName).getPublishUrl();
                        }
                         if (endpointURL.toLowerCase().startsWith("http:")){
                                        logger.warn("You should consider use a secure protocol (https) when sending your password!");
                                }
                        Map<String, Object> requestContext = ((BindingProvider) securityv2).getRequestContext();
                        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return securityv2;

        }

        @Override
        public UDDIPublicationPortType getUDDIPublishService(String endpointURL) throws TransportException {
                try {
                        if (publishv2 == null) {
                                publishv2 = new Publish3to2();

                        }
                        if (endpointURL == null) {
                                UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                endpointURL = client.getClientConfig().getUDDINode(nodeName).getPublishUrl();
                        }

                        Map<String, Object> requestContext = ((BindingProvider) publishv2).getRequestContext();
                        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return publishv2;
        }

        
        
        
        
        
        public Inquire getUDDIv2InquiryService() throws TransportException {
             return getUDDIv2InquiryService(null);
        }
        public Inquire getUDDIv2InquiryService(String endpointURL) throws TransportException {
                try {
                        if (inquiryv2 == null) {
                                inquiryv2 = new Inquiry3to2();

                        }
                        if (endpointURL == null) {
                                UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                endpointURL = client.getClientConfig().getUDDINode(nodeName).getInquiryUrl();
                        }
                        Map<String, Object> requestContext = ((BindingProvider) inquiryv2).getRequestContext();
                        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return inquiryv2.getUDDIv2WebServiceClient();

        }

        
        
        public Publish getUDDIv2PublishService(String endpointURL) throws TransportException {
                try {
                        if (publishv2 == null) {
                                publishv2 = new Publish3to2();

                        }
                        if (endpointURL == null) {
                                UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                endpointURL = client.getClientConfig().getUDDINode(nodeName).getPublishUrl();
                        }

                        Map<String, Object> requestContext = ((BindingProvider) publishv2).getRequestContext();
                        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return publishv2.getUDDIv2PublishWebServiceClient();
        }
        
        public Publish getUDDIv2PublishService() throws TransportException {
                return getUDDIv2PublishService(null);
        }
}
