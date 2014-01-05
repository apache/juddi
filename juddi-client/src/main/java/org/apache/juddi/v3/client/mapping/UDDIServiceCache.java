/*
 * Copyright 2001-2011 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.client.mapping;

import java.lang.management.ManagementFactory;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIKeyConvention;
import org.apache.juddi.v3.client.mapping.wsdl.ReadWSDL;
import org.apache.juddi.v3.client.mapping.wsdl.WSDL2UDDI;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

/**
 * The UDDIServiceCache maintains a cache of the service bindingTemplates of all service
 * the lookupService method is called for. 
 * 
 * To prevent the cache from going stale it
 * registers an Subscription with the UDDI server. The subscription matches any update
 * on any service. When the subscription is matched, the UDDI server will callback to
 * the UDDIClientSubscriptionListenerService which is a WebService Endpoint brought
 * up by this cache.
 * 
 * The Cache also registers an MBean which allows the 
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDIServiceCache implements UDDIServiceCacheMBean {
	
	public static String UDDI_ORG_NS                       = "urn:uddi-org:v3_service";
	public static String UDDI_CLIENT_SUBSCRIPTION_LISTENER = "UDDIClientSubscriptionListenerService";
	public static QName  SUBSCRIPTION_LISTENER_SERVICE_NAME= new QName(UDDI_ORG_NS, UDDI_CLIENT_SUBSCRIPTION_LISTENER);
	public static String SUBSCRIPTION_LISTENER_PORT_NAME   = "UDDIClientSubscriptionListenerImplPort";
	public static String DEFAULT_SUBSCRIPTION_LISTENER_URL = "http://localhost:8080/subscriptionlistener_uddi_client";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private UDDIClerk clerk = null;
	private URLLocalizer urlLocalizer = null;
	private Properties properties = null;
	
	private String subscriptionKey = null;
	private Endpoint listenerEndpoint = null;
	private URL listenerServiceUrl = null;
	private ObjectName mbeanName = null;
	
	
	private ConcurrentHashMap<String, Topology> serviceLocationMap = new ConcurrentHashMap<String, Topology>();
	
	public UDDIServiceCache() {
		super();
	}
	
	public UDDIServiceCache(UDDIClerk clerk) throws MalformedURLException {
		super();
		this.clerk = clerk;
		this.urlLocalizer = new URLLocalizerDefaultImpl(null);
		this.properties = clerk.getUDDINode().getProperties();
	}
	
	public UDDIServiceCache(UDDIClerk clerk, URL callbackBaseUrl) {
		super();
		this.clerk = clerk;
		this.urlLocalizer = new URLLocalizerDefaultImpl(callbackBaseUrl);
        this.properties = clerk.getUDDINode().getProperties();
	}

	public UDDIServiceCache(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties) throws DatatypeConfigurationException, MalformedURLException, RemoteException, ConfigurationException, WSDLException, TransportException, Exception {
		super();
		this.clerk = clerk;
		this.urlLocalizer = urlLocalizer;
		
		Properties properties2 = clerk.getUDDINode().getProperties();
		if (properties2!=null) {
			properties2.putAll(properties);
		} else {
			properties2 = properties;
		}
		this.properties = properties2;
	}
	
	public UDDIClerk getClerk() {
		return clerk;
	}
	
	public void publishAndRegisterHttpCallbackEndpoint() throws BindException {
		if (clerk!=null && listenerEndpoint==null) {
			try {
				listenerServiceUrl = new URL(urlLocalizer.rewrite(new URL(DEFAULT_SUBSCRIPTION_LISTENER_URL)));
				WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(clerk, urlLocalizer, properties);
				Definition wsdlDefinition = new ReadWSDL().readWSDL("org/apache/juddi/v3/client/mapping/UDDIClientSubscriptionListener.wsdl");
				
				String bindingKey = wsdl2UDDI.registerBusinessService(
						SUBSCRIPTION_LISTENER_SERVICE_NAME, 
						SUBSCRIPTION_LISTENER_PORT_NAME, listenerServiceUrl, wsdlDefinition).getBindingKey();
				UDDISubscriptionListenerPortType subscriptionListener = new UDDIClientSubscriptionListenerImpl(bindingKey, this);
				log.info("Bringing up a UDDIClientSubscriptionListenerImpl on Endpoint " + listenerServiceUrl.toExternalForm());
				listenerEndpoint = Endpoint.create(subscriptionListener);
				listenerEndpoint.publish(listenerServiceUrl.toExternalForm());
				
				log.info("Registering a CallbackSubscription to this endpoint using bindingKey " + bindingKey);
				registerSubscription(bindingKey);
				
			} catch (RuntimeException t) {
				listenerEndpoint = null;
				if (t.getCause() instanceof BindException) {
					throw new BindException(t.getCause().getMessage());
				} else {
					throw t;
				}
			} catch (Exception e) {
				log.error("Cannot publish or register the CallbackEndpoint " + e.getMessage(),e);
			}
		}
	}
	
	public boolean hasListener() {
		if (listenerEndpoint==null) return false;
		return listenerEndpoint.isPublished();
	}
	
	public void registerAsMBean() {
		try {
			if (clerk!=null) {
				mbeanName = new ObjectName("apache.juddi.client:type=UDDIServerCache-" + clerk.getManagerName() + "-" + clerk.getName());
			} else {
				mbeanName = new ObjectName("apache.juddi.client:type=UDDIServerCache-" + this);
			}
			MBeanServer mbeanServer = getMBeanServer();
			if (mbeanServer!=null) {
				mbeanServer.registerMBean(this, mbeanName);
			} else {
				mbeanServer=null;
			}
		} catch (Exception e) {
			log.error("Not able to register the UDDIServiceCache MBean " + e.getMessage(),e);
		}
	}
	
	public void shutdown() {
		if (subscriptionKey!=null) {
			clerk.unRegisterSubscription(subscriptionKey);
		}
		if (listenerEndpoint!=null) {
			listenerEndpoint.stop();
			WSDL2UDDI wsdl2UDDI;
			try {
				wsdl2UDDI = new WSDL2UDDI(clerk, urlLocalizer, properties);
				wsdl2UDDI.unRegisterBusinessService(
						SUBSCRIPTION_LISTENER_SERVICE_NAME, 
						SUBSCRIPTION_LISTENER_PORT_NAME, listenerServiceUrl);
			} catch (Exception e) {
				/* we did our best*/
				log.debug(e.getMessage(),e);
			}
		}
		if (mbeanName!=null) {
			try {
				MBeanServer mbeanServer = getMBeanServer();
				if (mbeanServer!=null) {
					mbeanServer.unregisterMBean(mbeanName);
				}
			} catch (Exception e) {
				/* we did our best*/
				log.debug(e.getMessage(),e);
			}
		}
	}
	
	public void removeAll() {
		log.info("Flushing the client side " + clerk.getManagerName() + " UDDIServiceCache ");
		for (String key : serviceLocationMap.keySet()) {
			serviceLocationMap.get(key);
		}
	}
	/**
	 * Adds or updates epr information for the given serviceKey.
	 * @param serviceKey
	 * @param eprs
	 */
	public void addService(String serviceKey, Topology topology) {
		serviceLocationMap.put(serviceKey, topology);
	}
	
	public Topology lookupService(String serviceKey) {
		return serviceLocationMap.get(serviceKey);
	}
	
	public void removeService(String serviceKey) {
		serviceLocationMap.remove(serviceKey);
	}
	
        /**
         * Create a subscription for changes in any Service in the Registry
         * @throws DatatypeConfigurationException 
         */
	public void registerSubscription(String bindingKey) throws DatatypeConfigurationException {
		
		String subscriptionKey = UDDIKeyConvention.getSubscriptionKey(properties);
		//Create a subscription for changes in any Service in the Registry
		FindService findAllServices = new FindService();
		FindQualifiers qualifiers = new FindQualifiers();
		qualifiers.getFindQualifier().add("approximateMatch");
		
		findAllServices.setFindQualifiers(qualifiers);
		
		Name name = new Name();
		name.setValue("%");
		findAllServices.getName().add(name);
		
		SubscriptionFilter filter = new SubscriptionFilter();
		filter.setFindService(findAllServices);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionFilter(filter);
		subscription.setBindingKey(bindingKey);
		subscription.setBrief(true);
		Duration oneMinute = DatatypeFactory.newInstance().newDuration("PT1M");
		subscription.setNotificationInterval(oneMinute);
		subscription.setSubscriptionKey(subscriptionKey);
		clerk.register(subscription);
		this.subscriptionKey = subscriptionKey;
	}

	public Map<String, Topology> getServiceCacheMap() {
		return serviceLocationMap;
	}
	
	private MBeanServer getMBeanServer() {
        MBeanServer mbserver = null;
        ArrayList<MBeanServer> mbservers = MBeanServerFactory.findMBeanServer(null);
        if (mbservers.size() > 0) {
            mbserver = (MBeanServer) mbservers.get(0);
        }
        if (mbserver != null && log.isDebugEnabled()) {
        	log.debug("Found MBean server");
        } else {
        	mbserver = ManagementFactory.getPlatformMBeanServer();
        }
        return mbserver;
    }

	/** Method callable from the mbean */
	@Override
	public int getServiceCacheSize() {
		return serviceLocationMap.size();
	}

	/** Method callable from the mbean */
	@Override
	public Set<String> getCacheEntries() {
		return serviceLocationMap.keySet();
	}

	/** Method callable from the mbean */
	@Override
	public void resetCache() {
		serviceLocationMap.clear();
	}
	
	
	
	
}
