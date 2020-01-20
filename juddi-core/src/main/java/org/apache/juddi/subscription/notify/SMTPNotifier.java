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

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.config.ResourceConfig;
import org.apache.juddi.cryptor.CryptorFactory;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.model.BindingTemplate;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * This class sends Email alerts when a specific subscription is tripped. 
 * The following properties can be set in the juddiv3.xml, or as System params.
 * 
 * "mail.smtp.from", "mail.smtp.host", "mail.smtp.port", 
 * "mail.smtp.socketFactory.class", "mail.smtp.socketFactory.fallback", "mail.smtp.starttls.enable",
 * "mail.smtp.socketFactory.port","mail.smtp.auth","mail.smtp.user","mail.smtp.password"
 *		
 * The following properties can be set juddiv3.xml.
 * 
 * @author Kurt Stam
 */
public class SMTPNotifier implements Notifier {

	protected static final Log log = LogFactory.getLog(SMTPNotifier.class);
	protected String notificationEmailAddress = null;
	//String from = null;
	protected Session session = null;
	protected Properties properties = null;

	protected final static String[] mailProps = {"mail.smtp.from", "mail.smtp.host", "mail.smtp.port", 
		"mail.smtp.socketFactory.class", "mail.smtp.socketFactory.fallback", "mail.smtp.starttls.enable",
		"mail.smtp.socketFactory.port","mail.smtp.auth","mail.smtp.user","mail.smtp.password","mail.debug"};

	protected final Properties getEMailProperties() throws ConfigurationException {
		if (properties==null || properties.isEmpty()) {
			properties = new Properties();
			String mailPrefix = AppConfig.getConfiguration().getString(Property.JUDDI_EMAIL_PREFIX, Property.DEFAULT_JUDDI_EMAIL_PREFIX);
			if (! mailPrefix.endsWith(".")) mailPrefix = mailPrefix + ".";
			for (String key: mailProps) {
				if (AppConfig.getConfiguration().containsKey(mailPrefix + key)) {
					properties.put(key, AppConfig.getConfiguration().getProperty(mailPrefix + key));
				} else if (System.getProperty(mailPrefix + key) != null) {
					properties.put(key, System.getProperty(mailPrefix + key));
				}
			}
		}
		return properties;
	}

	public SMTPNotifier(BindingTemplate bindingTemplate) throws URISyntaxException, Exception {
		super();
		if (!AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
			log.error("smtp enpoints only support AccessPointType " + AccessPointType.END_POINT);
		}
		String accessPointUrl = bindingTemplate.getAccessPointUrl().toLowerCase();
		if (!accessPointUrl.startsWith("mailto:")) {
			log.warn("smtp accessPointUrl for bindingTemplate " + bindingTemplate.getEntityKey() + 
					" should start with 'mailto'");
			//TODO maybe update the user's bindingTemplate with the error?, and also validate setting onsave
		} else {
			notificationEmailAddress = accessPointUrl.substring(accessPointUrl.indexOf(":")+1);
                        boolean auth=(getEMailProperties().getProperty("mail.smtp.auth", "false")).equalsIgnoreCase("true");
			if (auth) {
				final String username = getEMailProperties().getProperty("mail.smtp.user");
				String pwd = getEMailProperties().getProperty("mail.smtp.password");
                                //decrypt if possible
				if (getEMailProperties().getProperty("mail.smtp.password" + Property.ENCRYPTED_ATTRIBUTE, "false").equalsIgnoreCase("true"))
				{
					try {
						pwd = CryptorFactory.getCryptor().decrypt(pwd);
					} catch (NoSuchPaddingException ex) {
						log.error("Unable to decrypt settings",ex);
					} catch (NoSuchAlgorithmException ex) {
						log.error("Unable to decrypt settings",ex);
					} catch (InvalidAlgorithmParameterException ex) {
						log.error("Unable to decrypt settings",ex);
					} catch (InvalidKeyException ex) {
						log.error("Unable to decrypt settings",ex);
					} catch (IllegalBlockSizeException ex) {
						log.error("Unable to decrypt settings",ex);
					} catch (BadPaddingException ex) {
						log.error("Unable to decrypt settings",ex);
					}
				}
				final String password = pwd;
                                log.debug("SMTP username = " + username + " from address = " + notificationEmailAddress);
                                Properties eMailProperties = getEMailProperties();
                                eMailProperties.remove("mail.smtp.user");
                                eMailProperties.remove("mail.smtp.password");
				session = Session.getInstance(getEMailProperties(), new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
			} else {
                                Properties eMailProperties = getEMailProperties();
                                eMailProperties.remove("mail.smtp.user");
                                eMailProperties.remove("mail.smtp.password");
				session = Session.getInstance(eMailProperties);
			}
		}
	}

	public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {

		

		try {
                        log.info("Sending notification email to " + notificationEmailAddress + " from " + getEMailProperties().getProperty("mail.smtp.from", "jUDDI"));
			if (session !=null && notificationEmailAddress != null) {
				MimeMessage message = new MimeMessage(session);
				InternetAddress address = new InternetAddress(notificationEmailAddress);
				Address[] to = {address};
				message.setRecipients(RecipientType.TO, to);
				message.setFrom(new InternetAddress(getEMailProperties().getProperty("mail.smtp.from", "jUDDI")));
				//maybe nice to use a template rather then sending raw xml.
				String subscriptionResultXML = JAXBMarshaller.marshallToString(body, JAXBMarshaller.PACKAGE_SUBSCR_RES);
				message.setText(subscriptionResultXML, "UTF-8");
                                //message.setContent(subscriptionResultXML, "text/xml; charset=UTF-8;");
				message.setSubject(ResourceConfig.getGlobalMessage("notifications.smtp.default.subject") + " " 
						+ StringEscapeUtils.escapeHtml(body.getSubscriptionResultsList().getSubscription().getSubscriptionKey()));
				Transport.send(message);
			}
                        else throw new DispositionReportFaultMessage("Session is null!", null);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new DispositionReportFaultMessage(e.getMessage(), null);
		}

		DispositionReport dr = new DispositionReport();
		Result res = new Result();
		dr.getResult().add(res);

		return dr;
	}
}
