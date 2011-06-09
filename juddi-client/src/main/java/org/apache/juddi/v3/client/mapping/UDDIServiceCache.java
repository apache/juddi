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

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDIServiceCache {
	
	private Endpoint endpoint = null;
	private String bindingKey = null;
	private String subscriptionKey = null;
	private Log log = LogFactory.getLog(this.getClass());
	private UDDIClerk clerk = null;
	private URLLocalizer urlLocalizer = null;
	private Integer port = null;
	private String serverName = "localhost";
	private Properties properties = null;
	URL serviceUrl = null;
	private ConcurrentHashMap<String, Topology> serviceLocationMap = new ConcurrentHashMap<String, Topology>();
	
	public UDDIClerk getClerk() {
		return clerk;
	}

	public void setClerk(UDDIClerk clerk) {
		this.clerk = clerk;
	}

	public UDDIServiceCache(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties) throws DatatypeConfigurationException, MalformedURLException, RemoteException, ConfigurationException, WSDLException, TransportException {
		super();
		this.clerk = clerk;
		this.urlLocalizer = urlLocalizer;
		this.properties = properties;
		this.subscriptionKey = Property.getSubscriptionKey(properties);
		
		init();
	}

	private void init() throws DatatypeConfigurationException, MalformedURLException, WSDLException, RemoteException, ConfigurationException, TransportException {
		
		QName serviceQName = new QName("urn:uddi-org:v3_service", "UDDISubscriptionListenerService");
		String portName = "UDDISubscriptionListenerImplPort";
		String url = urlLocalizer.rewrite(new URL("http://localhost:8080/subscriptionlistener_" + clerk.getManagerName()));
		serviceUrl = new URL(url);
		
		bindingKey = Property.getBindingKey(properties, serviceQName, portName, serviceUrl);
		endpoint = Endpoint.create(new UDDIClientSubscriptionListenerImpl(bindingKey,this));
		endpoint.publish(serviceUrl.toExternalForm());
		
		WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(clerk, urlLocalizer, properties);
		Definition wsdlDefinition = new ReadWSDL().readWSDL("uddi_v3_service.wsdl");
		bindingKey = wsdl2UDDI.register(serviceQName, portName, serviceUrl, wsdlDefinition).getBindingKey();
		
		registerSubscription();
	}
	
	public void shutdown() throws RemoteException, ConfigurationException, TransportException {
		unRegisterSubscription();
		QName serviceQName = new QName("urn:uddi-org:v3_service", "UDDISubscriptionListenerService");
		String portName = "UDDISubscriptionListenerImplPort";
		WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(clerk, urlLocalizer, properties);
		wsdl2UDDI.unRegister(serviceQName, portName, serviceUrl);
		endpoint.stop();
		UDDIClientSubscriptionListenerImpl.getServiceCacheMap().remove(bindingKey);
	}
	
	public void removeAll() {
		for (String key : serviceLocationMap.keySet()) {
			serviceLocationMap.remove(key);
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
	
	public void registerSubscription() throws DatatypeConfigurationException {
		
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
	}
	
	public void unRegisterSubscription() {
		clerk.unRegisterSubscription(subscriptionKey);
	}
	
	
}