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
	private final static String UDDI_CONFIG = "META-INF/uddi.xml";
	private static Logger log = Logger.getLogger(ClientConfig.class);
	private Configuration config;
	private static ClientConfig instance=null;
	private Map<String,UDDINode> nodes = null;
	private Set<UDDIClerk> clerks = null;
	
	/**
	 * Constructor (note Singleton pattern).
	 * @throws ConfigurationException
	 */
	private ClientConfig() throws ConfigurationException 
	{
		loadConfiguration();
	}
	/**
	 * Does the actual work of reading the configuration from System
	 * Properties and/or juddiv3.properties file. When the juddiv3.properties
	 * file is updated the file will be reloaded. By default the reloadDelay is
	 * set to 1 second to prevent excessive date stamp checking.
	 */
	private synchronized void loadConfiguration() throws ConfigurationException {
		//Properties from system properties
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new SystemConfiguration());
		//Properties from XML file
		XMLConfiguration xmlConfig = new XMLConfiguration(UDDI_CONFIG);
		long refreshDelay = xmlConfig.getLong(Property.UDDI_RELOAD_DELAY, 1000l);
		log.debug("Setting refreshDelay to " + refreshDelay);
		FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
		fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
		xmlConfig.setReloadingStrategy(fileChangedReloadingStrategy);
		compositeConfig.addConfiguration(xmlConfig);
		//Making the new configuration globally accessible.
		config = compositeConfig;
		nodes = readNodeConfig(config);
		clerks = readClerkConfig(config, nodes);
	}

	/**
	 * Obtains the reference to the Singleton instance.
	 * 
	 * @return the APplicationConfuration Singleton Instance.
	 * @throws ConfigurationException
	 */
	public static ClientConfig getInstance() throws ConfigurationException 
	{
		if (instance==null) {
			instance = new ClientConfig();
		}
		return instance;
	}
	
	public static void init() throws ConfigurationException 
	{
		getInstance();
	}
	/**
	 * Hook to receive configuration reload events from an external application.
	 * 
	 * @throws ConfigurationException
	 */
	public static void reloadConfig() throws ConfigurationException
	{
		getInstance().loadConfiguration();
	}
	/**
	 * The object from which property values can be obtained.
	 * @return the commons Configuration interface
	 * @throws ConfigurationException 
	 */
	public static Configuration getConfiguration() throws ConfigurationException
	{
		return getInstance().config;
	}

	private static Set<UDDIClerk> readClerkConfig(Configuration config, Map<String,UDDINode> nodes) 
	throws ConfigurationException {
		String[] names = config.getStringArray("clerks.clerk[@name]");
		Set<UDDIClerk> clerks = new HashSet<UDDIClerk>();
		log.debug("clerk names=" + names);
		for (int i=0; i<names.length; i++) {
			UDDIClerk uddiClerk = new UDDIClerk();
			uddiClerk.setName(     config.getString("clerks.clerk(" + i + ")[@name]"));
			String nodeRef = config.getString("clerks.clerk(" + i + ")[@node]");
			if (!nodes.containsKey(nodeRef)) throw new ConfigurationException("Could not find Node with name=" + nodeRef);
			UDDINode node = nodes.get(nodeRef);
			uddiClerk.setNode(node);
			uddiClerk.setPublisher(config.getString("clerks.clerk(" + i + ")[@publisher]"));
			uddiClerk.setPassword( config.getString("clerks.clerk(" + i + ")[@password]"));
			String[] classes = config.getStringArray("clerks.clerk(" + i + ").class");
			uddiClerk.setClassWithAnnotations(classes);
			clerks.add(uddiClerk);
		}
		return clerks;
	}

	private static Map<String,UDDINode> readNodeConfig(Configuration config) 
	throws ConfigurationException {
		String[] names = config.getStringArray("nodes.node.name");
		Map<String,UDDINode> nodes = new HashMap<String,UDDINode>();
		log.debug("node names=" + names);
		for (int i=0; i<names.length; i++) {
			UDDINode uddiNode = new UDDINode();
			String name = config.getString("nodes.node(" + i +").name");
			uddiNode.setName(                   config.getString("nodes.node(" + i +").name"));
			uddiNode.setDescription(            config.getString("nodes.node(" + i +").description"));
			uddiNode.setInquiryUrl(             config.getString("nodes.node(" + i +").inquiryUrl"));
			uddiNode.setPublishUrl(             config.getString("nodes.node(" + i +").publishUrl"));
			uddiNode.setCustodyTransferUrl(     config.getString("nodes.node(" + i +").custodyTransferUrl"));
			uddiNode.setSecurityUrl(            config.getString("nodes.node(" + i +").securityUrl"));
			uddiNode.setSubscriptionUrl(        config.getString("nodes.node(" + i +").subscriptionUrl"));
			uddiNode.setSubscriptionListenerUrl(config.getString("nodes.node(" + i +").subscriptionListenerUrl"));
			uddiNode.setJuddiApiUrl(            config.getString("nodes.node(" + i +").juddiApiUrl"));
			uddiNode.setProxyTransport(         config.getString("nodes.node(" + i +").proxyTransport"));
			nodes.put(name,uddiNode);
		}
		if (!nodes.containsKey("default")) {
			throw new ConfigurationException("Required node named 'default' is not found.");
		}
		return nodes;
	}
	
	public Map<String, UDDINode> getNodes() {
		return nodes;
	}
	public Set<UDDIClerk> getClerks() {
		return clerks;
	}
}
