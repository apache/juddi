/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
package org.apache.juddi.validation;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.apache.juddi.api_v3.Node;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class ValidateNode extends ValidateUDDIApi {

        public ValidateNode(UddiEntityPublisher publisher) {
                super(publisher);
        }
        private static final Logger logger = Logger.getLogger(ValidateNode.class.getCanonicalName());
        /*-------------------------------------------------------------------
         validateSaveNode functions are specific to jUDDI.
         --------------------------------------------------------------------*/

        public void validateSaveNode(EntityManager em, org.apache.juddi.api_v3.SaveNodeInfo body) throws DispositionReportFaultMessage {

                if (body == null) {
                        throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
                }

                // No null or empty list
                List<Node> nodes = body.getNode();
                if (nodes == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.saveNodes.NoInput"));
                }

                for (Node clerk : body.getNode()) {
                        validateNode(clerk);
                }

        }

        public void validateNode(org.apache.juddi.api_v3.Node node) throws DispositionReportFaultMessage {

                // No null input
                if (node == null) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NullInput"));
                }

                String name = node.getName();
                if (name == null || name.length() == 0 || name.length()>255 || node.getClientName()==null|| node.getClientName().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoName"));
                }
                if (node.getDescription() == null || node.getDescription().length() == 0 || node.getDescription().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoDescription"));
                }
                if (node.getClientName() == null || node.getClientName().length() == 0 || node.getClientName().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoClientName"));
                }
                if (node.getCustodyTransferUrl() == null || node.getCustodyTransferUrl().length() == 0 || node.getClientName().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoNCT"));
                }
                if (node.getInquiryUrl() == null || node.getInquiryUrl().length() == 0 || node.getInquiryUrl().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoIN"));
                }
                if (node.getPublishUrl() == null || node.getPublishUrl().length() == 0 || node.getPublishUrl().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoPUB"));
                }
                if (node.getSubscriptionListenerUrl() == null || node.getSubscriptionListenerUrl().length() == 0 || node.getSubscriptionListenerUrl().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoSUBL"));
                }
                if (node.getReplicationUrl() == null || node.getReplicationUrl().length() == 0 || node.getReplicationUrl().length() > 255) {
                        //throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoSUBL"));
                        logger.log(Level.WARNING, "No replication url on save node request!");
                }
                if (node.getSubscriptionUrl() == null || node.getSubscriptionUrl().length() == 0 || node.getSubscriptionUrl().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoSUB"));
                }
                if (node.getProxyTransport() == null || node.getProxyTransport().length() == 0 || node.getProxyTransport().length() > 255) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoProxy"));
                } else {
                        try {
                                //validate that the class exists and that it is of type
                                //org.apache.juddi.v3.client.transport.Transport
                                Class<?> forName = Class.forName(node.getProxyTransport());
                                Object j = forName.newInstance();
                                if (!(j instanceof org.apache.juddi.v3.client.transport.Transport)) {
                                        throw new ValueNotAllowedException(new ErrorMessage("errors.node.illegalProxyTransport"));
                                }
                        } catch (Exception ex) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.node.illegalProxyTransport"));
                        }
                }
                if (node.getProxyTransport().equalsIgnoreCase(org.apache.juddi.v3.client.transport.RMITransport.class.getCanonicalName())) {
                        if (node.getFactoryInitial() == null || node.getFactoryInitial().length() == 0 || node.getFactoryInitial().length() > 255) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoRMIData"));
                        }
                        if (node.getFactoryNamingProvider() == null || node.getFactoryNamingProvider().length() == 0 || node.getFactoryNamingProvider().length() > 255) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoRMIData"));
                        }
                        if (node.getFactoryURLPkgs() == null || node.getFactoryURLPkgs().length() == 0 || node.getFactoryURLPkgs().length() > 255) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoRMIDataF"));
                        }
                }

        }
}
