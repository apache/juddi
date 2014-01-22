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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.crypto.dsig.CanonicalizationMethod;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.v3.client.cryptor.CryptorFactory;
import org.apache.juddi.v3.client.cryptor.DigSigUtil;

/**
 * Handles the client configuration of the uddi-client. By default it first
 * looks at system properties. Then loads from the config file from the system property
 * "uddi.client.xml", next the user specified file, finally, "META-INF/uddi.xml"
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ClientConfig 
{
	public final static String UDDI_CONFIG_FILENAME_PROPERTY = "uddi.client.xml";
	public final static String DEFAULT_UDDI_CONFIG = "META-INF/uddi.xml";
	private Log log = LogFactory.getLog(ClientConfig.class);
	private Configuration config = null;;
	private Map<String,UDDINode> uddiNodes = null;
	private Map<String,UDDIClerk> uddiClerks = null;
	private Set<XRegistration> xBusinessRegistrations = null;
	private Set<XRegistration> xServiceBindingRegistrations = null;
	private String clientName = null;
	private String clientCallbackUrl = null;
	private String configurationFile=null;
	
	/**
	 * Constructor (note Singleton pattern).
	 * @throws ConfigurationException
	 */
	public ClientConfig(String configurationFile) throws ConfigurationException 
	{
                loadConfiguration(configurationFile, null);
	}
	/**
	 * Constructor (note Singleton pattern).
	 * @throws ConfigurationException
	 */
	public ClientConfig(String configurationFile, Properties properties) throws ConfigurationException 
	{
		loadConfiguration(configurationFile, properties);
	}
        
        /**
         * Attempts to save any changes made to the configuration back to disk
         * @throws ConfigurationException 
         */
        public void saveConfig() throws ConfigurationException
        {
            
            XMLConfiguration saveConfiguration = new XMLConfiguration(configurationFile);
            Configuration cc = new CompositeConfiguration(saveConfiguration);
            Iterator<String> keys = this.config.getKeys();
            while (keys.hasNext()){
                String key = keys.next();
                if (key.startsWith("client") || key.startsWith("config"))
                {
                    cc.setProperty(key, config.getProperty(key));
                }
            }
            saveConfiguration.save();
        }
        
	protected void readConfig(Properties properties) throws ConfigurationException {
		uddiNodes = readNodeConfig(config, properties);
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
	private void loadConfiguration(String configurationFile, Properties properties) throws ConfigurationException {
		//Properties from system properties
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new SystemConfiguration());
		//Properties from XML file
                if (System.getProperty(UDDI_CONFIG_FILENAME_PROPERTY)!=null){
                        log.info("Using system property config override");
                        configurationFile = System.getProperty(UDDI_CONFIG_FILENAME_PROPERTY);
                }
		XMLConfiguration xmlConfig = null;
		if (configurationFile!=null) {
			xmlConfig = new XMLConfiguration(configurationFile);
		} else {
			final String filename = System.getProperty(UDDI_CONFIG_FILENAME_PROPERTY);
			if (filename != null) {
				xmlConfig = new XMLConfiguration(filename);
			} else { 
				xmlConfig = new XMLConfiguration(DEFAULT_UDDI_CONFIG);	
			}
		}
		log.info("Reading UDDI Client properties file " + xmlConfig.getBasePath() + " use -D"+UDDI_CONFIG_FILENAME_PROPERTY+ " to override");
		this.configurationFile = xmlConfig.getBasePath();
		long refreshDelay = xmlConfig.getLong(Property.UDDI_RELOAD_DELAY, 1000l);
		log.debug("Setting refreshDelay to " + refreshDelay);
		FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
		fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
		xmlConfig.setReloadingStrategy(fileChangedReloadingStrategy);
		compositeConfig.addConfiguration(xmlConfig);
		//Making the new configuration globally accessible.
		config = compositeConfig;
		readConfig(properties);
	}

	private Map<String,UDDIClerk> readClerkConfig(Configuration config, Map<String,UDDINode> uddiNodes) 
	throws ConfigurationException {
		clientName = config.getString("client[@name]");
		clientCallbackUrl = config.getString("client[@callbackUrl]");
		Map<String,UDDIClerk> clerks = new HashMap<String,UDDIClerk>();
		if (config.containsKey("client.clerks.clerk[@name]")) {
			String[] names = config.getStringArray("client.clerks.clerk[@name]");
			
			log.debug("clerk names=" + names.length);
			for (int i=0; i<names.length; i++) {
				UDDIClerk uddiClerk = new UDDIClerk();
				uddiClerk.setManagerName(clientName);
				uddiClerk.setName(     config.getString("client.clerks.clerk(" + i + ")[@name]"));
				String nodeRef = config.getString("client.clerks.clerk(" + i + ")[@node]");
				if (!uddiNodes.containsKey(nodeRef)) throw new ConfigurationException("Could not find Node with name=" + nodeRef);
				UDDINode uddiNode = uddiNodes.get(nodeRef);
				uddiClerk.setUDDINode(uddiNode);
				uddiClerk.setPublisher(config.getString("client.clerks.clerk(" + i + ")[@publisher]"));
				uddiClerk.setPassword( config.getString("client.clerks.clerk(" + i + ")[@password]"));
                                uddiClerk.setIsPasswordEncrypted(config.getBoolean("client.clerks.clerk(" + i + ")[@isPasswordEncrypted]",false) );
                                uddiClerk.setCryptoProvider(config.getString("client.clerks.clerk(" + i + ")[@cryptoProvider]"));
				
				String clerkBusinessKey = config.getString("client.clerks.clerk(" + i + ")[@businessKey]");
				String clerkBusinessName = config.getString("client.clerks.clerk(" + i + ")[@businessName]");
				String clerkKeyDomain = config.getString("client.clerks.clerk(" + i + ")[@keyDomain]");
				
				String[] classes = config.getStringArray("client.clerks.clerk(" + i + ").class");
				uddiClerk.setClassWithAnnotations(classes);
				
				int numberOfWslds = config.getStringArray("client.clerks.clerk(" + i + ").wsdl").length;
				if (numberOfWslds > 0) {
					UDDIClerk.WSDL[] wsdls = new UDDIClerk.WSDL[numberOfWslds];
					for (int w=0; w<wsdls.length; w++) {
						UDDIClerk.WSDL wsdl = uddiClerk.new WSDL();
						String fileName = config.getString("client.clerks.clerk(" + i + ").wsdl(" + w + ")");
						wsdl.setFileName(fileName);
						String businessKey = config.getString("client.clerks.clerk(" + i + ").wsdl(" + w + ")[@businessKey]");
						String businessName = config.getString("client.clerks.clerk(" + i + ").wsdl(" + w + ")[@businessName]");
						String keyDomain = config.getString("client.clerks.clerk(" + i + ").wsdl(" + w + ")[@keyDomain]");
						if (businessKey==null) businessKey = clerkBusinessKey;
						if (businessKey==null) businessKey = uddiClerk.getUDDINode().getProperties().getProperty("businessKey");
						if (businessKey==null) {
							//use key convention to build the businessKey
							if (businessName==null) businessName = clerkBusinessName;
							if (keyDomain==null) keyDomain = clerkKeyDomain;
							if (keyDomain==null) keyDomain = uddiClerk.getUDDINode().getProperties().getProperty("keyDomain");
							if ((businessName==null && ! uddiClerk.getUDDINode().getProperties().containsKey("businessName"))
								|| keyDomain==null && ! uddiClerk.getUDDINode().getProperties().containsKey("keyDomain")) throw new ConfigurationException("Either the wsdl(" + wsdls[w] 
									+ ") or clerk (" + uddiClerk.name + ") elements require a businessKey, or businessName & keyDomain attributes");
							else {
								Properties properties = new Properties(uddiClerk.getUDDINode().getProperties());
								if (businessName!=null) properties.put("businessName", businessName);
								if (keyDomain!=null) properties.put("keyDomain", keyDomain);
								businessKey = UDDIKeyConvention.getBusinessKey(properties);
							}
						}
						if (! businessKey.toLowerCase().startsWith("uddi:") || ! businessKey.substring(5).contains(":")) {
							throw new ConfigurationException("The businessKey " + businessKey + " does not implement a valid UDDI v3 key format.");
						}
						wsdl.setBusinessKey(businessKey);
						if (keyDomain==null) {
							keyDomain = businessKey.split(":")[1];
						}
						wsdl.setKeyDomain(keyDomain);
						wsdls[w]=wsdl;
					}
					uddiClerk.setWsdls(wsdls);
				}
				
				clerks.put(names[i],uddiClerk);
			}
		}
		return clerks;
	}
	
        /**
         * signals that the specified classes/wsdls are registered with the UDDI server
         * when UDDIClient.start() is called
         * client.clerks[@registerOnStartup]
         * @return true/false
         */
	public boolean isRegisterOnStartup() {
		boolean isRegisterOnStartup = false;
		if (config.containsKey("client.clerks[@registerOnStartup]")) {
			isRegisterOnStartup = config.getBoolean("client.clerks[@registerOnStartup]");
		}
		return isRegisterOnStartup;
	}

	private Map<String,UDDINode> readNodeConfig(Configuration config, Properties properties) 
	throws ConfigurationException {
		String[] names = config.getStringArray("client.nodes.node.name");
		Map<String,UDDINode> nodes = new HashMap<String,UDDINode>();
		log.debug("node names=" + names.length);
		for (int i=0; i<names.length; i++) {
			UDDINode uddiNode = new UDDINode();
			String nodeName = config.getString("client.nodes.node(" + i +").name");
			String[] propertyKeys = config.getStringArray("client.nodes.node(" + i +").properties.property[@name]");
			
			if (propertyKeys!=null && propertyKeys.length>0) {
				if (properties==null) properties = new Properties();
				for (int p=0; p<propertyKeys.length; p++) {
					String name=config.getString("client.nodes.node(" + i +").properties.property(" + p + ")[@name]");
					String value=config.getString("client.nodes.node(" + i +").properties.property(" + p + ")[@value]");
					log.debug("Property: name=" + name + " value=" + value);
					properties.put(name, value);
				}
				uddiNode.setProperties(properties);
			}

			uddiNode.setHomeJUDDI(              config.getBoolean("client.nodes.node(" + i +")[@isHomeJUDDI]",false));
			uddiNode.setName(                   TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").name"),properties));
			uddiNode.setClientName(             TokenResolver.replaceTokens(config.getString("client[@name]"),properties));
			uddiNode.setDescription(            TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").description"),properties));
			uddiNode.setProxyTransport(         TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").proxyTransport"),properties));
			uddiNode.setInquiryUrl(             TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").inquiryUrl"),properties));
                        uddiNode.setInquiryRESTUrl(             TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").inquiryRESTUrl"),properties));
			uddiNode.setPublishUrl(             TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").publishUrl"),properties));
			uddiNode.setCustodyTransferUrl(     TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").custodyTransferUrl"),properties));
			uddiNode.setSecurityUrl(            TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").securityUrl"),properties));
			uddiNode.setSubscriptionUrl(        TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").subscriptionUrl"),properties));
			uddiNode.setSubscriptionListenerUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").subscriptionListenerUrl"),properties));
			uddiNode.setJuddiApiUrl(            TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").juddiApiUrl"),properties));
			uddiNode.setFactoryInitial(         TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").javaNamingFactoryInitial"),properties));
			uddiNode.setFactoryURLPkgs(         TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").javaNamingFactoryUrlPkgs"),properties));
			uddiNode.setFactoryNamingProvider(  TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").javaNamingProviderUrl"),properties));
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
		String[] entityKeys = config.getStringArray("client.clerks.xregister." + entityType + "[@entityKey]");
		Set<XRegistration> xRegistrations = new HashSet<XRegistration>();
		if (entityKeys.length > 0) log.info("XRegistration " + entityKeys.length + " " + entityType + "Keys");
		for (int i=0; i<entityKeys.length; i++) {
			XRegistration xRegistration = new XRegistration();
			xRegistration.setEntityKey(config.getString("client.clerks.xregister." + entityType + "(" + i + ")[@entityKey]"));
			
			String fromClerkRef = config.getString("client.clerks.xregister." + entityType + "(" + i + ")[@fromClerk]");
			if (!clerks.containsKey(fromClerkRef)) throw new ConfigurationException("Could not find fromClerk with name=" + fromClerkRef);
			UDDIClerk fromClerk = clerks.get(fromClerkRef);
			xRegistration.setFromClerk(fromClerk);
			
			String toClerkRef = config.getString("client.clerks.xregister." + entityType + "(" + i + ")[@toClerk]");
			if (!clerks.containsKey(toClerkRef)) throw new ConfigurationException("Could not find toClerk with name=" + toClerkRef);
			UDDIClerk toClerk = clerks.get(toClerkRef);
			xRegistration.setToClerk(toClerk);
			log.debug(xRegistration);
			
			xRegistrations.add(xRegistration);
		}
		return xRegistrations;
	}
	
	protected Map<String, UDDINode> getUDDINodes() {
		return uddiNodes;
	}

        /**
         * gets the current configuration's node list
         * only the node name, client name, descriptions and transport class are returned, everything else is nulled out for
         * security reasons. Only a copy of these values are returned
         * @return 
         */
        public List<Node> getUDDINodeList(){
                List<Node> ret = new ArrayList<Node>();
                Iterator<UDDINode> it = uddiNodes.values().iterator();
                while (it.hasNext()){
                        UDDINode next = it.next();
                        Node n = new Node();
                        n.setClientName(next.getClientName());
                        n.setDescription(next.getDescription());
                        n.setName(next.getName());
                        n.setProxyTransport(next.getProxyTransport());
                        ret.add(n);
                }
                return ret;
        }
	
	public UDDINode getHomeNode() throws ConfigurationException {
		if (uddiNodes==null) throw new ConfigurationException("The juddi client configuration " +
				"must contain at least one node element.");
		if (uddiNodes.values().size()==1) return uddiNodes.values().iterator().next();
		for (UDDINode uddiNode : uddiNodes.values()) {
			if (uddiNode.isHomeJUDDI()) {
				return uddiNode;
			}
		}
		throw new ConfigurationException("One of the node elements in the client configuration needs to a 'isHomeJUDDI=\"true\"' attribute.");
	}
	
	public UDDINode getUDDINode(String nodeName) throws ConfigurationException {
		if (! uddiNodes.containsKey(nodeName)) {
			throw new ConfigurationException("Node '" + nodeName 
					+ "' cannot be found in the config '"+  getClientName() + "'" );
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

        public String getClientName() {
            return clientName;
        }
        
        @Deprecated
        public String getClientCallbackUrl() {
        	return clientCallbackUrl;
        }

        
        public String getConfigurationFile() {
            return configurationFile;
        }
        
        /**
         * Used for WADL/WSDL to WSDL
         * @return true/false
         */
        public boolean isX_To_Wsdl_Ignore_SSL_Errors()
        {
            return this.config.getBoolean("client.XtoWsdl.IgnoreSSLErrors", false);
        }

        
        
        /**
         * Fetches all digital signature related properties for the digital signature utility.
         * warning, this will decrypt all passwords
         * @return a properties object
         * @throws Exception 
         * @see DigSigUtil
         * @see Properties
         */
    public Properties getDigitalSignatureConfiguration() throws Exception {
        Properties p = new Properties();
        p.setProperty(DigSigUtil.CANONICALIZATIONMETHOD, this.config.getString("client.signature.canonicalizationMethod", CanonicalizationMethod.EXCLUSIVE));
        p.setProperty(DigSigUtil.CHECK_TIMESTAMPS, ((Boolean) (this.config.getBoolean("client.signature.checkTimestamps", true))).toString());
        p.setProperty(DigSigUtil.CHECK_REVOCATION_STATUS_CRL, ((Boolean) (this.config.getBoolean("client.signature.checkRevocationCRL", true))).toString());
        p.setProperty(DigSigUtil.CHECK_REVOCATION_STATUS_OCSP, ((Boolean) (this.config.getBoolean("client.signature.checkRevocationOCSP", true))).toString());
        p.setProperty(DigSigUtil.CHECK_TRUST_CHAIN, ((Boolean) (this.config.getBoolean("client.signature.checkTrust", true))).toString());

        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE, this.config.getString("client.signature.signingKeyStorePath",""));
        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, this.config.getString("client.signature.signingKeyStoreType",""));
    
        if (this.config.getBoolean("client.signature.signingKeyPassword[@isPasswordEncrypted]", false)) {
            String enc = this.config.getString("client.signature.signingKeyPassword","");
            String prov = this.config.getString("client.signature.signingKeyPassword[@cryptoProvider]","");
            p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD, CryptorFactory.getCryptor(prov).decrypt(enc));
        } else {
            log.warn("Hey, you should consider encrypting your key password!");
            p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD, this.config.getString("client.signature.signingKeyPassword",""));
        }
        if (this.config.getBoolean("client.signature.signingKeyStoreFilePassword[@isPasswordEncrypted]", false)) {
            String enc = this.config.getString("client.signature.signingKeyStoreFilePassword","");
            String prov = this.config.getString("client.signature.signingKeyStoreFilePassword[@cryptoProvider]","");
            p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, CryptorFactory.getCryptor(prov).decrypt(enc));
        } else {
            log.warn("Hey, you should consider encrypting your keystore password!");
            p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, this.config.getString("client.signature.signingKeyStoreFilePassword",""));
        }

        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, this.config.getString("client.signature.signingKeyAlias",""));
        p.setProperty(DigSigUtil.SIGNATURE_METHOD, this.config.getString("client.signature.signatureMethod", "http://www.w3.org/2000/09/xmldsig#rsa-sha1"));
        p.setProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, this.config.getString("client.signature.keyInfoInclusionSubjectDN","true"));
        p.setProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, this.config.getString("client.signature.keyInfoInclusionBase64PublicKey","true"));
        p.setProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, this.config.getString("client.signature.keyInfoInclusionSerial","true"));
        
        p.setProperty(DigSigUtil.SIGNATURE_OPTION_DIGEST_METHOD, this.config.getString("client.signature.digestMethod", "http://www.w3.org/2000/09/xmldsig#sha1"));
        
        p.setProperty(DigSigUtil.TRUSTSTORE_FILE, this.config.getString("client.signature.trustStorePath",""));
        p.setProperty(DigSigUtil.TRUSTSTORE_FILETYPE, this.config.getString("client.signature.trustStoreType",""));
      

        if (this.config.getBoolean("client.signature.trustStorePassword[@isPasswordEncrypted]", false)) {
            String enc = this.config.getString("client.signature.trustStorePassword","");
            String prov = this.config.getString("client.signature.trustStorePassword[@cryptoProvider]","");
            p.setProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, CryptorFactory.getCryptor(prov).decrypt(enc));
        } else {
            log.warn("Hey, you should consider encrypting your trust store password!");
            p.setProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, this.config.getString("client.signature.trustStorePassword",""));
        }

        return p;
    }
}