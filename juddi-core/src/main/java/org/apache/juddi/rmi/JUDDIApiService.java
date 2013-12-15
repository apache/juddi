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
package org.apache.juddi.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.apache.juddi.api.impl.JUDDIApiImpl;
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
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.NodeList;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SaveClerkInfo;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.api_v3.SaveNodeInfo;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.api_v3.SubscriptionWrapper;
import org.apache.juddi.api_v3.SyncSubscription;
import org.apache.juddi.api_v3.SyncSubscriptionDetail;
import org.apache.juddi.api_v3.ValidValues;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.DispositionReport;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * UDDIPublicationPortType wrapper so it can be exposed as a service over RMI.
 *
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class JUDDIApiService extends UnicastRemoteObject implements JUDDIApiPortType {

        private static final long serialVersionUID = 1404805184314952141L;
        private JUDDIApiPortType juddiAPI = new JUDDIApiImpl();

        protected JUDDIApiService(int port) throws RemoteException {
                super(port);
        }

        public void deletePublisher(DeletePublisher body)
                throws DispositionReportFaultMessage {
                juddiAPI.deletePublisher(body);
        }

        public PublisherDetail getAllPublisherDetail(GetAllPublisherDetail body)
                throws DispositionReportFaultMessage {
                return juddiAPI.getAllPublisherDetail(body);
        }

        public PublisherDetail getPublisherDetail(GetPublisherDetail body)
                throws DispositionReportFaultMessage {
                return juddiAPI.getPublisherDetail(body);
        }

        public PublisherDetail savePublisher(SavePublisher body)
                throws DispositionReportFaultMessage {
                return juddiAPI.savePublisher(body);
        }

        public void adminDeleteTModel(DeleteTModel body)
                throws DispositionReportFaultMessage {
                juddiAPI.adminDeleteTModel(body);
        }

        public void deleteClientSubscriptionInfo(DeleteClientSubscriptionInfo body)
                throws DispositionReportFaultMessage {
                juddiAPI.deleteClientSubscriptionInfo(body);
        }

        @Override
        public ClientSubscriptionInfoDetail saveClientSubscriptionInfo(SaveClientSubscriptionInfo body)
                throws DispositionReportFaultMessage {
                return juddiAPI.saveClientSubscriptionInfo(body);

        }

        @Override
        public SyncSubscriptionDetail invokeSyncSubscription(SyncSubscription body)
                throws DispositionReportFaultMessage {
                return juddiAPI.invokeSyncSubscription(body);
        }

        @Override
        public ClerkDetail saveClerk(SaveClerkInfo body) throws DispositionReportFaultMessage {
                return juddiAPI.saveClerk(body);
        }

        @Override
        public NodeDetail saveNode(SaveNodeInfo body) throws DispositionReportFaultMessage {
                return juddiAPI.saveNode(body);
        }

        @Override
        public NodeList getAllNodes(String authInfo) throws DispositionReportFaultMessage {
                return juddiAPI.getAllNodes(authInfo);
        }

        @Override
        public ClerkList getAllClerks(String authInfo) throws DispositionReportFaultMessage {
                return juddiAPI.getAllClerks(authInfo);
        }

        @Override
        public void deleteNode(DeleteNode req) throws DispositionReportFaultMessage {
                juddiAPI.deleteNode(req);
        }

        @Override
        public void deleteClerk(DeleteClerk req) throws DispositionReportFaultMessage {
                juddiAPI.deleteClerk(req);
        }

        @Override
        public DispositionReport adminSaveBusiness(String authInfo, List<AdminSaveBusinessWrapper> values) throws DispositionReportFaultMessage {
                return juddiAPI.adminSaveBusiness(authInfo, values);
        }

        @Override
        public DispositionReport adminSaveTModel(String authInfo, List<AdminSaveTModelWrapper> values) throws DispositionReportFaultMessage {
                return juddiAPI.adminSaveTModel(authInfo, values);
        }

        @Override
        public ReplicationConfiguration getReplicationNodes(String authInfo) throws DispositionReportFaultMessage {
                return juddiAPI.getReplicationNodes(authInfo);
        }

        @Override
        public DispositionReport setReplicationNodes(String authInfo, ReplicationConfiguration replicationConfiguration) throws DispositionReportFaultMessage {
                return juddiAPI.setReplicationNodes(authInfo, replicationConfiguration);
        }

        @Override
        public List<SubscriptionWrapper> getAllClientSubscriptionInfo(String authInfo) throws DispositionReportFaultMessage {
                return juddiAPI.getAllClientSubscriptionInfo(authInfo);
        }

        @Override
        public DispositionReport setAllValidValues(String authInfo, List<ValidValues> values) throws DispositionReportFaultMessage {
                return juddiAPI.setAllValidValues(authInfo, values);
        }

        @Override
        public void adminDeleteSubscription(String authInfo, List<String> subscriptionKey) throws DispositionReportFaultMessage {
                juddiAPI.adminDeleteSubscription(authInfo, subscriptionKey);
        }

        @Override
        public void adminSaveSubscription(String authInfo, String publisherOrUsername, List<Subscription> subscriptions) throws DispositionReportFaultMessage {
                juddiAPI.adminSaveSubscription(authInfo, publisherOrUsername, subscriptions);
        }
}
