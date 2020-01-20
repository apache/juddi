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
import org.apache.juddi.v3.client.JUDDIApiService;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.cryptor.CryptorFactory;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

public class JAXWSTransport extends Transport {

        private static Log logger = LogFactory.getLog(JAXWSTransport.class);

        String nodeName = null;
        String clientName = null;
        UDDIInquiryPortType inquiryService = null;
        UDDISecurityPortType securityService = null;
        UDDIPublicationPortType publishService = null;
        UDDISubscriptionPortType subscriptionService = null;
        UDDISubscriptionListenerPortType subscriptionListenerService = null;
        UDDICustodyTransferPortType custodyTransferService = null;
        JUDDIApiPortType publisherService = null;

        public JAXWSTransport() {
                super();
                this.nodeName = Transport.DEFAULT_NODE_NAME;
        }

        public JAXWSTransport(String nodeName) {
                super();
                this.nodeName = nodeName;
        }

        public JAXWSTransport(String clientName, String nodeName) {
                super();
                this.clientName = clientName;
                this.nodeName = nodeName;
        }

        public UDDIInquiryPortType getUDDIInquiryService(String endpointURL) throws TransportException {
                try {
                        if (inquiryService == null) {
                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getInquiryUrl();
                                }
                                UDDIService service = new UDDIService();
                                inquiryService = service.getUDDIInquiryPort();
                        }
                        Map<String, Object> requestContext = ((BindingProvider) inquiryService).getRequestContext();
                        if (endpointURL != null) {
                        
                                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        }
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return inquiryService;
        }

        public UDDISecurityPortType getUDDISecurityService(String endpointURL) throws TransportException {
                try {
                        if (securityService == null) {
                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getSecurityUrl();
                                }
                                UDDIService service = new UDDIService();
                                securityService = service.getUDDISecurityPort();
                        }
                        if (endpointURL != null) {
                                if (endpointURL.toLowerCase().startsWith("http:")){
                                        logger.warn("You should consider using a secure protocol (https) when sending your password!");
                                }
                                Map<String, Object> requestContext = ((BindingProvider) securityService).getRequestContext();
                                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                                setCredentials(requestContext);
                        }
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return securityService;
        }

        public UDDIPublicationPortType getUDDIPublishService(String endpointURL) throws TransportException {
                try {
                        if (publishService == null) {

                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getPublishUrl();
                                }
                                UDDIService service = new UDDIService();
                                publishService = service.getUDDIPublicationPort();
                        }
                        Map<String, Object> requestContext = ((BindingProvider) publishService).getRequestContext();
                        if (endpointURL != null)
                                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                        setCredentials(requestContext);
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return publishService;
        }

        public UDDISubscriptionPortType getUDDISubscriptionService(String endpointURL) throws TransportException {
                try {
                        if (subscriptionService == null) {
                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getSubscriptionUrl();
                                }
                                UDDIService service = new UDDIService();
                                subscriptionService = service.getUDDISubscriptionPort();
                        }
                        if (endpointURL != null) {
                                Map<String, Object> requestContext = ((BindingProvider) subscriptionService).getRequestContext();
                                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                                setCredentials(requestContext);
                        }
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return subscriptionService;
        }

        public UDDISubscriptionListenerPortType getUDDISubscriptionListenerService(String endpointURL) throws TransportException {
                try {
                        if (subscriptionListenerService == null) {
                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getSubscriptionListenerUrl();
                                }
                                UDDIService service = new UDDIService();
                                subscriptionListenerService = service.getUDDISubscriptionListenerPort();
                        }
                        if (endpointURL != null) {
                                Map<String, Object> requestContext = ((BindingProvider) subscriptionListenerService).getRequestContext();
                                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                                setCredentials(requestContext);
                        }
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return subscriptionListenerService;
        }

        public UDDICustodyTransferPortType getUDDICustodyTransferService(String endpointURL) throws TransportException {
                try {
                        if (custodyTransferService == null) {
                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getCustodyTransferUrl();
                                }
                                UDDIService service = new UDDIService();
                                custodyTransferService = service.getUDDICustodyPort();
                        }
                        if (endpointURL != null) {
                                Map<String, Object> requestContext = ((BindingProvider) custodyTransferService).getRequestContext();
                                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                                setCredentials(requestContext);
                        }
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return custodyTransferService;
        }

        /**
         * This is a jUDDI specific API
         */
        public JUDDIApiPortType getJUDDIApiService(String endpointURL) throws TransportException {
                try {
                        if (publisherService == null) {
                                if (endpointURL == null) {
                                        UDDIClient client = UDDIClientContainer.getUDDIClient(clientName);
                                        endpointURL = client.getClientConfig().getUDDINode(nodeName).getJuddiApiUrl();
                                }
                                JUDDIApiService service = new JUDDIApiService();
                                publisherService = (JUDDIApiPortType) service.getPort(JUDDIApiPortType.class);
                        }
                        if (endpointURL != null) {
                                Map<String, Object> requestContext = ((BindingProvider) publisherService).getRequestContext();
                                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
                                setCredentials(requestContext);
                        }
                } catch (Exception e) {
                        throw new TransportException(e.getMessage(), e);
                }
                return publisherService;
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

}
