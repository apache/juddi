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
package org.apache.juddi.subscription.notify;

import java.io.StringWriter;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.JAXB;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.config.ResourceConfig;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.model.BindingTemplate;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * This class extends the default SMTP notifier The following properties can be
 * set in the juddiv3.xml, or as System params.
 *
 * "mail.smtp.from", "mail.smtp.host", "mail.smtp.port",
 * "mail.smtp.socketFactory.class", "mail.smtp.socketFactory.fallback",
 * "mail.smtp.starttls.enable",
 * "mail.smtp.socketFactory.port","mail.smtp.auth","mail.smtp.user","mail.smtp.password"
 *
 * The following properties can be set juddiv3.xml.
 *
 * @author Kurt Stam
 */
public class USERFRIENDLYSMTPNotifier extends SMTPNotifier {

        public USERFRIENDLYSMTPNotifier(BindingTemplate bindingTemplate) throws URISyntaxException, Exception {
                super(bindingTemplate);
                publisherName = bindingTemplate.getAuthorizedName();
        }

        String publisherName;

        @Override
        public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {

                try {
                        log.info("Sending notification email to " + notificationEmailAddress + " from " + getEMailProperties().getProperty("mail.smtp.from", "jUDDI"));
                        if (session != null && notificationEmailAddress != null) {
                                MimeMessage message = new MimeMessage(session);
                                InternetAddress address = new InternetAddress(notificationEmailAddress);
                                Address[] to = {address};
                                message.setRecipients(RecipientType.TO, to);
                                message.setFrom(new InternetAddress(getEMailProperties().getProperty("mail.smtp.from", "jUDDI")));
                                //maybe nice to use a template rather then sending raw xml.
                                String subscriptionResultXML = JAXBMarshaller.marshallToString(body, JAXBMarshaller.PACKAGE_SUBSCR_RES);
                                Multipart mp = new MimeMultipart();
                                
                                MimeBodyPart content = new MimeBodyPart();
                                String msg_content = ResourceConfig.getGlobalMessage("notifications.smtp.userfriendly.body");

                                msg_content = String.format(msg_content,
                                     StringEscapeUtils.escapeHtml(this.publisherName),
                                     StringEscapeUtils.escapeHtml(AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID, "(unknown node id!)")),
                                     GetSubscriptionType(body),
                                     GetChangeSummary(body));
                                
                                content.setContent(msg_content, "text/html; charset=UTF-8;");
                                mp.addBodyPart(content);
                                
                                MimeBodyPart attachment = new MimeBodyPart();
                                attachment.setContent(subscriptionResultXML, "text/xml; charset=UTF-8;");
                                attachment.setFileName("uddiNotification.xml");
                                mp.addBodyPart(attachment);
                                
                                message.setContent(mp);
                                message.setSubject(ResourceConfig.getGlobalMessage("notifications.smtp.userfriendly.subject") + " "
                                     + body.getSubscriptionResultsList().getSubscription().getSubscriptionKey());
                                Transport.send(message);
                        }
                        else throw new DispositionReportFaultMessage("Session is null!", null);
                } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new DispositionReportFaultMessage(e.getMessage(), null);
                }

                DispositionReport dr = new DispositionReport();
                Result res = new Result();
                dr.getResult().add(res);

                return dr;
        }

        static String GetChangeSummary(NotifySubscriptionListener body) {
                SubscriptionResultsList r = body.getSubscriptionResultsList();
                StringWriter sw = new StringWriter();
                
                if (r.getAssertionStatusReport() != null) {
                        JAXB.marshal(r.getAssertionStatusReport(), sw);
                }
                else if (r.getBindingDetail() != null) {
                        JAXB.marshal(r.getBindingDetail(), sw);
                }
                else if (r.getBusinessDetail() != null) {
                        JAXB.marshal(r.getBusinessDetail(), sw);
                }
                else if (r.getBusinessList() != null) {
                        JAXB.marshal(r.getBusinessList(), sw);
                }
                else if (r.getRelatedBusinessesList() != null) {
                        JAXB.marshal(r.getRelatedBusinessesList(), sw);
                }
                else if (r.getServiceDetail() != null) {
                        JAXB.marshal(r.getServiceDetail(), sw);
                }
                else if (r.getServiceList() != null) {
                        JAXB.marshal(r.getServiceList(), sw);
                }
                else if (r.getTModelDetail() != null) {
                        JAXB.marshal(r.getTModelDetail(), sw);
                }
                else if (r.getTModelList() != null) {
                        JAXB.marshal(r.getTModelList(), sw);
                }
                
                return "<pre>" +StringEscapeUtils.escapeHtml(sw.toString()) + "</pre>";
        }

        static String GetSubscriptionType(NotifySubscriptionListener body) {
                if (body != null && body.getSubscriptionResultsList() != null
                     && body.getSubscriptionResultsList().getSubscription() != null
                     && body.getSubscriptionResultsList().getSubscription().getSubscriptionFilter() != null) {
                        SubscriptionFilter sub = body.getSubscriptionResultsList().getSubscription().getSubscriptionFilter();
                        if (sub.getFindBinding() != null) {
                                return " binding search results";
                        }
                        if (sub.getFindBusiness() != null) {
                                return " business search results";
                        }
                        if (sub.getFindRelatedBusinesses() != null) {
                                return " related business search results";
                        }
                        if (sub.getFindService() != null) {
                                return " service search results";
                        }
                        if (sub.getFindTModel() != null) {
                                return " tModel search results";
                        }
                        if (sub.getGetAssertionStatusReport() != null) {
                                return " assertion status report";
                        }
                        if (sub.getGetBindingDetail() != null) {
                                return " details on a specific binding";
                        }
                        if (sub.getGetBusinessDetail() != null) {
                                return " details on a specific business";
                        }
                        if (sub.getGetServiceDetail() != null) {
                                return " details on a specific service";
                        }
                        if (sub.getGetTModelDetail() != null) {
                                return " details on a specific tModel";
                        }
                }
                return " (unable to determine what the subscription type is)";

        }

}
