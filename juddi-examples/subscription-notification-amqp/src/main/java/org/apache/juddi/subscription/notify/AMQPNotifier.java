/*
 * Copyright 2013 The Apache Software Foundation.
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

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.model.BindingTemplate;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * AMQP Notifier
 *
 * This is designed to enable users to setup AMQP based alerts for UDDI
 * subscriptions
 *
 * This class is partically complete, but it is largely untested and lacks any
 * kind of
 *
 * the following settings need to be added to the juddiv3.xml file
 * amqp.java.naming.factory.initial=org.apache.qpid.jndi.PropertiesFileInitialContextFactory
 * amqp.connectionfactory.qpidConnectionfactory amqp.destination=(some topic or
 * queue name) amqp.destination.type=topic
 *
 * usage create a service/bindingtemplate/accessPoint where the value is
 * amqp://url_to_qpid/amqp The useType must be "endPoint". create a subscription
 * where the binding template reference points to this endpoint. trigger the
 * subscription and wait for delivery.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class AMQPNotifier implements Notifier {

        Log log = LogFactory.getLog(this.getClass());
        String destination = null;

        String exchangeType = null;
        String exchangeName = null;

        public AMQPNotifier(BindingTemplate bindingTemplate) throws URISyntaxException, ConfigurationException {
                super();
                if (!AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
                        log.error("AMQP enpoints only support AccessPointType " + AccessPointType.END_POINT);
                }
                String accessPointUrl = bindingTemplate.getAccessPointUrl().toLowerCase();
                if (!accessPointUrl.startsWith("amqp:")) {
                        log.warn("AMQP accessPointUrl for bindingTemplate " + bindingTemplate.getEntityKey()
                                + " should start with 'amqp:'");
                } 
                destination = accessPointUrl;
                for (int i = 0; i < bindingTemplate.getTmodelInstanceInfos().size(); i++) {
                        if (bindingTemplate.getTmodelInstanceInfos().get(i).getTmodelKey().equals(Demo.TMODEL_DESTINATION_TYPE)) {
                                exchangeType = bindingTemplate.getTmodelInstanceInfos().get(i).getInstanceParms();
                        }
                        if (bindingTemplate.getTmodelInstanceInfos().get(i).getTmodelKey().equals(Demo.TMODEL_DESTINATION_NAME)) {
                                exchangeName = bindingTemplate.getTmodelInstanceInfos().get(i).getInstanceParms();
                        }
                }
        }

        @Override
        public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {
                Connection connection = null;
                Context context = null;
                boolean success = false;
                String err = null;
                try {
                        if (destination != null && exchangeType != null && exchangeName != null) {
                                log.info("Sending notification AMQP to " + destination);
                                Properties properties = new Properties();

                                properties.put("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
                                properties.put("connectionfactory.qpidConnectionfactory", destination);
                                properties.put("destination." + exchangeName,exchangeType);

                                context = new InitialContext(properties);

                                ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionfactory");
                                connection = connectionFactory.createConnection();
                                connection.start();

                                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                                Destination destinationLocal = (Destination) context.lookup(exchangeName);

                                MessageProducer messageProducer = session.createProducer(destinationLocal);

                                String subscriptionResultXML = JAXBMarshaller.marshallToString(body, JAXBMarshaller.PACKAGE_SUBSCR_RES);
                                TextMessage message = session.createTextMessage(subscriptionResultXML);
                                messageProducer.send(message);
                                success = true;

                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        log.error("Error deliverying AMQP subscription " + e.getMessage());
                        log.debug("Error deliverying AMQP subscription " + e.getMessage(),e);
                        err = e.getMessage();

                } finally {
                        try {
                                if (connection != null) {
                                        connection.close();
                                }
                        } catch (JMSException ex) {
                                log.error(null, ex);
                        }
                        try {
                                if (context != null) {
                                        context.close();
                                }
                        } catch (NamingException ex) {
                                log.error(null, ex);
                        }
                }
                if (!success) {
                        throw new DispositionReportFaultMessage(err, null);
                }
                DispositionReport dr = new DispositionReport();
                Result res = new Result();
                dr.getResult().add(res);

                return dr;
        }
}
