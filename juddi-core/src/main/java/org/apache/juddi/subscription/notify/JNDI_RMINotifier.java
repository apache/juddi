package org.apache.juddi.subscription.notify;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.InitialContextInfo;
import org.apache.juddi.Property;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.model.TmodelInstanceInfo;
import org.uddi.api_v3.DispositionReport;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

public class JNDI_RMINotifier implements Notifier {
	
	Log log = LogFactory.getLog(this.getClass());
	UDDISubscriptionListenerPortType  subscriptionListenerPort = null;
	public static String JNDI_RMI_TRANSPORT_KEY = "uddi:uddi.org:transport:jndi-rmi";
	
	public JNDI_RMINotifier(BindingTemplate bindingTemplate) throws IOException, 
	URISyntaxException, NotBoundException, NamingException, JAXBException {
		super();
		if (!AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
			log.error("jndi-rmi enpoints only support AccessPointType " + AccessPointType.END_POINT);
		}
		String accessPointUrl = bindingTemplate.getAccessPointUrl().toLowerCase();
		if (!accessPointUrl.startsWith("jndi-rmi")) {
			log.warn("jndi-rmi accessPointUrl for bindingTemplate " + bindingTemplate.getEntityKey() + 
					" should start with 'jndi-rmi'");
		}
		InitialContext context = new InitialContext();
		for (TmodelInstanceInfo tModelInstanceInfo : bindingTemplate.getTmodelInstanceInfos()) {
			if (tModelInstanceInfo.getTmodelKey().equals(JNDI_RMI_TRANSPORT_KEY)) {
				if (log.isDebugEnabled()) log.debug("Found transport tModelKey " + tModelInstanceInfo.getTmodelKey());
				String instanceParmsStr = tModelInstanceInfo.getInstanceParms();
				if (instanceParmsStr!=null) {
					if (log.isDebugEnabled()) log.debug("Found instanceParms with value: " + instanceParmsStr);
					InitialContextInfo icInfo = (InitialContextInfo) JAXBMarshaller.unmarshallFromString(instanceParmsStr, JAXBMarshaller.PACKAGE_JUDDI);
					Properties properties = new Properties();
					for (Property property: icInfo.getContextProperty()) {
						if (log.isDebugEnabled()) {
							log.debug("Initial Context Property from instanceParms " + 
									   property.getName() + ":" + property.getValue());
						}	
						properties.put(property.getName(), property.getValue());
					}
					context = new InitialContext(properties);
					break;
				}
			}
		}
		URI accessPointURI = new URI(accessPointUrl);
		String path = accessPointURI.getPath();
		
		subscriptionListenerPort = (UDDISubscriptionListenerPortType) context.lookup(path);
		log.info("Successfully located " + path);
	}

	public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {
		return subscriptionListenerPort.notifySubscriptionListener(body);
	}
}
