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
package org.apache.juddi.subscription.notify;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.datatype.DatatypeFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.client.AMQTopic;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;

/**
 *
 * @author Alex O'Ree
 */
public class Demo implements MessageListener {

        public static void main(String[] args) throws Exception {
                System.out.println("Hello world!");
                new Demo().Fire();
        }

        public static final String TOPIC = "UDDI";
        public static final String TMODEL_KEY_BASE = "uddi:amqptestdomain:";
        public static final String TMODEL_KEY_TOPIC = TMODEL_KEY_BASE + ":topic";
        public static final String TMODEL_DESTINATION_TYPE = TMODEL_KEY_BASE + "amqp.destination.type";
        public static final String TMODEL_DESTINATION_NAME = TMODEL_KEY_BASE + "amqp.destination";

        private void Fire() throws Exception {
                String amqpURL = "amqp://guest:guest@clientid/?brokerlist='tcp://localhost:5672'";
                UDDIClient client = new UDDIClient("META-INF/subscription-notification-client-uddi.xml");
                UDDIClerk clerk = client.getClerk("default");
                String base = "uddi:amqptestdomain:";
                String lang = "en";

                TModel destinationType = new TModel();
                destinationType.setName(new Name("AMQP Destination Type", lang));
                destinationType.getDescription().add(new Description("topicExchange, queue, etc", lang));
                destinationType.setTModelKey(TMODEL_DESTINATION_TYPE);

                TModel destinationName = new TModel();
                destinationName.setName(new Name("AMQP Destination Name", lang));
                destinationName.getDescription().add(new Description("The name of the topic or queue", lang));
                destinationName.setTModelKey(TMODEL_DESTINATION_NAME);

                TModel createKeyGenator = UDDIClerk.createKeyGenator(base + "keygenerator", "AMQP Test Key domain", lang);
                BusinessEntity be = new BusinessEntity();
                be.setBusinessKey(base + "business");
                be.getName().add(new Name("AMQP Test callbacks", lang));
                be.setBusinessServices(new BusinessServices());
                BusinessService bs = new BusinessService();
                bs.getName().add(new Name("AMQP Test service", lang));
                bs.setBindingTemplates(new BindingTemplates());
                bs.setBusinessKey(base + "business");
                bs.setServiceKey(base + "service");

                BindingTemplate bt = new BindingTemplate();
                bt.setBindingKey(base + "binding");
                bt.setServiceKey(base + "service");
                bt.setAccessPoint(new AccessPoint(amqpURL, AccessPointType.END_POINT.toString()));
                bt.setTModelInstanceDetails(new TModelInstanceDetails());

                TModelInstanceInfo version = UDDIClerk.createServiceInterfaceVersion("1.0", lang);
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(version);

                TModelInstanceInfo transport = new TModelInstanceInfo();
                transport.setTModelKey(UDDIConstants.TRANSPORT_AMQP);
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(transport);

                TModelInstanceInfo topic = new TModelInstanceInfo();
                topic.setTModelKey(TMODEL_DESTINATION_TYPE);
                topic.setInstanceDetails(new InstanceDetails());
                topic.getInstanceDetails().setInstanceParms("amq.topic");
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(topic);

                TModelInstanceInfo name = new TModelInstanceInfo();
                name.setTModelKey(TMODEL_DESTINATION_NAME);
                name.setInstanceDetails(new InstanceDetails());
                name.getInstanceDetails().setInstanceParms(TOPIC);
                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(name);

                bs.getBindingTemplates().getBindingTemplate().add(bt);

                be.getBusinessServices().getBusinessService().add(bs);
                try {
                        System.out.println("Registering tModel");
                        clerk.register(createKeyGenator);
                        System.out.println("Registering destination type tmodel");
                        clerk.register(destinationType);
                        System.out.println("Registering destination name tmodel");
                        clerk.register(destinationName);

                        System.out.println("Registering business with callback definition");
                        clerk.register(be);

                        Properties p = new Properties();
                        p.setProperty("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
                        p.setProperty("connectionfactory.qpidConnectionfactory", amqpURL);
                        p.setProperty("destination." + TOPIC, "amq.topic");

                        System.out.println("Connecting to AMQP at " + amqpURL);

                        Context context = new InitialContext(p);

                        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionfactory");
                        Connection connection = connectionFactory.createConnection();
                        connection.start();

                        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        Destination destination = (Destination) context.lookup(TOPIC);

                        MessageConsumer consumer = session.createConsumer(destination);

                        consumer.setMessageListener(this);
                        System.out.println("Connected and listening...");

                        Subscription sub = new Subscription();
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(1000));
                        sub.setBindingKey(base + "binding");
                        sub.setSubscriptionKey(base + "sub-fb");
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindBusiness(new FindBusiness());
                        sub.getSubscriptionFilter().getFindBusiness().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindBusiness().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindBusiness().getName().add(new Name(UDDIConstants.WILDCARD, null));

                        Subscription subscriptionBiz = clerk.register(sub, clerk.getUDDINode().getApiNode());

                        System.out.println("Registered FindBusiness subscription key: " + (subscriptionBiz.getSubscriptionKey()) + " bindingkey: " + subscriptionBiz.getBindingKey());

                        sub = new Subscription();
                        sub.setSubscriptionKey(base + "sub-fs");
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(1000));
                        sub.setBindingKey(base + "binding");
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindService(new FindService());
                        sub.getSubscriptionFilter().getFindService().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindService().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindService().getName().add(new Name(UDDIConstants.WILDCARD, null));

                        Subscription subscriptionSvc = clerk.register(sub, clerk.getUDDINode().getApiNode());

                        System.out.println("Registered FindService subscription key: " + (subscriptionSvc.getSubscriptionKey()) + " bindingkey: " + subscriptionSvc.getBindingKey());

                        sub = new Subscription();
                        sub.setNotificationInterval(DatatypeFactory.newInstance().newDuration(1000));
                        sub.setBindingKey(base + "binding");
                        sub.setSubscriptionKey(base + "sub-ft");
                        sub.setSubscriptionFilter(new SubscriptionFilter());
                        sub.getSubscriptionFilter().setFindTModel(new FindTModel());
                        sub.getSubscriptionFilter().getFindTModel().setFindQualifiers(new FindQualifiers());
                        sub.getSubscriptionFilter().getFindTModel().getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        sub.getSubscriptionFilter().getFindTModel().setName(new Name(UDDIConstants.WILDCARD, null));

                        Subscription subscriptionTM = clerk.register(sub, clerk.getUDDINode().getApiNode());

                        System.out.println("Registered FindTModel subscription key: " + (subscriptionTM.getSubscriptionKey()) + " bindingkey: " + subscriptionTM.getBindingKey());

                        System.out.println("Waiting for callbacks. Now would be a good time to launch either another program or juddi-gui to make some changes. Press any key to stop!");

                        System.out.println("Listening, press any key to quit");
                        System.in.read();
                        connection.close();

                } catch (Exception ex) {
                        ex.printStackTrace();
                } finally {
                        //clerk.unRegisterBusiness(be.getBusinessKey());
                        //clerk.unRegisterTModel(createKeyGenator.getTModelKey());
                }
        }

        @Override
        public void onMessage(Message msg) {

                System.out.println("Message received: " + msg);
        }
}
