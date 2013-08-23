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
 * amqp.java.naming.factory.initial
 * amqp.connectionfactory.qpidConnectionfactory
 * amqp.destination
 * amqp.destination.type amq.topic
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class AMQPNotifier implements Notifier {

    Log log = LogFactory.getLog(this.getClass());
    String destination = null;

    public AMQPNotifier(BindingTemplate bindingTemplate) throws URISyntaxException, ConfigurationException {
        super();
        if (!AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
            log.error("AMQP enpoints only support AccessPointType " + AccessPointType.END_POINT);
        }
        String accessPointUrl = bindingTemplate.getAccessPointUrl().toLowerCase();
        if (!accessPointUrl.startsWith("amqp:")) {
            log.warn("AMQP accessPointUrl for bindingTemplate " + bindingTemplate.getEntityKey()
                    + " should start with 'amqp:'");
        } else {
            destination = accessPointUrl.substring(accessPointUrl.indexOf(":") + 1);

        }
    }

    @Override
    public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {
        Connection connection = null;
        Context context = null;
        boolean success = false;
        String err = null;
        try {
            if (destination != null) {
                log.info("Sending notification AMQP to " + destination);
                Properties properties = new Properties();
                 
                properties.put("java.naming.factory.initial", 
                        AppConfig.getConfiguration().getString("amqp.java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory"));
                properties.put("connectionfactory.qpidConnectionfactory", destination);
                properties.put("destination." +AppConfig.getConfiguration().getString("amqp.destination") ,
                        AppConfig.getConfiguration().getString("amqp.destination.type"));
                properties.load(this.getClass().getResourceAsStream("hello.properties"));
                context = new InitialContext(properties);

                ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("qpidConnectionfactory");
                connection = connectionFactory.createConnection();
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destinationLocal = (Destination) context.lookup("UDDISubscriptionUpdates");

                MessageProducer messageProducer = session.createProducer(destinationLocal);


                String subscriptionResultXML = JAXBMarshaller.marshallToString(body, JAXBMarshaller.PACKAGE_SUBSCR_RES);
                TextMessage message = session.createTextMessage(subscriptionResultXML);
                messageProducer.send(message);
                success = true;

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            err = e.getMessage();

        } finally {
            try {
                connection.close();
            } catch (JMSException ex) {
                log.error(null, ex);
            }
            try {
                context.close();
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
