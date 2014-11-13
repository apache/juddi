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

import java.io.File;
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
import org.apache.juddi.v3.client.subscription.SubscriptionCallbackListener;

/**
 * Handles the client configuration of the uddi-client. By default it first
 * looks at system properties. Then loads from the config file from the system
 * property "uddi.client.xml", next the user specified file, finally,
 * "META-INF/uddi.xml"
 *
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class ClientConfig {

        public final static String UDDI_CONFIG_FILENAME_PROPERTY = "uddi.client.xml";
        public final static String DEFAULT_UDDI_CONFIG = "META-INF/uddi.xml";
        private Log log = LogFactory.getLog(ClientConfig.class);
        private Configuration config = null;

        private Map<String, UDDINode> uddiNodes = null;
        private Map<String, UDDIClerk> uddiClerks = null;
        private Set<XRegistration> xBusinessRegistrations = null;
        private Set<XRegistration> xServiceBindingRegistrations = null;
        private String clientName = null;
        private String clientCallbackUrl = null;
        private String configurationFile = null;

        /**
         * Constructor (note Singleton pattern).
         *
         * @throws ConfigurationException
         */
        public ClientConfig(String configurationFile) throws ConfigurationException {
                loadConfiguration(configurationFile, null);
        }

        /**
         * Constructor (note Singleton pattern).
         *
         * @throws ConfigurationException
         */
        public ClientConfig(String configurationFile, Properties properties) throws ConfigurationException {
                loadConfiguration(configurationFile, properties);
        }

        /**
         * Attempts to save any changes made to the configuration back to disk
         * Revised in 3.2.1 to reconstruct the file from the in memory data
         * structure, enable you to programmatically add nodes.
         * <br><br>
         * For previous functionality see, saveConfigRaw()
         *
         * @throws ConfigurationException
         */
        public void saveConfig() throws ConfigurationException {

                if (log.isDebugEnabled()) {
                        System.out.println("DEBUG dumping current cfg");
                        Iterator<String> keys = config.getKeys();
                        while (keys.hasNext()) {
                                String k = keys.next();
                                System.out.println(k + " = " + config.getProperty(k));
                        }
                }
                XMLConfiguration saveConfiguration = new XMLConfiguration();
                Configuration cc = new CompositeConfiguration(saveConfiguration);

                saveConfiguration.setRootElementName("uddi");

                cc.addProperty("reloadDelay", config.getProperty("reloadDelay"));
                addCurrentNodeConfig(cc);
                addCurrentClerks(cc);
                try {
                        addDigitalSubscription(cc);
                } catch (Exception ex) {
                        throw new ConfigurationException("error", ex);
                }
                addSubscriptionCallback(cc);
                addXRegistration(cc);
                if (log.isDebugEnabled()) {
                        System.out.println("DEBUG dumping NEW cfg");
                        Iterator<String> keys = cc.getKeys();

                        while (keys.hasNext()) {
                                String k = keys.next();
                                System.out.println(k + " = " + config.getProperty(k));
                        }
                }

                saveConfiguration.save(configurationFile);
        }
        
        /**
         * Use this method to attempt to save the jUDDI configuration file after
         * you've modified it using the Apache Commons Configuration settings.
         * This is especially useful if you've constructed a user interface for manipulating
         * the configuration like a properties sheet and is used by the juddi-gui (web ui)
         * @since 3.2.1
         * @throws org.apache.commons.configuration.ConfigurationException
         */
        public void saveConfigRaw() throws ConfigurationException{
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
                xServiceBindingRegistrations = readXServiceBindingRegConfig(config, uddiClerks);
                xBusinessRegistrations = readXBusinessRegConfig(config, uddiClerks);
        }

        /**
         * Does the actual work of reading the configuration from System
         * Properties and/or uddi.xml file. When the uddi.xml file is updated
         * the file will be reloaded. By default the reloadDelay is set to 1
         * second to prevent excessive date stamp checking.
         */
        private void loadConfiguration(String configurationFile, Properties properties) throws ConfigurationException {
                //Properties from system properties
                CompositeConfiguration compositeConfig = new CompositeConfiguration();
                compositeConfig.addConfiguration(new SystemConfiguration());
                //Properties from XML file
                if (System.getProperty(UDDI_CONFIG_FILENAME_PROPERTY) != null) {
                        log.info("Using system property config override");
                        configurationFile = System.getProperty(UDDI_CONFIG_FILENAME_PROPERTY);
                }
                XMLConfiguration xmlConfig = null;
                if (configurationFile != null) {
                        xmlConfig = new XMLConfiguration(configurationFile);
                } else {
                        final String filename = System.getProperty(UDDI_CONFIG_FILENAME_PROPERTY);
                        if (filename != null) {
                                xmlConfig = new XMLConfiguration(filename);
                        } else {
                                xmlConfig = new XMLConfiguration(DEFAULT_UDDI_CONFIG);
                        }
                }
                log.info("Reading UDDI Client properties file " + xmlConfig.getBasePath() + " use -D" + UDDI_CONFIG_FILENAME_PROPERTY + " to override");
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

                validateConfig();
        }

        private Map<String, UDDIClerk> readClerkConfig(Configuration config, Map<String, UDDINode> uddiNodes)
             throws ConfigurationException {
                clientName = config.getString("client[@name]");
                clientCallbackUrl = config.getString("client[@callbackUrl]");
                Map<String, UDDIClerk> clerks = new HashMap<String, UDDIClerk>();
                if (config.containsKey("client.clerks.clerk[@name]")) {
                        String[] names = config.getStringArray("client.clerks.clerk[@name]");

                        log.debug("clerk names=" + names.length);
                        for (int i = 0; i < names.length; i++) {
                                UDDIClerk uddiClerk = new UDDIClerk();
                                uddiClerk.setManagerName(clientName);
                                uddiClerk.setName(config.getString("client.clerks.clerk(" + i + ")[@name]"));
                                String nodeRef = config.getString("client.clerks.clerk(" + i + ")[@node]");
                                if (!uddiNodes.containsKey(nodeRef)) {
                                        throw new ConfigurationException("Could not find Node with name=" + nodeRef);
                                }
                                UDDINode uddiNode = uddiNodes.get(nodeRef);
                                uddiClerk.setUDDINode(uddiNode);
                                uddiClerk.setPublisher(config.getString("client.clerks.clerk(" + i + ")[@publisher]"));
                                uddiClerk.setPassword(config.getString("client.clerks.clerk(" + i + ")[@password]"));
                                uddiClerk.setIsPasswordEncrypted(config.getBoolean("client.clerks.clerk(" + i + ")[@isPasswordEncrypted]", false));
                                uddiClerk.setCryptoProvider(config.getString("client.clerks.clerk(" + i + ")[@cryptoProvider]"));

                                String clerkBusinessKey = config.getString("client.clerks.clerk(" + i + ")[@businessKey]");
                                String clerkBusinessName = config.getString("client.clerks.clerk(" + i + ")[@businessName]");
                                String clerkKeyDomain = config.getString("client.clerks.clerk(" + i + ")[@keyDomain]");

                                String[] classes = config.getStringArray("client.clerks.clerk(" + i + ").class");
                                uddiClerk.setClassWithAnnotations(classes);

                                int numberOfWslds = config.getStringArray("client.clerks.clerk(" + i + ").wsdl").length;
                                if (numberOfWslds > 0) {
                                        UDDIClerk.WSDL[] wsdls = new UDDIClerk.WSDL[numberOfWslds];
                                        for (int w = 0; w < wsdls.length; w++) {

                                                UDDIClerk.WSDL wsdl = uddiClerk.new WSDL();
                                                String fileName = config.getString("client.clerks.clerk(" + i + ").wsdl(" + w + ")");
                                                wsdl.setFileName(fileName);
                                                if (!new File(fileName).exists()) {
                                                        log.warn("The wsdl file referenced in the config at '" + fileName + "' doesn't exist!");
                                                }
                                                String businessKey = config.getString("client.clerks.clerk(" + i + ").wsdl(" + w + ")[@businessKey]");
                                                String businessName = config.getString("client.clerks.clerk(" + i + ").wsdl(" + w + ")[@businessName]");
                                                String keyDomain = config.getString("client.clerks.clerk(" + i + ").wsdl(" + w + ")[@keyDomain]");
                                                if (businessKey == null) {
                                                        businessKey = clerkBusinessKey;
                                                }
                                                if (businessKey == null) {
                                                        businessKey = uddiClerk.getUDDINode().getProperties().getProperty("businessKey");
                                                }
                                                if (businessKey == null) {
                                                        //use key convention to build the businessKey
                                                        if (businessName == null) {
                                                                businessName = clerkBusinessName;
                                                        }
                                                        if (keyDomain == null) {
                                                                keyDomain = clerkKeyDomain;
                                                        }
                                                        if (keyDomain == null) {
                                                                keyDomain = uddiClerk.getUDDINode().getProperties().getProperty("keyDomain");
                                                        }
                                                        if ((businessName == null && !uddiClerk.getUDDINode().getProperties().containsKey("businessName"))
                                                             || keyDomain == null && !uddiClerk.getUDDINode().getProperties().containsKey("keyDomain")) {
                                                                throw new ConfigurationException("Either the wsdl(" + wsdls[w]
                                                                     + ") or clerk (" + uddiClerk.name + ") elements require a businessKey, or businessName & keyDomain attributes");
                                                        } else {
                                                                Properties properties = new Properties(uddiClerk.getUDDINode().getProperties());
                                                                if (businessName != null) {
                                                                        properties.put("businessName", businessName);
                                                                }
                                                                if (keyDomain != null) {
                                                                        properties.put("keyDomain", keyDomain);
                                                                }
                                                                businessKey = UDDIKeyConvention.getBusinessKey(properties);
                                                        }
                                                }
                                                if (!businessKey.toLowerCase().startsWith("uddi:") || !businessKey.substring(5).contains(":")) {
                                                        throw new ConfigurationException("The businessKey '" + businessKey + "' does not implement a valid UDDI v3 key format. See config file at client.clerks.clerk(" + i + ").wsdl(" + w + ")[@businessKey]");
                                                }
                                                wsdl.setBusinessKey(businessKey);
                                                if (keyDomain == null) {
                                                        keyDomain = businessKey.split(":")[1];
                                                }
                                                wsdl.setKeyDomain(keyDomain);
                                                wsdls[w] = wsdl;
                                        }
                                        uddiClerk.setWsdls(wsdls);
                                }

                                clerks.put(names[i], uddiClerk);
                        }
                }
                return clerks;
        }

        /**
         * signals that the specified classes/wsdls are registered with the UDDI
         * server when UDDIClient.start() is called
         * client.clerks[@registerOnStartup]
         *
         * @return true/false
         */
        public boolean isRegisterOnStartup() {
                boolean isRegisterOnStartup = false;
                if (config.containsKey("client.clerks[@registerOnStartup]")) {
                        isRegisterOnStartup = config.getBoolean("client.clerks[@registerOnStartup]");
                }
                return isRegisterOnStartup;
        }

        private Map<String, UDDINode> readNodeConfig(Configuration config, Properties properties)
             throws ConfigurationException {
                String[] names = config.getStringArray("client.nodes.node.name");
                Map<String, UDDINode> nodes = new HashMap<String, UDDINode>();
                log.debug("node names=" + names.length);
                for (int i = 0; i < names.length; i++) {
                        UDDINode uddiNode = new UDDINode();
                        String nodeName = config.getString("client.nodes.node(" + i + ").name");
                        String[] propertyKeys = config.getStringArray("client.nodes.node(" + i + ").properties.property[@name]");

                        if (propertyKeys != null && propertyKeys.length > 0) {
                                if (properties == null) {
                                        properties = new Properties();
                                }
                                for (int p = 0; p < propertyKeys.length; p++) {
                                        String name = config.getString("client.nodes.node(" + i + ").properties.property(" + p + ")[@name]");
                                        String value = config.getString("client.nodes.node(" + i + ").properties.property(" + p + ")[@value]");
                                        log.debug("Property: name=" + name + " value=" + value);
                                        properties.put(name, value);
                                }
                                uddiNode.setProperties(properties);
                        }

                        uddiNode.setHomeJUDDI(config.getBoolean("client.nodes.node(" + i + ")[@isHomeJUDDI]", false));
                        uddiNode.setName(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").name"), properties));
                        uddiNode.setClientName(TokenResolver.replaceTokens(config.getString("client[@name]"), properties));
                        uddiNode.setDescription(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").description"), properties));
                        uddiNode.setProxyTransport(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").proxyTransport"), properties));
                        uddiNode.setInquiryUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").inquiryUrl"), properties));
                        uddiNode.setInquiryRESTUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").inquiryRESTUrl"), properties));
                        uddiNode.setPublishUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").publishUrl"), properties));
                        uddiNode.setCustodyTransferUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").custodyTransferUrl"), properties));
                        uddiNode.setSecurityUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").securityUrl"), properties));
                        uddiNode.setReplicationUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").replicationUrl"), properties));
                        uddiNode.setSubscriptionUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").subscriptionUrl"), properties));
                        uddiNode.setSubscriptionListenerUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").subscriptionListenerUrl"), properties));
                        uddiNode.setJuddiApiUrl(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").juddiApiUrl"), properties));
                        uddiNode.setFactoryInitial(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").javaNamingFactoryInitial"), properties));
                        uddiNode.setFactoryURLPkgs(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").javaNamingFactoryUrlPkgs"), properties));
                        uddiNode.setFactoryNamingProvider(TokenResolver.replaceTokens(config.getString("client.nodes.node(" + i + ").javaNamingProviderUrl"), properties));
                        nodes.put(nodeName, uddiNode);
                }
                return nodes;
        }

        private Set<XRegistration> readXBusinessRegConfig(Configuration config, Map<String, UDDIClerk> clerks)
             throws ConfigurationException {
                return readXRegConfig(config, clerks, "business");
        }

        private Set<XRegistration> readXServiceBindingRegConfig(Configuration config, Map<String, UDDIClerk> clerks)
             throws ConfigurationException {
                return readXRegConfig(config, clerks, "servicebinding");
        }

        private Set<XRegistration> readXRegConfig(Configuration config, Map<String, UDDIClerk> clerks, String entityType)
             throws ConfigurationException {
                String[] entityKeys = config.getStringArray("client.clerks.xregister." + entityType + "[@entityKey]");
                Set<XRegistration> xRegistrations = new HashSet<XRegistration>();
                if (entityKeys.length > 0) {
                        log.info("XRegistration " + entityKeys.length + " " + entityType + "Keys");
                }
                for (int i = 0; i < entityKeys.length; i++) {
                        XRegistration xRegistration = new XRegistration();
                        xRegistration.setEntityKey(config.getString("client.clerks.xregister." + entityType + "(" + i + ")[@entityKey]"));

                        String fromClerkRef = config.getString("client.clerks.xregister." + entityType + "(" + i + ")[@fromClerk]");
                        if (!clerks.containsKey(fromClerkRef)) {
                                throw new ConfigurationException("Could not find fromClerk with name=" + fromClerkRef);
                        }
                        UDDIClerk fromClerk = clerks.get(fromClerkRef);
                        xRegistration.setFromClerk(fromClerk);

                        String toClerkRef = config.getString("client.clerks.xregister." + entityType + "(" + i + ")[@toClerk]");
                        if (!clerks.containsKey(toClerkRef)) {
                                throw new ConfigurationException("Could not find toClerk with name=" + toClerkRef);
                        }
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
         * gets the current configuration's node list Only a copy of these
         * values are returned
         *
         * @return a list of nodes representing the config file as described
         * @since 3.3 updated to return all fields
         * @since 3.2
         */
        public List<Node> getUDDINodeList() {
                List<Node> ret = new ArrayList<Node>();
                Iterator<UDDINode> it = uddiNodes.values().iterator();
                while (it.hasNext()) {
                        UDDINode next = it.next();
                        Node n = new Node();
                        n.setClientName(next.getClientName());
                        n.setDescription(next.getDescription());
                        n.setName(next.getName());
                        n.setProxyTransport(next.getProxyTransport());
                        n.setCustodyTransferUrl(next.getCustodyTransferUrl());
                        n.setFactoryInitial(next.getFactoryInitial());
                        n.setFactoryNamingProvider(next.getFactoryNamingProvider());
                        n.setFactoryURLPkgs(next.getFactoryURLPkgs());
                        n.setInquiryUrl(next.getInquiryUrl());
                        n.setJuddiApiUrl(next.getJuddiApiUrl());
                        n.setPublishUrl(next.getPublishUrl());
                        n.setReplicationUrl(next.getReplicationUrl());
                        n.setSecurityUrl(next.getSecurityUrl());
                        n.setSubscriptionListenerUrl(next.getSubscriptionListenerUrl());
                        n.setSubscriptionUrl(next.getSubscriptionUrl());
                        ret.add(n);
                }
                return ret;
        }

        public UDDINode getHomeNode() throws ConfigurationException {
                if (uddiNodes == null) {
                        throw new ConfigurationException("The juddi client configuration "
                             + "must contain at least one node element.");
                }
                if (uddiNodes.values().size() == 1) {
                        return uddiNodes.values().iterator().next();
                }
                UDDINode ret = null;
                for (UDDINode uddiNode : uddiNodes.values()) {
                        if (uddiNode.isHomeJUDDI()) {
                                if (ret != null) {
                                        throw new ConfigurationException("Only one of the node elements in the client configuration needs to a 'isHomeJUDDI=\"true\"' attribute.");
                                }
                                ret = uddiNode;
                        }
                }
                if (ret != null) {
                        return ret;
                }
                throw new ConfigurationException("One of the node elements in the client configuration needs to a 'isHomeJUDDI=\"true\"' attribute.");
        }

        public UDDINode getUDDINode(String nodeName) throws ConfigurationException {
                if (!uddiNodes.containsKey(nodeName)) {
                        throw new ConfigurationException("Node '" + nodeName
                             + "' cannot be found in the config '" + getClientName() + "'");
                }
                return uddiNodes.get(nodeName);
        }

        public Map<String, UDDIClerk> getUDDIClerks() {
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
         *
         * @return true/false
         */
        public boolean isX_To_Wsdl_Ignore_SSL_Errors() {
                return this.config.getBoolean("client.XtoWsdl.IgnoreSSLErrors", false);
        }

        /**
         * Fetches all digital signature related properties for the digital
         * signature utility. warning, this will decrypt all passwords
         *
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

                p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE, this.config.getString("client.signature.signingKeyStorePath", ""));
                p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, this.config.getString("client.signature.signingKeyStoreType", ""));

                if (this.config.getBoolean("client.signature.signingKeyPassword[@isPasswordEncrypted]", false)) {
                        String enc = this.config.getString("client.signature.signingKeyPassword", "");
                        String prov = this.config.getString("client.signature.signingKeyPassword[@cryptoProvider]", "");
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD, CryptorFactory.getCryptor(prov).decrypt(enc));
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_WAS_ENC, "true");
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_PROVIDER, prov);
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_CIPHER, enc);
                } else {
                        log.warn("Hey, you should consider encrypting your key password!");
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD, this.config.getString("client.signature.signingKeyPassword", ""));
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_WAS_ENC, "false");
                }
                if (this.config.getBoolean("client.signature.signingKeyStoreFilePassword[@isPasswordEncrypted]", false)) {
                        String enc = this.config.getString("client.signature.signingKeyStoreFilePassword", "");
                        String prov = this.config.getString("client.signature.signingKeyStoreFilePassword[@cryptoProvider]", "");
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, CryptorFactory.getCryptor(prov).decrypt(enc));
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD_PROVIDER, (prov));
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD_CIPHER, (enc));
                } else {
                        log.warn("Hey, you should consider encrypting your keystore password!");
                        p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, this.config.getString("client.signature.signingKeyStoreFilePassword", ""));
                }

                p.setProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, this.config.getString("client.signature.signingKeyAlias", ""));
                p.setProperty(DigSigUtil.SIGNATURE_METHOD, this.config.getString("client.signature.signatureMethod", "http://www.w3.org/2000/09/xmldsig#rsa-sha1"));
                p.setProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, this.config.getString("client.signature.keyInfoInclusionSubjectDN", "true"));
                p.setProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, this.config.getString("client.signature.keyInfoInclusionBase64PublicKey", "true"));
                p.setProperty(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, this.config.getString("client.signature.keyInfoInclusionSerial", "true"));

                p.setProperty(DigSigUtil.SIGNATURE_OPTION_DIGEST_METHOD, this.config.getString("client.signature.digestMethod", "http://www.w3.org/2000/09/xmldsig#sha1"));

                p.setProperty(DigSigUtil.TRUSTSTORE_FILE, this.config.getString("client.signature.trustStorePath", ""));
                p.setProperty(DigSigUtil.TRUSTSTORE_FILETYPE, this.config.getString("client.signature.trustStoreType", ""));

                if (this.config.getBoolean("client.signature.trustStorePassword[@isPasswordEncrypted]", false)) {
                        String enc = this.config.getString("client.signature.trustStorePassword", "");
                        String prov = this.config.getString("client.signature.trustStorePassword[@cryptoProvider]", "");
                        p.setProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, CryptorFactory.getCryptor(prov).decrypt(enc));
                        p.setProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD_PROVIDER, (prov));
                        p.setProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD_CIPHER, (enc));
                } else {
                        log.warn("Hey, you should consider encrypting your trust store password!");
                        p.setProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD, this.config.getString("client.signature.trustStorePassword", ""));
                }

                return p;
        }

        /**
         * adds a new node to the client configuration section. Don't forget to
         * call save to persist the changes
         *
         * @param n
         * @since 3.3
         */
        public void addUDDINode(UDDINode node) throws ConfigurationException {
                if (node == null) {
                        throw new ConfigurationException("The new node is null");
                }
                if (this.uddiNodes.containsKey(node.getName())) {
                        throw new ConfigurationException("Node '" + node.getName() + "' already existings in the collection!");
                }
                if (node.getClientName() == null || "".equalsIgnoreCase(node.getClientName())) {
                        log.info("ClientName wasn't specified, I'll configure it with the defaults");
                        node.setClientName(this.clientName);
                }
                this.uddiNodes.put(node.getClientName(), node);

        }

        /**
         * removes the named node from the client configuration section. don't
         * forget to save to persist the changes
         *
         * @param name expecting the Node name, not the "clientName"
         * @throws ConfigurationException
         */
        public void removeUDDINode(String name) throws ConfigurationException {
                if (this.uddiNodes.containsKey(name)) {
                        this.uddiNodes.remove(name);
                }

                //TODO to configuration xml structure
        }

        /**
         * performs some basic validation tests on the config setting that was
         * read from file
         */
        private void validateConfig() throws ConfigurationException {
                if (config == null) {
                        throw new ConfigurationException("config is null!");
                }
                if (uddiNodes == null) {
                        throw new ConfigurationException("nodes is null!");
                }
                if (uddiClerks == null) {
                        throw new ConfigurationException("clerks is null!");
                }
                Iterator<Map.Entry<String, UDDIClerk>> it = uddiClerks.entrySet().iterator();
                while (it.hasNext()) {
                        Map.Entry<String, UDDIClerk> next = it.next();
                        if (next.getValue().uddiNode == null) {
                                throw new ConfigurationException("clerk " + next.getValue().name + " references a node that doesn't exist!");
                        }
                }
        }

        private void addCurrentNodeConfig(Configuration cc) {

                cc.addProperty("[@xmlns]", config.getProperty("[@xmlns]"));
                cc.addProperty("client(0)[@name]", clientName);
                Iterator<Map.Entry<String, UDDINode>> iterator = uddiNodes.entrySet().iterator();
                int i = 0;
                while (iterator.hasNext()) {
                        log.debug("node names=" + uddiNodes.size());

                        UDDINode uddiNode = iterator.next().getValue();

                        Properties properties = uddiNode.getProperties();

                        if (properties == null) {
                                properties = new Properties();
                        }
                        Iterator<Map.Entry<Object, Object>> iterator1 = properties.entrySet().iterator();
                        int x = 0;
                        while (iterator1.hasNext()) {
                                Map.Entry<Object, Object> next = iterator1.next();
                                cc.addProperty("client(0).nodes.node(" + i + ").properties.property(" + x + ")[@name]", next.getKey());
                                cc.addProperty("client(0).nodes.node(" + i + ").properties.property(" + x + ")[@value]", next.getValue());

                                log.debug("Property: name=" + next.getKey() + " value=" + next.getValue());

                                x++;
                        }

                        cc.addProperty("client(0).nodes.node(" + i + ")[@isHomeJUDDI]", uddiNode.isHomeJUDDI());
                        cc.addProperty("client(0).nodes.node(" + i + ").name", uddiNode.getName());
                        cc.addProperty("client(0).nodes.node(" + i + ").description", uddiNode.getDescription());
                        cc.addProperty("client(0).nodes.node(" + i + ").proxyTransport", uddiNode.getProxyTransport());
                        cc.addProperty("client(0).nodes.node(" + i + ").inquiryUrl", uddiNode.getInquiryUrl());
                        cc.addProperty("client(0).nodes.node(" + i + ").inquiryRESTUrl", uddiNode.getInquiry_REST_Url());
                        cc.addProperty("client(0).nodes.node(" + i + ").publishUrl", uddiNode.getPublishUrl());
                        cc.addProperty("client(0).nodes.node(" + i + ").custodyTransferUrl", uddiNode.getCustodyTransferUrl());
                        cc.addProperty("client(0).nodes.node(" + i + ").securityUrl", uddiNode.getSecurityUrl());
                        cc.addProperty("client(0).nodes.node(" + i + ").replicationUrl", uddiNode.getReplicationUrl());
                        cc.addProperty("client(0).nodes.node(" + i + ").subscriptionUrl", uddiNode.getSubscriptionUrl());
                        cc.addProperty("client(0).nodes.node(" + i + ").juddiApiUrl", uddiNode.getJuddiApiUrl());
                        cc.addProperty("client(0).nodes.node(" + i + ").subscriptionListenerUrl", uddiNode.getSubscriptionListenerUrl());
                        cc.addProperty("client(0).nodes.node(" + i + ").javaNamingFactoryInitial", uddiNode.getFactoryInitial());
                        cc.addProperty("client(0).nodes.node(" + i + ").javaNamingFactoryUrlPkgs", uddiNode.getFactoryURLPkgs());
                        cc.addProperty("client(0).nodes.node(" + i + ").javaNamingProviderUrl", uddiNode.getFactoryNamingProvider());

                        i++;
                }

        }

        private void addCurrentClerks(Configuration cc) {

                Iterator<Map.Entry<String, UDDIClerk>> iterator = uddiClerks.entrySet().iterator();
                clientName = config.getString("client[@name]");
                clientCallbackUrl = config.getString("client(0)[@callbackUrl]");

                cc.addProperty("client(0).clerks[@registerOnStartup]", isRegisterOnStartup());
                int i = 0;
                while (iterator.hasNext()) {

                        UDDIClerk uddiClerk = iterator.next().getValue();
                        cc.addProperty("client(0).clerks.clerk(" + i + ")[@name]", uddiClerk.getName());
                        //registerOnStartup
                        cc.addProperty("client(0).clerks.clerk(" + i + ")[@node]", uddiClerk.getUDDINode().getName());

                        cc.addProperty("client(0).clerks.clerk(" + i + ")[@publisher]", uddiClerk.getPublisher());
                        cc.addProperty("client(0).clerks.clerk(" + i + ")[@password]", uddiClerk.getRawPassword());

                        cc.addProperty("client(0).clerks.clerk(" + i + ")[@isPasswordEncrypted]", uddiClerk.getIsPasswordEncrypted());

                        cc.addProperty("client(0).clerks.clerk(" + i + ")[@cryptoProvider]", uddiClerk.getCryptoProvider());

                        String[] classes = uddiClerk.getClassWithAnnotations();
                        if (classes != null) {
                                for (int x = 0; x < classes.length; x++) {
                                        cc.addProperty("client(0).clerks.clerk(" + i + ").class(" + x + ")", classes[x]);
                                }
                        }

                        UDDIClerk.WSDL[] wsdls = uddiClerk.getWsdls();
                        if (wsdls != null) {
                                for (int w = 0; w < wsdls.length; w++) {
                                        cc.addProperty("client(0).clerks.clerk(" + i + ").wsdl(" + w + ")", wsdls[w].getFileName());
                                        cc.addProperty("client(0).clerks.clerk(" + i + ").wsdl(" + w + ")[@businessKey]", wsdls[w].getBusinessKey());
                                        cc.addProperty("client(0).clerks.clerk(" + i + ").wsdl(" + w + ")[@keyDomain]", wsdls[w].getKeyDomain());
                                }
                        }
                        i++;
                }

                if (xBusinessRegistrations != null) {
                        Iterator<XRegistration> iterator1 = xBusinessRegistrations.iterator();
                        int x = 0;
                        while (iterator1.hasNext()) {
                                XRegistration next = iterator1.next();
                                cc.addProperty("client(0).clerks.business(" + x + ")[@fromClerk]", next.getFromClerk().name);
                                cc.addProperty("client(0).clerks.business(" + x + ")[@toClerk]", next.getToClerk().name);
                                cc.addProperty("client(0).clerks.business(" + x + ")[@entityKey]", next.getEntityKey());
                                x++;
                        }
                }
                if (xServiceBindingRegistrations != null) {
                        Iterator<XRegistration> iterator1 = xServiceBindingRegistrations.iterator();
                        int x = 0;
                        while (iterator1.hasNext()) {
                                XRegistration next = iterator1.next();
                                cc.addProperty("client(0).clerks.servicebinding(" + x + ")[@fromClerk]", next.getFromClerk().name);
                                cc.addProperty("client(0).clerks.servicebinding(" + x + ")[@toClerk]", next.getToClerk().name);
                                cc.addProperty("client(0).clerks.servicebinding(" + x + ")[@entityKey]", next.getEntityKey());
                                x++;
                        }
                }
        }

        private void addSubscriptionCallback(Configuration cc) {
                if (this.config.containsKey(SubscriptionCallbackListener.PROPERTY_AUTOREG_BT)) {
                        cc.addProperty(SubscriptionCallbackListener.PROPERTY_AUTOREG_BT, this.config.getProperty(SubscriptionCallbackListener.PROPERTY_AUTOREG_BT));
                }
                if (this.config.containsKey(SubscriptionCallbackListener.PROPERTY_AUTOREG_SERVICE_KEY)) {
                        cc.addProperty(SubscriptionCallbackListener.PROPERTY_AUTOREG_SERVICE_KEY, this.config.getProperty(SubscriptionCallbackListener.PROPERTY_AUTOREG_SERVICE_KEY));
                }
                if (this.config.containsKey(SubscriptionCallbackListener.PROPERTY_KEYDOMAIN)) {
                        cc.addProperty(SubscriptionCallbackListener.PROPERTY_KEYDOMAIN, this.config.getProperty(SubscriptionCallbackListener.PROPERTY_KEYDOMAIN));
                }
                if (this.config.containsKey(SubscriptionCallbackListener.PROPERTY_LISTENURL)) {
                        cc.addProperty(SubscriptionCallbackListener.PROPERTY_LISTENURL, this.config.getProperty(SubscriptionCallbackListener.PROPERTY_LISTENURL));
                }
                if (this.config.containsKey(SubscriptionCallbackListener.PROPERTY_NODE)) {
                        cc.addProperty(SubscriptionCallbackListener.PROPERTY_NODE, this.config.getProperty(SubscriptionCallbackListener.PROPERTY_NODE));
                }
                if (this.config.containsKey(SubscriptionCallbackListener.PROPERTY_SIGNATURE_BEHAVIOR)) {
                        cc.addProperty(SubscriptionCallbackListener.PROPERTY_SIGNATURE_BEHAVIOR, this.config.getProperty(SubscriptionCallbackListener.PROPERTY_SIGNATURE_BEHAVIOR));
                }
        }

        private void addDigitalSubscription(Configuration cc) throws Exception {
                Properties p = this.getDigitalSignatureConfiguration();
                Iterator<Map.Entry<Object, Object>> it = p.entrySet().iterator();
                while (it.hasNext()) {
                        Map.Entry<Object, Object> next = it.next();
                        String key = (String) next.getKey();
                        Object val = next.getValue();
                        if (val == null) {
                                continue;
                        }
                        if (key.equalsIgnoreCase(DigSigUtil.CANONICALIZATIONMETHOD)) {
                                cc.addProperty("client(0).signature.canonicalizationMethod", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.CHECK_TIMESTAMPS)) {
                                cc.addProperty("client(0).signature.checkTimestamps", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.CHECK_REVOCATION_STATUS_CRL)) {
                                cc.addProperty("client(0).signature.checkRevocationCRL", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.CHECK_REVOCATION_STATUS_OCSP)) {
                                cc.addProperty("client(0).signature.checkRevocationOCSP", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.CHECK_TRUST_CHAIN)) {
                                cc.addProperty("client(0).signature.checkTrust", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_FILE)) {
                                cc.addProperty("client(0).signature.signingKeyStorePath", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE)) {
                                cc.addProperty("client(0).signature.signingKeyStoreType", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD)) {
                                cc.addProperty("client(0).signature.signingKeyPassword", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_WAS_ENC)) {
                                cc.addProperty("client(0).signature.signingKeyPassword[@isPasswordEncrypted]", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_PROVIDER)) {
                                cc.addProperty("client(0).signature.signingKeyPassword[@cryptoProvider]", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_CIPHER)) {
                                cc.addProperty("client(0).signature.signingKeyPassword", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS)) {
                                cc.addProperty("client(0).signature.signingKeyAlias", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_METHOD)) {
                                cc.addProperty("client(0).signature.signatureMethod", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN)) {
                                cc.addProperty("client(0).signature.keyInfoInclusionSubjectDN", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64)) {
                                cc.addProperty("client(0).signature.keyInfoInclusionBase64PublicKey", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL)) {
                                cc.addProperty("client(0).signature.keyInfoInclusionSerial", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_OPTION_DIGEST_METHOD)) {
                                cc.addProperty("client(0).signature.digestMethod", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.TRUSTSTORE_FILE)) {
                                cc.addProperty("client(0).signature.trustStorePath", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.TRUSTSTORE_FILETYPE)) {
                                cc.addProperty("client(0).signature.trustStoreType", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD_WASENC)) {
                                cc.addProperty("client(0).signature.signingKeyStoreFilePassword[@isPasswordEncrypted]", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD_PROVIDER)) {
                                cc.addProperty("client(0).signature.signingKeyStoreFilePassword[@cryptoProvider]", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.TRUSTSTORE_FILE_PASSWORD_WASENC)) {
                                cc.addProperty("client(0).signature.trustStorePassword[@isPasswordEncrypted]", val);
                        } else if (key.equalsIgnoreCase(DigSigUtil.TRUSTSTORE_FILE_PASSWORD_PROVIDER)) {
                                cc.addProperty("client(0).signature.trustStorePassword[@cryptoProvider]", val);
                        }

                }

                if (p.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD_WASENC, "false").equalsIgnoreCase("true")) {
                        cc.addProperty("client(0).signature.signingKeyStoreFilePassword", p.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD_CIPHER));
                } else {
                        cc.addProperty("client(0).signature.signingKeyStoreFilePassword", p.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD));
                }

                if (p.getProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD_WASENC, "false").equalsIgnoreCase("true")) {
                        cc.addProperty("client(0).signature.trustStorePassword", p.getProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD_CIPHER));
                } else {
                        cc.addProperty("client(0).signature.trustStorePassword", p.getProperty(DigSigUtil.TRUSTSTORE_FILE_PASSWORD));
                }

                if (p.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_WAS_ENC, "false").equalsIgnoreCase("true")) {
                        cc.addProperty("client(0).signature.signingKeyPassword", p.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD_CIPHER));
                } else {
                        cc.addProperty("client(0).signature.signingKeyPassword", p.getProperty(DigSigUtil.SIGNATURE_KEYSTORE_KEY_PASSWORD));
                }

        }

        private void addXRegistration(Configuration cc) {

                cc.addProperty("client.XtoWsdl.IgnoreSSLErrors", isX_To_Wsdl_Ignore_SSL_Errors());
        }

}
