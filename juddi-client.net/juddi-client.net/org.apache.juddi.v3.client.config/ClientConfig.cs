using org.apache.juddi.v3.client.log;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Configuration;
using System.Text;

namespace org.apache.juddi.v3.client.config
{
public    class ClientConfig
    {
        	private readonly static String UDDI_CONFIG_FILENAME_PROPERTY = "uddi.client.xml";
	public readonly static String DEFAULT_UDDI_CONFIG = "META-INF/uddi.xml";
	private Log log = LogFactory.getLog(typeof(ClientConfig));
	//private Configuration config = null;
	private Dictionary<String,UDDINode> uddiNodes = null;
	private Dictionary<String,UDDIClerk> uddiClerks = null;
	private HashSet<XRegistration> xBusinessRegistrations = null;
	private HashSet<XRegistration> xServiceBindingRegistrations = null;
	private String clientName = null;
	private String configurationFile=null;
	
	/**
	 * Constructor (note Singleton pattern).
	 * @
	 */
	public ClientConfig(String configurationFile) 
	{
		loadConfiguration(configurationFile, null);
	}
	/**
	 * Constructor (note Singleton pattern).
	 * @
	 */
	public ClientConfig(String configurationFile, Properties properties) 
	{
		loadConfiguration(configurationFile, properties);
	}
	protected void readConfig(Properties properties)  {
		/*uddiNodes = readNodeConfig(config, properties);
		uddiClerks = readClerkConfig(config, uddiNodes);
		xServiceBindingRegistrations = readXServiceBindingRegConfig(config,uddiClerks);
		xBusinessRegistrations = readXBusinessRegConfig(config, uddiClerks);*/
	}
	/**
	 * Does the actual work of reading the configuration from System
	 * Properties and/or uddi.xml file. When the uddi.xml
	 * file is updated the file will be reloaded. By default the reloadDelay is
	 * set to 1 second to prevent excessive date stamp checking.
	 */
	private void loadConfiguration(String configurationFile, Properties properties)  {
        /*
		//Properties from system properties
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new SystemConfiguration());
		//Properties from XML file
		XMLConfiguration xmlConfig = null;
		if (configurationFile!=null) {
			xmlConfig = new XMLConfiguration(configurationFile);
		} else {
			 String filename = System.getProperty(UDDI_CONFIG_FILENAME_PROPERTY);
			if (filename != null) {
				xmlConfig = new XMLConfiguration(filename);
			} else { 
				xmlConfig = new XMLConfiguration(DEFAULT_UDDI_CONFIG);	
			}
		}
		log.info("Reading UDDI Client properties file " + xmlConfig.getBasePath());
		this.configurationFile = xmlConfig.getBasePath();
		long refreshDelay = xmlConfig.getLong(Property.UDDI_RELOAD_DELAY, 1000L);
		log.debug("Setting refreshDelay to " + refreshDelay);
		FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
		fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
		xmlConfig.setReloadingStrategy(fileChangedReloadingStrategy);
		compositeConfig.addConfiguration(xmlConfig);
		//Making the new configuration globally accessible.
		config = compositeConfig;
		readConfig(properties);*/
	}

    /*private Dictionary<String,UDDIClerk> readClerkConfig(Configuration config, Dictionary<String,UDDINode> uddiNodes) 
   {
      
      clientName = config.getString("client[@name]");
      Dictionary<String,UDDIClerk> clerks = new Dictionary<String,UDDIClerk>();
      if (config.containsKey("client.clerks.clerk[@name]")) {
          String[] names = config.getStringArray("client.clerks.clerk[@name]");
			
          log.debug("clerk names=" + names);
          for (int i=0; i<names.length; i++) {
              UDDIClerk uddiClerk = new UDDIClerk();
              uddiClerk.setManagerName(clientName);
              uddiClerk.setName(     config.getString("client.clerks.clerk(" + i + ")[@name]"));
              String nodeRef = config.getString("client.clerks.clerk(" + i + ")[@node]");
              if (!uddiNodes.containsKey(nodeRef)) throw new ConfigurationException("Could not find Node with name=" + nodeRef);
              UDDINode uddiNode = uddiNodes[nodeRef];
              uddiClerk.setUDDInode(uddiNode);
              uddiClerk.setPublisher(config.getString("client.clerks.clerk(" + i + ")[@publisher]"));
              uddiClerk.setPassword( config.getString("client.clerks.clerk(" + i + ")[@password]"));
				
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
         
  }*/

