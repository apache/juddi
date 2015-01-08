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
package org.apache.juddi.api.runtime;

import java.rmi.RemoteException;
import java.util.List;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import org.apache.juddi.api_v3.AdminSaveBusinessWrapper;
import org.apache.juddi.api_v3.AdminSaveTModelWrapper;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.ClerkList;
import org.apache.juddi.api_v3.ClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.DeleteClerk;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.DeleteNode;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetEntityHistoryMessageRequest;
import org.apache.juddi.api_v3.GetEntityHistoryMessageResponse;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.NodeList;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.api_v3.SubscriptionWrapper;
import org.apache.juddi.api_v3.SyncSubscription;
import org.apache.juddi.api_v3.SyncSubscriptionDetail;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.PersonName;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 *
 * @author alex
 */

@WebService(serviceName = "JUDDIApiService",
        endpointInterface = "org.apache.juddi.v3_service.JUDDIApiPortType",
        targetNamespace = "urn:juddi-apache-org:v3_service")
public class juddiTestimpl implements JUDDIApiPortType {
      

        @Override
        public PublisherDetail getPublisherDetail(GetPublisherDetail parameters) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public void deleteClientSubscriptionInfo(DeleteClientSubscriptionInfo body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
        }

        @Override
        public PublisherDetail getAllPublisherDetail(GetAllPublisherDetail body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public ClerkDetail saveClerk(SaveClerk body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public void deletePublisher(DeletePublisher body) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public NodeDetail saveNode(SaveNode body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public PublisherDetail savePublisher(SavePublisher body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public void adminDeleteTModel(DeleteTModel body) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ClientSubscriptionInfoDetail saveClientSubscriptionInfo(SaveClientSubscriptionInfo body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public SyncSubscriptionDetail invokeSyncSubscription(SyncSubscription syncSubscription) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public NodeList getAllNodes(String authInfo) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public ClerkList getAllClerks(String authInfo) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public void deleteNode(DeleteNode body) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deleteClerk(DeleteClerk request) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public DispositionReport adminSaveBusiness(String authInfo, List<AdminSaveBusinessWrapper> values) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public DispositionReport adminSaveTModel(String authInfo, List<AdminSaveTModelWrapper> values) throws DispositionReportFaultMessage, RemoteException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ReplicationConfiguration getReplicationNodes(String authInfo) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                ReplicationConfiguration replicationConfiguration = new ReplicationConfiguration();
                replicationConfiguration.setCommunicationGraph(new CommunicationGraph());
                replicationConfiguration.setRegistryContact(new ReplicationConfiguration.RegistryContact());
                replicationConfiguration.getRegistryContact().setContact(new Contact());
                        replicationConfiguration.getRegistryContact().getContact().getPersonName().add(new PersonName("name", null));

                        
                return replicationConfiguration;
        }

        @Override
        public DispositionReport setReplicationNodes(String authInfo, ReplicationConfiguration replicationConfiguration) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public List<SubscriptionWrapper> getAllClientSubscriptionInfo(String authInfo) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }

        @Override
        public void adminDeleteSubscription(String authInfo, List<String> subscriptionKey) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
        }

        @Override
        public void adminSaveSubscription(String authInfo, String publisherOrUsername, Holder<List<Subscription>> subscriptions) throws DispositionReportFaultMessage {
                CLIServerTest.sink = true;
        }

        //@Override
        public GetEntityHistoryMessageResponse getEntityHistory(GetEntityHistoryMessageRequest body) throws DispositionReportFaultMessage, RemoteException {
                CLIServerTest.sink = true;
                return null;
        }
        
}
