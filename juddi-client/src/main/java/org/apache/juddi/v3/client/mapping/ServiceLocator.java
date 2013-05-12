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

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;

import javax.wsdl.WSDLException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIKeyConvention;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessService;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ServiceLocator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private UDDIClerk clerk;
	private Properties properties = new Properties();
	private UDDIServiceCache serviceCache = null;
	private SelectionPolicy selectionPolicy = null;
	
	public ServiceLocator(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties) throws ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DatatypeConfigurationException, MalformedURLException, RemoteException, ConfigurationException, WSDLException, TransportException, Exception {
		super();

		this.clerk = clerk;
		this.properties = properties;
		
		serviceCache = new UDDIServiceCache(clerk, urlLocalizer, properties);
		String policy = properties.getProperty("juddi.client.selection.policy", "org.apache.juddi.v3.client.mapping.PolicyLocalFirst");
		@SuppressWarnings("unchecked")
		Class<? extends SelectionPolicy> selectionPolicyClass = (Class<? extends SelectionPolicy>)
			ClassUtil.forName(policy, this.getClass());
		selectionPolicy =  selectionPolicyClass.getConstructor(Properties.class).newInstance(properties);
	}
	
	public void shutdown() throws RemoteException, ConfigurationException, TransportException {
		serviceCache.shutdown();
	}
	
	public void addService(String serviceKey) {
		Topology topology = lookupEndpointInUDDI(serviceKey);
		serviceCache.addService(serviceKey, topology);
	}
	
	public void removeService(String serviceKey) {
		serviceCache.removeService(serviceKey);
	}
	
	
	public String lookupEndpoint(QName serviceQName, String portName) {
		String epr =null;
		String serviceKey = UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart());
		Topology topology = serviceCache.lookupService(serviceKey);
		if (topology==null) {
			topology = lookupEndpointInUDDI(serviceKey);
		}
	    if (topology!=null && topology.getEprs().size() > 0) {
	    	epr = selectionPolicy.select(topology);
	    }
		return epr;
	}
	
	private Topology lookupEndpointInUDDI(String serviceKey) {
		Topology topology = null;
		try {
			
			BusinessService service = clerk.findService(serviceKey);
			if (service==null) {
				log.debug("No Service with key " + serviceKey + " was found in the registry.");
				//TODO find service by tModel
			}
			if (service!=null && service.getBindingTemplates()!=null && service.getBindingTemplates().getBindingTemplate() != null) 
			{
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
		    
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return topology;
	}
	
	
	
}