    public bool isRegisterOnStartup() {
		/*bool isRegisterOnStartup = false;
		if (config.containsKey("client.clerks[@registerOnStartup]")) {
			isRegisterOnStartup = config.getbool("client.clerks[@registerOnStartup]");
		}
		return isRegisterOnStartup;*/
        return false;
	}

    //private Dictionary<String, UDDINode> readNodeConfig(Configuration config, Properties properties) 
	 //{
        /*
		String[] names = config.getStringArray("client.nodes.node.name");
        Dictionary<String, UDDINode> nodes = new Dictionary<String, UDDINode>();
		log.debug("node names=" + names);
		for (int i=0; i<names.Length; i++) {
			UDDINode uddiNode = new UDDINode();
			String nodeName = config.getString("client.nodes.node(" + i +").name");
			String[] propertyKeys = config.getStringArray("client.nodes.node(" + i +").properties.property[@name]");
			
			if (propertyKeys!=null && propertyKeys.Length>0) {
				if (properties==null) properties = new Properties();
				for (int p=0; p<propertyKeys.Length; p++) {
					String name=config.getString("client.nodes.node(" + i +").properties.property(" + p + ")[@name]");
					String value=config.getString("client.nodes.node(" + i +").properties.property(" + p + ")[@value]");
					log.debug("Property: name=" + name + " value=" + value);
					properties.put(name, value);
				}
				uddiNode.setProperties(properties);
			}
			uddiNode.setHomeJUDDI(              config.getbool("client.nodes.node(" + i +")[@isHomeJUDDI]",false));
			uddiNode.setName(                   TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").name"),properties));
			uddiNode.setClientName(             TokenResolver.replaceTokens(config.getString("client[@name]"),properties));
			uddiNode.setDescription(            TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").description"),properties));
			uddiNode.setProxyTransport(         TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").proxyTransport"),properties));
			uddiNode.setInquiryUrl(             TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i +").inquiryUrl"),properties));
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
         * */
      
	//}

    /*
    private HashSet<XRegistration> readXBusinessRegConfig(Configuration config, Dictionary<String, UDDIClerk> clerks) 
	 {
		return readXRegConfig(config, clerks, "business");
	}

    private HashSet<XRegistration> readXServiceBindingRegConfig(Configuration config, Dictionary<String, UDDIClerk> clerks) 
	 {
		return readXRegConfig(config, clerks, "servicebinding");
	}

    private HashSet<XRegistration> readXRegConfig(Configuration config, Dictionary<String, UDDIClerk> clerks, String entityType) 
	 {
		String[] entityKeys = config.getStringArray("client.clerks.xregister." + entityType + "[@entityKey]");
        HashSet<XRegistration> xRegistrations = new HashSet<XRegistration>();
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
    */
    public Dictionary<String, UDDINode> getUDDINodes()
    {
		return uddiNodes;
	}
	
	public UDDINode getHomeNode()  {
		if (uddiNodes==null) throw new ConfigurationException("The juddi client configuration " +
				"must contain at least one node element.");
        if (uddiNodes.Values.Count == 1) {
          IEnumerator it=  uddiNodes.Values.GetEnumerator();
          it.MoveNext();
          return it.Current as UDDINode;
        }
		foreach (UDDINode uddiNode in uddiNodes.Values) {
			if (uddiNode.isHomeJUDDI()) {
				return uddiNode;
			}
		}
		throw new ConfigurationException("One of the node elements in the client configuration needs to a 'isHomeJUDDI=\"true\"' attribute.");
	}
	
	public UDDINode getUDDINode(String nodeName)  {
		if (! uddiNodes.ContainsKey(nodeName)) {
			throw new ConfigurationException("Node '" + nodeName 
					+ "' cannot be found in the config '"+  getClientName() + "'" );
		}
		return uddiNodes[nodeName];
	}

    public Dictionary<String, UDDIClerk> getUDDIClerks()
    {
		return uddiClerks;
	}
	
	public HashSet<XRegistration> getXServiceBindingRegistrations() {
		return xServiceBindingRegistrations;
	}

    public HashSet<XRegistration> getXBusinessRegistrations()
    {
		return xBusinessRegistrations;
	}
    
   /* public Configuration getConfiguration() {
    	return config;
    }*/
    
    public String getClientName() {
		return clientName;
	}
    
    public String getConfigurationFile() {
    	return configurationFile;
    }
    }
}
