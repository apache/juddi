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

package org.apache.juddi.client.plugins;

import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * @since 3.2.1
 */
public class MulticastDNSTransport extends Transport {

        @Override
        public UDDIInquiryPortType getUDDIInquiryService(String endpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UDDISecurityPortType getUDDISecurityService(String endpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UDDIPublicationPortType getUDDIPublishService(String endpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UDDISubscriptionPortType getUDDISubscriptionService(String endpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UDDICustodyTransferPortType getUDDICustodyTransferService(String endpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UDDISubscriptionListenerPortType getUDDISubscriptionListenerService(String endpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public JUDDIApiPortType getJUDDIApiService(String endpointURL) throws TransportException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

}
