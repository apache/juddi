/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

/**
 * Handles the client configuration of the uddi-client. By default it first
 * looks at system properties.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ClientConfig 
{
	private final static String UDDI_CONFIG_FILENAME_PROPERTY = "uddi.client.xml";
	public final static String UDDI_CONFIG = "META-INF/uddi.xml";
	private Logger log = Logger.getLogger(ClientConfig.class);
	private Configuration config = null;;
	private Map<String,UDDINode> uddiNodes = null;
	private Map<String,UDDIClerk> uddiClerks = null;
	private Set<XRegistration> xBusinessRegistrations = null;
	private Set<XRegistration> xServiceBindingRegistrations = null;
	private String managerName = null;
	
	/**
	 * Constructor (note Singleton pattern).
	 * @throws ConfigurationException
	 */
	public ClientConfig(String configurationFile) throws ConfigurationException 
	{
		loadConfiguration(configurationFile);
	}
	protected void loadManager() throws ConfigurationException {
		uddiNodes = readNodeConfig(config);
		uddiClerks = readClerkConfig(config, uddiNodes);
		xServiceBindingRegistrations = readXServiceBindingRegConfig(config,uddiClerks);
		xBusinessRegistrations = readXBusinessRegConfig(config, uddiClerks);
	}
	/**
	 * Does the actual work of reading the configuration from System
	 * Properties and/or uddi.xml file. When the uddi.xml
	 * file is updated the file will be reloaded. By default the reloadDelay is
	 * set to 1 second to prevent excessive date stamp checking.
	 */
	private void loadConfiguration(String configurationFile) throws ConfigurationException {
		//Properties from system properties
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new SystemConfiguration());
		//Properties from XML file
		XMLConfiguration xmlConfig = null;
		if (configurationFile!=null) {
			xmlConfig = new XMLConfiguration(configurationFile);
		} else {
			final String filename = System.getProperty(UDDI_CONFIG_FILENAME_PROPERTY);
			if (filename != null) 
			{
				xmlConfig = new XMLConfiguration(filename);
			} else { 
				xmlConfig = new XMLConfiguration(UDDI_CONFIG);	
			}
		}
		log.info("Reading UDDI Client properties file " + xmlConfig.getBasePath());
		long refreshDelay = xmlConfig.getLong(Property.UDDI_RELOAD_DELAY, 1000l);
		log.debug("Setting refreshDelay to " + refreshDelay);
		FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
		fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
		xmlConfig.setReloadingStrategy(fileChangedReloadingStrategy);
		compositeConfig.addConfiguration(xmlConfig);
		//Making the new configuration globally accessible.
		config = compositeConfig;
		loadManager();
	}

	private Map<String,UDDIClerk> readClerkConfig(Configuration config, Map<String,UDDINode> uddiNodes) 
	throws ConfigurationException {
		managerName = config.getString("manager[@name]");
		Map<String,UDDIClerk> clerks = new HashMap<String,UDDIClerk>();
		if (config.containsKey("manager.clerks.clerk[@name]")) {
			String[] names = config.getStringArray("manager.clerks.clerk[@name]");
			
			log.debug("clerk names=" + names);
			for (int i=0; i<names.length; i++) {
				UDDIClerk uddiClerk = new UDDIClerk();
				uddiClerk.setManagerName(managerName);
				uddiClerk.setName(     config.getString("manager.clerks.clerk(" + i + ")[@name]"));
				String nodeRef = config.getString("manager.clerks.clerk(" + i + ")[@node]");
				if (!uddiNodes.containsKey(nodeRef)) throw new ConfigurationException("Could not find Node with name=" + nodeRef);
				UDDINode uddiNode = uddiNodes.get(nodeRef);
				uddiClerk.setUDDINode(uddiNode);
				uddiClerk.setPublisher(config.getString("manager.clerks.clerk(" + i + ")[@publisher]"));
				uddiClerk.setPassword( config.getString("manager.clerks.clerk(" + i + ")[@password]"));
				String[] classes = config.getStringArray("manager.clerks.clerk(" + i + ").class");
				uddiClerk.setClassWithAnnotations(classes);
				clerks.put(names[i],uddiClerk);
			}
		}
		return clerks;
	}
	
	public boolean isRegisterOnStartup() {
		boolean isRegisterOnStartup = false;
		if (config.containsKey("manager.clerks[@registerOnStartup]")) {
			isRegisterOnStartup = config.getBoolean("manager.clerks[@registerOnStartup]");
		}
		return isRegisterOnStartup;
	}

	private Map<String,UDDINode> readNodeConfig(Configuration config) 
	throws ConfigurationException {
		String[] names = config.getStringArray("manager.nodes.node.name");
		Map<String,UDDINode> nodes = new HashMap<String,UDDINode>();
		log.debug("node names=" + names);
		for (int i=0; i<names.length; i++) {
			UDDINode uddiNode = new UDDINode();
			String nodeName = config.getString("manager.nodes.node(" + i +").name");
			String[] propertyKeys = config.getStringArray("manager.nodes.node(" + i +").properties.property[@name]");
			Properties properties = null;
			if (propertyKeys!=null && propertyKeys.length>0) {
				properties = new Properties();
				for (int p=0; p<propertyKeys.length; p++) {
					String name=config.getString("manager.nodes.node(" + i +").properties.property(" + p + ")[@name]");
					String value=config.getString("manager.nodes.node(" + i +").properties.property(" + p + ")[@value]");
					log.debug("Property: name=" + name + " value=" + value);
					properties.put(name, value);
				}
				uddiNode.setProperties(properties);
			}
			uddiNode.setAllowJUDDIAPI(          config.getBoolean("manager.nodes.node(" + i +")[@allowJUDDIAPI]",true));
			uddiNode.setName(                   config.getString("manager.nodes.node(" + i +").name"));
			uddiNode.setManagerName(            config.getString("manager[@name]"));
			uddiNode.setDescription(            config.getString("manager.nodes.node(" + i +").description"));
			uddiNode.setProxyTransport(         config.getString("manager.nodes.node(" + i +").proxyTransport"));
			uddiNode.setInquiryUrl(             TokenResolver.replaceTokens(config.getString("manager.nodes.node(" + i +").inquiryUrl"),properties));
			uddiNode.setPublishUrl(             TokenResolver.replaceTokens(config.getString("manager.nodes.node(" + i +").publishUrl"),properties));
			uddiNode.setCustodyTransferUrl(     TokenResolver.replaceTokens(config.getString("manager.nodes.node(" + i +").custodyTransferUrl"),properties));
			uddiNode.setSecurityUrl(            TokenResolver.replaceTokens(config.getString("manager.nodes.node(" + i +").securityUrl"),properties));
			uddiNode.setSubscriptionUrl(        TokenResolver.replaceTokens(config.getString("manager.nodes.node(" + i +").subscriptionUrl"),properties));
			uddiNode.setSubscriptionListenerUrl(TokenResolver.replaceTokens(config.getString("manager.nodes.node(" + i +").subscriptionListenerUrl"),properties));
			uddiNode.setJuddiApiUrl(            TokenResolver.replaceTokens(config.getString("manager.nodes.node(" + i +").juddiApiUrl"),properties));
			uddiNode.setFactoryInitial(         config.getString("manager.nodes.node(" + i +").javaNamingFactoryInitial"));
			uddiNode.setFactoryURLPkgs(         config.getString("manager.nodes.node(" + i +").javaNamingFactoryUrlPkgs"));
			uddiNode.setFactoryNamingProvider(  TokenResolver.replaceTokens(config.getString("manager.nodes.node(" + i +").javaNamingProviderUrl"),properties));
			nodes.put(nodeName,uddiNode);
		}
		return nodes;
	}
	
	private Set<XRegistration> readXBusinessRegConfig(Configuration config, Map<String,UDDIClerk> clerks) 
	throws ConfigurationException {
		return readXRegConfig(config, clerks, "business");
	}
	
	private Set<XRegistration> readXServiceBindingRegConfig(Configuration config, Map<String,UDDIClerk> clerks) 
	throws ConfigurationException {
		return readXRegConfig(config, clerks, "servicebinding");
	}
	
	private Set<XRegistration> readXRegConfig(Configuration config, Map<String,UDDIClerk> clerks, String entityType) 
	throws ConfigurationException {
		String[] entityKeys = config.getStringArray("manager.clerks.xregister." + entityType + "[@entityKey]");
		Set<XRegistration> xRegistrations = new HashSet<XRegistration>();
		log.info("XRegistration " + entityKeys.length + " " + entityType + "Keys");
		for (int i=0; i<entityKeys.length; i++) {
			XRegistration xRegistration = new XRegistration();
			xRegistration.setEntityKey(config.getString("manager.clerks.xregister." + entityType + "(" + i + ")[@entityKey]"));
			
			String fromClerkRef = config.getString("manager.clerks.xregister." + entityType + "(" + i + ")[@fromClerk]");
			if (!clerks.containsKey(fromClerkRef)) throw new ConfigurationException("Could not find fromClerk with name=" + fromClerkRef);
			UDDIClerk fromClerk = clerks.get(fromClerkRef);
			xRegistration.setFromClerk(fromClerk);
			
			String toClerkRef = config.getString("manager.clerks.xregister." + entityType + "(" + i + ")[@toClerk]");
			if (!clerks.containsKey(toClerkRef)) throw new ConfigurationException("Could not find toClerk with name=" + toClerkRef);
			UDDIClerk toClerk = clerks.get(toClerkRef);
			xRegistration.setToClerk(toClerk);
			log.info(xRegistration);
			
			xRegistrations.add(xRegistration);
		}
		return xRegistrations;
	}
	
	protected Map<String, UDDINode> getUDDINodes() {
		return uddiNodes;
	}
	
	public UDDINode getUDDINode(String nodeName) throws ConfigurationException {
		if (! uddiNodes.containsKey(nodeName)) {
			throw new ConfigurationException("Node '" + nodeName 
					+ "' cannot be found in the config '"+  getManagerName() + "'" );
		}
		return uddiNodes.get(nodeName);
	}
	
	public Map<String,UDDIClerk> getUDDIClerks() {
		return uddiClerks;
	}
	
	public Set<XRegistration> getXServiceBindingRegistrations() {
		return xServiceBindingRegistrations;
	}
	
	public Set<XRegistration> getXBusinessRegistrations() {
		return xBusinessRegistrations;
	}
    
    public Configuration getConfiguration() {
    	return config;
    }
    
    public String getManagerName() {
		return managerName;
	}
}
