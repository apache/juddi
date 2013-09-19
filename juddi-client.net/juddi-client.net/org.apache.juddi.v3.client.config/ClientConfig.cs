/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
using org.apache.juddi.v3.client.log;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace org.apache.juddi.v3.client.config
{
    /// <summary>
    /// This class handles the loading of configuration file data from disk, typically from either a caller
    /// specified file, "uddi.xml" in the current directory, or from a System Environment variable "uddi.client.xml"
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class ClientConfig
    {
        private readonly static String UDDI_CONFIG_FILENAME_PROPERTY = "uddi.client.xml";
        public readonly static String DEFAULT_UDDI_CONFIG = "uddi.xml";
        private Log log = LogFactory.getLog(typeof(ClientConfig));
        private uddi config = null;
        private Dictionary<String, UDDINode> uddiNodes = null;
        private Dictionary<String, UDDIClerk> uddiClerks = null;
        private HashSet<XRegistration> xBusinessRegistrations = null;
        private HashSet<XRegistration> xServiceBindingRegistrations = null;
        private String clientName = null;
        private String configurationFile = null;

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
        protected void readConfig(Properties properties)
        {

            uddiNodes = readNodeConfig(config, properties);
            uddiClerks = readClerkConfig(config, uddiNodes);
            xServiceBindingRegistrations = readXServiceBindingRegConfig(config, uddiClerks);
            xBusinessRegistrations = readXBusinessRegConfig(config, uddiClerks);
        }
        /**
         * Does the actual work of reading the configuration from System
         * Properties and/or uddi.xml file. When the uddi.xml
         * file is updated the file will be reloaded. By default the reloadDelay is
         * set to 1 second to prevent excessive date stamp checking.
         */
        private void loadConfiguration(String configurationFile, Properties properties)
        {
            if (config != null)
                return;
            String filename = null;




            //Properties from XML file


            if (!String.IsNullOrEmpty(configurationFile))
            {
                filename = configurationFile;
            }
            else
            {
                String prop = System.Environment.GetEnvironmentVariable(ClientConfig.UDDI_CONFIG_FILENAME_PROPERTY);
                if (!String.IsNullOrEmpty(prop))
                    filename = prop;
                else
                    filename = UDDI_CONFIG_FILENAME_PROPERTY;
            }

            log.info("Reading UDDI Client properties file " + filename);
            config = XmlConfiguration.LoadXmlConfiguration(filename);
            this.configurationFile = filename;

            //Properties from system properties

            IDictionaryEnumerator it = Environment.GetEnvironmentVariables().GetEnumerator();
            while (it.MoveNext())
            {
                config.getProperties().setProperty(it.Key.ToString(), it.Value.ToString());
            }
            readConfig(properties);
        }

        public void SaveConfiguration()
        {
            if (this.config == null)
                throw new Exception("Config is not loaded, cannot save");
            XmlConfiguration.SaveXmlConfiguration(this.configurationFile, this.config);
        }

        private Dictionary<String, UDDIClerk> readClerkConfig(uddi config, Dictionary<String, UDDINode> uddiNodes)
        {

            clientName = config.client.name;
            Dictionary<String, UDDIClerk> clerks = new Dictionary<String, UDDIClerk>();
            if (config.client.clerks != null && config.client.clerks.clerk != null && config.client.clerks.clerk.Length > 0)//.ContainsKey("client.clerks.clerk[@name]"))
            {


                log.debug("clerk names=" + config.client.clerks.clerk.Length);
                for (int i = 0; i < config.client.clerks.clerk.Length; i++)
                {
                    UDDIClerk uddiClerk = new UDDIClerk();
                    uddiClerk.setManagerName(clientName);
                    uddiClerk.setName(config.client.clerks.clerk[i].name);
                    String nodeRef = config.client.clerks.clerk[i].node;
                    if (!uddiNodes.ContainsKey(nodeRef)) throw new ConfigurationErrorsException("Could not find Node with name=" + nodeRef);
                    UDDINode uddiNode = uddiNodes[nodeRef];
                    uddiClerk.setUDDInode(uddiNode);
                    uddiClerk.setPublisher(config.client.clerks.clerk[i].publisher);
                    uddiClerk.setPassword(config.client.clerks.clerk[i].password);
                    uddiClerk.setPasswordEncrypted(config.client.clerks.clerk[i].isPasswordEncrypted);
                    uddiClerk.setCryptoProvider(config.client.clerks.clerk[i].cryptoProvider);

                    String clerkBusinessKey = config.client.clerks.clerk[i].businessKey;
                    String clerkBusinessName = config.client.clerks.clerk[i].businessName;
                    String clerkKeyDomain = config.client.clerks.clerk[i].keyDomain;

                    String[] classes = config.client.clerks.clerk[i].@class;
                    uddiClerk.setClassWithAnnotations(classes);

                    int numberOfWslds = 0;
                    if (config.client.clerks.clerk[i].wsdl != null)
                        numberOfWslds = config.client.clerks.clerk[i].wsdl.Length;// config.getStringArray("client.clerks.clerk(" + i + ").wsdl").Length;
                    if (numberOfWslds > 0)
                    {
                        UDDIClerk.WSDL[] wsdls = new UDDIClerk.WSDL[numberOfWslds];
                        for (int w = 0; w < wsdls.Length; w++)
                        {
                            UDDIClerk.WSDL wsdl = new UDDIClerk.WSDL();
                            String fileName = config.client.clerks.clerk[i].wsdl[w].Value;
                            wsdl.setFileName(null);
                            String businessKey = config.client.clerks.clerk[i].wsdl[w].businessKey;
                            String businessName = config.client.clerks.clerk[i].wsdl[w].businessName;
                            String keyDomain = config.client.clerks.clerk[i].wsdl[w].keyDomain;
                            if (businessKey == null) businessKey = clerkBusinessKey;
                            if (businessKey == null) businessKey = uddiClerk.getUDDINode().getProperties().getString("businessKey");
                            if (businessKey == null)
                            {
                                //use key convention to build the businessKey
                                if (businessName == null) businessName = clerkBusinessName;
                                if (keyDomain == null) keyDomain = clerkKeyDomain;
                                if (keyDomain == null) keyDomain = uddiClerk.getUDDINode().getProperties().getString("keyDomain");
                                if ((businessName == null && !uddiClerk.getUDDINode().getProperties().containsKey("businessName"))
                                    || keyDomain == null && !uddiClerk.getUDDINode().getProperties().containsKey("keyDomain")) throw new ConfigurationErrorsException("Either the wsdl(" + wsdls[w]
                                         + ") or clerk (" + uddiClerk.getName() + ") elements require a businessKey, or businessName & keyDomain attributes");
                                else
                                {
                                    Properties properties = (uddiClerk.getUDDINode().getProperties());
                                    if (businessName != null) properties.setProperty("businessName", businessName);
                                    if (keyDomain != null) properties.setProperty("keyDomain", keyDomain);
                                    businessKey = UDDIKeyConvention.getBusinessKey(properties);
                                }
                            }
                            if (!businessKey.ToLower().StartsWith("uddi:") || !businessKey.Substring(5).Contains(":"))
                            {
                                throw new ConfigurationErrorsException("The businessKey " + businessKey + " does not implement a valid UDDI v3 key format.");
                            }
                            wsdl.setBusinessKey(businessKey);
                            if (keyDomain == null)
                            {
                                keyDomain = businessKey.Split(new string[] { ":" }, StringSplitOptions.RemoveEmptyEntries)[1];
                            }
                            wsdl.setKeyDomain(keyDomain);
                            wsdls[w] = wsdl;
                        }
                        uddiClerk.setWsdls(wsdls);
                    }

                    clerks.Add(uddiClerk.getName(), uddiClerk);
                }
            }
            else
                log.warn("No clerks are defined!");
            return clerks;

        }

        public bool isRegisterOnStartup()
        {
            try
            {
                return config.client.clerks.registerOnStartup;
            }
            catch { }
            return false;

        }

        private Dictionary<String, UDDINode> readNodeConfig(uddi config, Properties properties)
        {

            //String[] names = config.getStringArray("client.nodes.node.name");
            Dictionary<String, UDDINode> nodes = new Dictionary<String, UDDINode>();
            log.debug("node count=" + config.client.nodes.Length);

            for (int i = 0; i < config.client.nodes.Length; i++)
            {
                UDDINode uddiNode = new UDDINode();

                uddiNode.setClientName(config.client.nodes[i].name);
                uddiNode.setProperties(config.client.nodes[i].properties);
                uddiNode.setHomeJUDDI(config.client.nodes[i].isHomeJUDDI);
                uddiNode.setName(config.client.nodes[i].name);
                uddiNode.setClientName(config.client.nodes[i].name);
                uddiNode.setDescription(config.client.nodes[i].description);
                uddiNode.setProxyTransport(config.client.nodes[i].proxyTransport);

                uddiNode.setInquiryUrl(TokenResolver.replaceTokens(config.client.nodes[i].inquiryUrl, config.client.nodes[i].properties));
                uddiNode.setPublishUrl(TokenResolver.replaceTokens(config.client.nodes[i].publishUrl, config.client.nodes[i].properties));
                uddiNode.setCustodyTransferUrl(TokenResolver.replaceTokens(config.client.nodes[i].custodyTransferUrl, config.client.nodes[i].properties));
                uddiNode.setSecurityUrl(TokenResolver.replaceTokens(config.client.nodes[i].securityUrl, config.client.nodes[i].properties));
                uddiNode.setSubscriptionUrl(TokenResolver.replaceTokens(config.client.nodes[i].subscriptionUrl, config.client.nodes[i].properties));
                uddiNode.setSubscriptionListenerUrl(TokenResolver.replaceTokens(config.client.nodes[i].subscriptionListenerUrl, config.client.nodes[i].properties));
                uddiNode.setJuddiApiUrl(TokenResolver.replaceTokens(config.client.nodes[i].juddiApiUrl, config.client.nodes[i].properties));
                uddiNode.setFactoryInitial(config.client.nodes[i].factoryInitial);
                uddiNode.setFactoryURLPkgs(config.client.nodes[i].factoryURLPkgs);
                uddiNode.setFactoryNamingProvider(config.client.nodes[i].factoryNamingProvider);
                nodes.Add(uddiNode.getName(), uddiNode);
            }
            return nodes;

        }

        enum XRegistrationType
        {
            business, servicebinding
        }

        private HashSet<XRegistration> readXBusinessRegConfig(uddi config, Dictionary<String, UDDIClerk> clerks)
        {

            HashSet<XRegistration> xRegistrations = new HashSet<XRegistration>();
            if (config.client.clerks == null)
            {
                log.warn("XRegistration cannot continue, no clerks are defined!");
                return xRegistrations;
            }
            if (config.client.clerks.xregister==null || config.client.clerks.xregister.business == null)
                return xRegistrations;
            if (config.client.clerks.xregister.business.Length > 0)
                log.info("XRegistration " + config.client.clerks.xregister.business.Length + " business Keys");
            for (int i = 0; i < config.client.clerks.xregister.business.Length; i++)
            {
                XRegistration xRegistration = new XRegistration();
                xRegistration.setEntityKey(config.client.clerks.xregister.business[i].bindingKey);

                String fromClerkRef = config.client.clerks.xregister.business[i].fromClerk;
                if (!clerks.ContainsKey(fromClerkRef)) throw new ConfigurationErrorsException("Could not find fromClerk with name=" + fromClerkRef);
                UDDIClerk fromClerk = clerks[(fromClerkRef)];
                xRegistration.setFromClerk(fromClerk);

                String toClerkRef = config.client.clerks.xregister.business[i].toClerk;
                if (!clerks.ContainsKey(toClerkRef)) throw new ConfigurationErrorsException("Could not find toClerk with name=" + toClerkRef);
                UDDIClerk toClerk = clerks[(toClerkRef)];
                xRegistration.setToClerk(toClerk);
                log.debug(xRegistration);

                xRegistrations.Add(xRegistration);
            }
            return xRegistrations;

        }

        private HashSet<XRegistration> readXServiceBindingRegConfig(uddi config, Dictionary<String, UDDIClerk> clerks)
        {
            HashSet<XRegistration> xRegistrations = new HashSet<XRegistration>();
            if (config.client.clerks == null)
            {
                log.warn("XRegistration cannot continue, no clerks are defined!");
                return xRegistrations;
            }
            if (config.client.clerks.xregister==null || config.client.clerks.xregister.servicebinding == null)
                return xRegistrations;
            if (config.client.clerks.xregister.servicebinding.Length > 0)
                log.info("XRegistration " + config.client.clerks.xregister.servicebinding.Length + " serviceBinding Keys");
            for (int i = 0; i < config.client.clerks.xregister.servicebinding.Length; i++)
            {
                XRegistration xRegistration = new XRegistration();
                xRegistration.setEntityKey(config.client.clerks.xregister.servicebinding[i].bindingKey);

                String fromClerkRef = config.client.clerks.xregister.servicebinding[i].fromClerk;
                if (!clerks.ContainsKey(fromClerkRef)) throw new ConfigurationErrorsException("Could not find fromClerk with name=" + fromClerkRef);
                UDDIClerk fromClerk = clerks[(fromClerkRef)];
                xRegistration.setFromClerk(fromClerk);

                String toClerkRef = config.client.clerks.xregister.servicebinding[i].toClerk;
                if (!clerks.ContainsKey(toClerkRef)) throw new ConfigurationErrorsException("Could not find toClerk with name=" + toClerkRef);
                UDDIClerk toClerk = clerks[(toClerkRef)];
                xRegistration.setToClerk(toClerk);
                log.debug(xRegistration);

                xRegistrations.Add(xRegistration);
            }
            return xRegistrations;
        }

        public Dictionary<String, UDDINode> getUDDINodes()
        {
            return uddiNodes;
        }

        public UDDINode getHomeNode()
        {
            if (uddiNodes == null) throw new ConfigurationErrorsException("The juddi client configuration " +
                      "must contain at least one node element.");
            if (uddiNodes.Values.Count == 1)
            {
                IEnumerator it = uddiNodes.Values.GetEnumerator();
                it.MoveNext();
                return it.Current as UDDINode;
            }
            foreach (UDDINode uddiNode in uddiNodes.Values)
            {
                if (uddiNode.isHomeJUDDI())
                {
                    return uddiNode;
                }
            }
            throw new ConfigurationErrorsException("One of the node elements in the client configuration needs to a 'isHomeJUDDI=\"true\"' attribute.");
        }

        public UDDINode getUDDINode(String nodeName)
        {
            if (!uddiNodes.ContainsKey(nodeName))
            {
                throw new ConfigurationErrorsException("Node '" + nodeName
                        + "' cannot be found in the config '" + getClientName() + "'");
            }
            return uddiNodes[nodeName];
        }

        public Dictionary<String, UDDIClerk> getUDDIClerks()
        {
            return uddiClerks;
        }

        public HashSet<XRegistration> getXServiceBindingRegistrations()
        {
            return xServiceBindingRegistrations;
        }

        /**
        * Used for WADL/WSDL to WSDL
        * @return 
        */
        public bool getX_To_Wsdl_Ignore_SSL_Errors()
        {
            return this.config.client.XtoWsdl.IgnoreSSLErrors;
        }


        public HashSet<XRegistration> getXBusinessRegistrations()
        {
            return xBusinessRegistrations;
        }
        /// <summary>
        /// gives access to the raw configuration xml structure
        /// </summary>
        /// <returns></returns>
        public uddi getConfiguration()
        {
            return this.config;
        }

        public String getClientName()
        {
            return clientName;
        }

        public String getConfigurationFile()
        {
            return configurationFile;
        }
    }
}
