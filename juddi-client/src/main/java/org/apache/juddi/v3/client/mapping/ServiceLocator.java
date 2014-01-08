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

import java.net.BindException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIKeyConvention;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessService;

/**
 * The ServiceLocator contacts the UDDI registry to lookup an Endpoint given a UDDI ServiceKey.
 *
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ServiceLocator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private UDDIClerk clerk;
	private Properties properties = new Properties();
	private UDDIServiceCache serviceCache = null;
	private String policy = null;
	private SelectionPolicy selectionPolicy = null;
	private URLLocalizer urlLocalizer = null;
	
	/**
	 * Requirement in the config is a clerk with access credentials to the UDDI server
	 * you want the locator to do lookups to. When a live cache is used the clerk
	 * will register a callback into this UDDI server and the clerk will therefore need
	 * inquiry and publish access. The credentials can be set in the uddi-client.xml
	 * configuration file.
	 *  
	 * @param clerk a UDDI Clerk with publish access to the UDDI Server.
	 * @throws ConfigurationException
	 */
	public ServiceLocator(UDDIClerk clerk) {
		super();
		this.clerk = clerk;
		properties =clerk.getUDDINode().getProperties();
	}
	
	public ServiceLocator(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties) throws ConfigurationException	{
		super();
		this.clerk = clerk;
		this.urlLocalizer = urlLocalizer;
		this.properties = properties;
		if (properties == null) properties = clerk.getUDDINode().getProperties();
	}
	
	public ServiceLocator withCache(URL baseCallbackURL) throws ConfigurationException {
		if (serviceCache == null) {
			serviceCache = initCache(baseCallbackURL);
		}
		return this;
	}
	
	/**
	 * A live cache will receive callbacks from the UDDI server in case there is an update to
	 * a service's bindings. All callbacks will clear the UDDIClientCache ensuring that subsequent
	 * lookups will contact the UDDI server for the latest binding information.
	 * 
	 * The baseCallbackURL can be set to solve binding issue. If
	 * 
	 * @param baseCallbackURL
	 * @return
	 * @throws ConfigurationException
	 * @throws BindException
	 */
	public ServiceLocator withLiveCache(URL baseCallbackURL) throws ConfigurationException, BindException {
		if (serviceCache == null) {
			serviceCache = initCache(baseCallbackURL);
			serviceCache.registerAsMBean();
		}
		if (! serviceCache.hasListener()) {
			serviceCache.publishAndRegisterHttpCallbackEndpoint();
		}
		return this;
	}
	
	public UDDIServiceCache getUDDIServiceCache() {
		return serviceCache;
	}
	
	/**
	 * The policy selection can be set as property "juddi.client.selection.policy"
	 * or it can be set programmatically using this method. A Policy is a class which 
	 * implements the SelectionPolicy interface. Known implementations are
	 * org.apache.juddi.v3.client.mapping.PolicyLocalFirst and 
	 * org.apache.juddi.v3.client.mapping.PolicyRoundRobin. If the policy is not
	 * set the default org.apache.juddi.v3.client.mapping.PolicyLocalFirst is used.
	 * 
	 * @param policy - the desired policy.
	 * @return ServiceLocator
	 * @see org.apache.juddi.v3.client.mapping.PolicyLocalFirst
	 * @see org.apache.juddi.v3.client.mapping.PolicyRoundRobin
	 */
	public ServiceLocator setPolicy(String policy) {
		this.policy = policy;
		return this;
	}
	/**
	 * Returns the selection policy in use by this instance of the ServiceLocator.
	 * 
	 * @return SelectionPolicy - the selection policy.
	 * @throws ConfigurationException
	 */
	public SelectionPolicy getPolicy() throws ConfigurationException {
		try {
			if (selectionPolicy==null) {
				if (policy==null) {
					policy = properties.getProperty("juddi.client.selection.policy", "org.apache.juddi.v3.client.mapping.PolicyLocalFirst");
				}
				@SuppressWarnings("unchecked")
				Class<? extends SelectionPolicy> selectionPolicyClass = (Class<? extends SelectionPolicy>)
					ClassUtil.forName(policy, this.getClass());
				selectionPolicy =  selectionPolicyClass.getConstructor(Properties.class).newInstance(properties);
			}
			return selectionPolicy;
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage(),e);
		}
	}
	
	/**
	 * Creates a new UDDIServiceCache, which brings up a new WebService Endpoint. This
	 * EndPoint will be called by the UDDI server if any service changes. A callback
	 * will result in cleaning the cache. 
	 * 
	 * @param baseCallbackURL
	 * @throws ConfigurationException
	 */
	private UDDIServiceCache initCache(URL baseCallbackURL) throws ConfigurationException {
		if (clerk==null) throw new ConfigurationException("The UDDIClerk is needed to use the UDDIServiceCache and is null");
		if (urlLocalizer==null) urlLocalizer = new URLLocalizerDefaultImpl(baseCallbackURL);
		try {
			log.info("Creating a UDDICLientCache");
			return new UDDIServiceCache(clerk, urlLocalizer, properties);
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage(),e);
		}
	}
	
	/**
	 * 
	 * @throws RemoteException
	 * @throws ConfigurationException
	 * @throws TransportException
	 */
	public void shutdown() throws RemoteException, ConfigurationException, TransportException {
		serviceCache.shutdown();
	}
	/**
	 * Looks up the Endpoints for a Service. If the cache is in use it will try to 
	 * obtain them from the cache. If no Endpoints are found, or if the cache is not
	 * in use, the clerk will do a lookup for this service. After Endpoints are found
	 * it will use a policy to pick one Endpoint to return. Returns null if no endpoints
	 * are found.
	 * 
	 * @param serviceKey
	 * @return
	 * @throws RemoteException
	 * @throws ConfigurationException
	 * @throws TransportException
	 */
	public String lookupEndpoint(String serviceKey) throws RemoteException, ConfigurationException, TransportException {
		Topology topology = null;
		if (serviceCache==null) { //nocache in use
			topology = lookupEndpointInUDDI(serviceKey);
		} else { //with cache
			//try to get it from the cache first
			topology = serviceCache.lookupService(serviceKey);
			if (topology==null) { //not found in the cache
				topology = lookupEndpointInUDDI(serviceKey);
			}
		}
	    if (topology!=null && topology.getEprs().size() > 0) {
	    	String epr = getPolicy().select(topology);
	    	return epr;
	    } else {
	    	return null;
	    }
	}
	
	/** 
	 * Looks up the Endpoints for a Service. If the cache is in use it will try to 
	 * obtain them from the cache. If no Endpoints are found, or if the cache is not
	 * in use, the clerk will do a lookup for this service. After Endpoints are found
	 * it will use a policy to pick one Endpoint to return. Returns null if no endpoints
	 * are found.
	 * 
	 * @param serviceQName
	 * @param portName
	 * @return 
	 * @throws TransportException 
	 * @throws ConfigurationException 
	 * @throws RemoteException 
	 */
	public String lookupEndpoint(QName serviceQName, String portName) throws RemoteException, ConfigurationException, TransportException {
		String serviceKey = UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart());
		return lookupEndpoint(serviceKey);
	}
	
	private Topology lookupEndpointInUDDI(String serviceKey) throws RemoteException, ConfigurationException, TransportException {
		Topology topology = null;
		
		BusinessService service = clerk.getServiceDetail(serviceKey);
		if (service==null) {
			log.warn("No Service with key " + serviceKey + " was found in the registry.");
			//TODO find service by tModel
		}
		if (service!=null && service.getBindingTemplates()!=null && service.getBindingTemplates().getBindingTemplate() != null) {
			ArrayList<String> eprs = new ArrayList<String>();
			BindingTemplates bindingTemplates = service.getBindingTemplates();
			if (bindingTemplates==null) {
				log.warn("Found service " + service.getName().get(0).getValue()
						  + " with serviceKey '" + serviceKey + "'" 
						  + " but no EPRs");
			} else {
				log.debug("Found service " + service.getName().get(0).getValue()
						  + " with serviceKey '" + serviceKey + "'" 
						  + " and " + bindingTemplates.getBindingTemplate().size() + " EPRs");
				//Loop over all bindingTemplates found and get the endpoints.
				for (BindingTemplate bindingTemplate : bindingTemplates.getBindingTemplate()) {
					AccessPoint accessPoint = bindingTemplate.getAccessPoint();
					if (AccessPointType.END_POINT.toString().equals(accessPoint.getUseType())) {
						String url = accessPoint.getValue();
						log.debug("epr= " + url);
						eprs.add(url);
					}
				}
				if (eprs.size()>0) {
					topology = new Topology(eprs);
				}
			}
		}
		if (serviceCache!=null && topology!=null) { //add to cache
			serviceCache.addService(serviceKey, topology);
		}
		return topology;
	}
	
	
	
}
