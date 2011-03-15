package org.apache.juddi.subscription.notify;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Properties;

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
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class SMTPNotifier implements Notifier {
	
	Log log = LogFactory.getLog(this.getClass());
	String notificationEmailAddress = null;
	String from = null;
	Session session = null;
	String transport = null;
	
	public SMTPNotifier(BindingTemplate bindingTemplate) throws URISyntaxException, ConfigurationException {
		super();
		if (!AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
			log.error("smtp enpoints only support AccessPointType " + AccessPointType.END_POINT);
		}
		String accessPointUrl = bindingTemplate.getAccessPointUrl().toLowerCase();
		if (!accessPointUrl.startsWith("mailto")) {
			log.warn("smtp accessPointUrl for bindingTemplate " + bindingTemplate.getEntityKey() + 
					" should start with 'mailto'");
		}
		URI accessPointURI = new URI(accessPointUrl);
		notificationEmailAddress = accessPointURI.getUserInfo() + "@" + accessPointURI.getHost();
		
		Properties properties = new Properties();
		String host = AppConfig.getConfiguration().getString(Property.JUDDI_EMAIL_HOST,"localhost");
		properties.put("mail.smtp.host", host);
		String port = AppConfig.getConfiguration().getString(Property.JUDDI_EMAIL_PORT,"25");
		properties.put("mail.smtp.port", port);
		
		boolean isAuth = AppConfig.getConfiguration().getBoolean(Property.JUDDI_EMAIL_AUTH,false);
		if (isAuth) {
			String user = AppConfig.getConfiguration().getString(Property.JUDDI_EMAIL_USER,"juddi");
			properties.put("mail.smtp.user", user);
			String password = AppConfig.getConfiguration().getString(Property.JUDDI_EMAIL_PASSWORD,"juddi");
			properties.put("mail.smtp.user", password);
		}
		session = Session.getDefaultInstance(properties);
		from = AppConfig.getConfiguration().getString(Property.JUDDI_EMAIL_FROM,"juddi");
		transport = AppConfig.getConfiguration().getString(Property.JUDDI_EMAIL_TRANSPORT,"smtp");
	}

	public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {
		
		log.info("Sending notification email to " + notificationEmailAddress);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			//maybe nice to use a template rather then sending raw xml.
			String subscriptionResultXML = JAXBMarshaller.marshallToString(body, JAXBMarshaller.PACKAGE_UDDIAPI);
			message.setText(subscriptionResultXML);
			message.setSubject("UDDI Subscription Notification for subscription " 
					+ body.getSubscriptionResultsList().getSubscription().getSubscriptionKey());
			Transport.send(message);
			
		} catch (Exception e) {
			throw new DispositionReportFaultMessage(e.getMessage(), null);
		}
		
		DispositionReport dr = new DispositionReport();
		Result res = new Result();
		dr.getResult().add(res);
		
		return dr;
	}
}
