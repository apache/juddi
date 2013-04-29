package org.apache.juddi.subscription.notify;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.v3.client.Release;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class SMTPNotifier implements Notifier {
	
	Log log = LogFactory.getLog(this.getClass());
	String notificationEmailAddress = null;
	String from = null;
	Session session = null;
	Properties properties = null;
	
	private final static String[] mailProps = {"mail.smtp.from", "mail.smtp.host", "mail.smtp.port", 
		"mail.smtp.socketFactory.class", "mail.smtp.socketFactory.fallback", "mail.smtp.starttls.enable",
		"mail.smtp.socketFactory.port","mail.smtp.auth","mail.smtp.user","mail.smtp.password"};
	
	protected Properties getEMailProperties() throws ConfigurationException {
		if (properties==null) {
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
			// only read from file during testing
			if (properties.size()==0 || "jUDDI@example.org".equals(properties.getProperty("mail.smtp.from"))) {
				String curDir = System.getProperty("user.dir");
				if (! curDir.endsWith("uddi-tck")) curDir += "/uddi-tck";
				String version = Release.getRegistryVersion().replaceAll(".SNAPSHOT", "-SNAPSHOT");
				String path = curDir + "/target/juddi-tomcat-" + version + "/temp/";
				log.info("Path="+ path);
				File tmpFile = new File(path + "/juddi-mail.properties");
				if (tmpFile.exists()) {
					try {
						Properties fileProperties = new Properties();
						fileProperties.load(new FileInputStream(tmpFile));
						for (String key: mailProps) {
							if (fileProperties.containsKey(mailPrefix + key)) {
								properties.put(key, fileProperties.get(mailPrefix + key));
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.info("TEST only: Reading properties from " + tmpFile.getAbsolutePath() + ":" + properties);
				}
			}
		}
		return properties;
	}
	
	public SMTPNotifier(BindingTemplate bindingTemplate) throws URISyntaxException, ConfigurationException {
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
			if (Boolean.getBoolean(getEMailProperties().getProperty("mail.smtp.starttls.enable"))) {
				final String username = getEMailProperties().getProperty("mail.smtp.username");
				final String password = getEMailProperties().getProperty("mail.smtp.password");
				session = Session.getInstance(getEMailProperties(), new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
			} else {
				session = Session.getInstance(getEMailProperties());
			}
		}
	}

	public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {
		
		log.info("Sending notification email to " + notificationEmailAddress);
		
		try {
			if (session !=null && notificationEmailAddress != null) {
				MimeMessage message = new MimeMessage(session);
				InternetAddress address = new InternetAddress(notificationEmailAddress);
				Address[] to = {address};
				message.setRecipients(RecipientType.TO, to);
				message.setFrom(new InternetAddress(getEMailProperties().getProperty("mail.smtp.from", "jUDDI")));
				//maybe nice to use a template rather then sending raw xml.
				String subscriptionResultXML = JAXBMarshaller.marshallToString(body, JAXBMarshaller.PACKAGE_SUBSCR_RES);
				message.setText(subscriptionResultXML, "UTF-8");
				message.setSubject("UDDI Subscription Notification for subscription " 
						+ body.getSubscriptionResultsList().getSubscription().getSubscriptionKey());
				Transport.send(message);
			}
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
