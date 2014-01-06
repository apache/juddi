/*
 * Copyright 2001-2010 The Apache Software Foundation.
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

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.wsdl.WSDLException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.Release;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.embed.EmbeddedRegistry;
import org.apache.juddi.v3.client.mapping.ServiceLocator;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.transport.InVMTransport;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
/**
 * <p>The UDDIClient is the main entry point for using the jUDDI client. The UDDICLient
 * provides a simple way to get interact with a UDDI registry using the UDDI v3 API.</p>
 * 
 * <h3>Note:</h3>
 * <p>It is also possible to use the Java API for XML Registries (JAXR). Apache Scout is
 * an implementation of this API that can be configured to -behind the scenes- use the
 * jUDDI Client code to access either UDDIv2 or UDDIv3 registry. The advantage of using
 * JAXR is that your code can be configured to interact with any XML Registry (such as UDDI
 * or ebXML). The downside is that JAXR has not evolved beyond the 1.0 release and is
 * tightly coupled to the ebXML data structures, which being mapped to the UDDI data structures.
 * For more information on JAXR see the Apache Scout project, which is a sub project of Apache jUDDI.
 * If programmatic acess to a UDDIv3 registry is what you want, we recommend using the UDDIv3 
 * API with the UDDIClient.</p>
 * 
 * <p>The UDDIClient uses a XML formatted configuration file, which by default is loaded from the classpath
 * from location META-INF/uddi.xml.</p>
 * 
 * @author kstam
 *
 */
public class UDDIClient {

    private static Log log = LogFactory.getLog(UDDIClient.class);
    private ClientConfig clientConfig = null;
    private String CONFIG_FILE = "META-INF/uddi.xml";
    private Properties properties = null;
    private static Map<String, ServiceLocator> serviceLocators = new HashMap<String, ServiceLocator>();

    /**
     * Default constructor, loads from the default config, META-INF/uddi.xml
     *
     * @throws ConfigurationException
     */
    public UDDIClient() throws ConfigurationException {
        super();
        log.info("jUDDI Client version - " + Release.getjUDDIClientVersion());
        clientConfig = new ClientConfig(CONFIG_FILE, properties);
        UDDIClientContainer.addClient(this);
    }

    /**
     * Manages the clerks. Initiates reading the client configuration from the
     * uddi.xml.
     *
     * @throws ConfigurationException
     */
    public UDDIClient(String configurationFile) throws ConfigurationException {
        super();
        log.info("jUDDI Client version - " + Release.getjUDDIClientVersion());
        clientConfig = new ClientConfig(configurationFile);
        UDDIClientContainer.addClient(this);
    }

    /**
     * Manages the clerks. Initiates reading the client configuration from the
     * uddi.xml.
     *
     * @throws ConfigurationException
     */
    public UDDIClient(String configurationFile, Properties properties) throws ConfigurationException {
        super();
        log.info("jUDDI Client version - " + Release.getjUDDIClientVersion());
        clientConfig = new ClientConfig(configurationFile, properties);
        UDDIClientContainer.addClient(this);
    }

    /**
     * Uses the client config, and looks for a clerk called "default"
     *
     * @return
     * @throws ConfigurationException
     */
    public synchronized ServiceLocator getServiceLocator() throws ConfigurationException {
        return getServiceLocator(null);
    }

    /**
     * @param clerkName - if null defaults to "default"
     * @return
     * @throws ConfigurationException
     */
    public synchronized ServiceLocator getServiceLocator(String clerkName) throws ConfigurationException {
        UDDIClerk clerk = getClerk(clerkName);
        if (!serviceLocators.containsKey(clerk.getName())) {
            ServiceLocator serviceLocator = new ServiceLocator(clerk, new URLLocalizerDefaultImpl(), properties);
            serviceLocators.put(clerk.getName(), serviceLocator);
        }
        return serviceLocators.get(clerk.getName());
    }

    /**
     * Stops the clerks.
     *
     * @throws ConfigurationException
     */
    public void stop() throws ConfigurationException {
        log.info("Stopping UDDI Client " + clientConfig.getClientName());
        releaseResources();
        //fix for when someone runs UDDIClient.stop more than once
        if (UDDIClientContainer.contains(getName()))
            UDDIClientContainer.removeClerkManager(getName());

        //If running in embedded mode
        if (InVMTransport.class.getCanonicalName().equals(getClientConfig().getHomeNode().getProxyTransport())) {
            log.info("Shutting down embedded Server");
            stopEmbeddedServer();
        }
        log.info("UDDI Clerks shutdown completed for manager " + clientConfig.getClientName());
    }

    private void releaseResources() {
        if (this.clientConfig.isRegisterOnStartup()) {
            unRegisterWSDLs();
            unRegisterBindingsOfAnnotatedServices(true);
        }
    }

    /**
     * Initializes the UDDI Clerk.
     *
     * @throws ConfigurationException
     */
    public void start() throws ConfigurationException {

        if (UDDIClientContainer.addClient(this)) {
            //If running in embedded mode
            if (InVMTransport.class.getCanonicalName().equals(getClientConfig().getHomeNode().getProxyTransport())) {
                log.info("Starting embedded Server");
                startEmbeddedServer();
            }
            if (this.clientConfig.isRegisterOnStartup()) {
                Runnable runnable = new BackGroundRegistration(this);
                Thread thread = new Thread(runnable);
                thread.start();
            }
        }
    }

    protected void startEmbeddedServer() throws ConfigurationException {

        try {
            String embeddedServerClass = getClientConfig().getHomeNode().getProperties().getProperty("embeddedServer", "org.apache.juddi.v3.client.embed.JUDDIRegistry");
            Class<?> clazz = ClassUtil.forName(embeddedServerClass, this.getClass());
            EmbeddedRegistry embeddedRegistry = (EmbeddedRegistry) clazz.newInstance();
            embeddedRegistry.start();
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

    protected void stopEmbeddedServer() throws ConfigurationException {

        try {
            String embeddedServerClass = getClientConfig().getHomeNode().getProperties().getProperty("embeddedServer", "org.apache.juddi.v3.client.embed.JUDDIRegistry");
            Class<?> clazz = ClassUtil.forName(embeddedServerClass, this.getClass());
            EmbeddedRegistry embeddedRegistry = (EmbeddedRegistry) clazz.newInstance();
            embeddedRegistry.stop();
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

    public void restart() throws ConfigurationException {
        stop();
        start();
    }

    /**
     * Saves the clerk and node info from the uddi.xml to the home jUDDI
     * registry. This info is needed if you want to JUDDI Server to do
     * XRegistration/"replication".
     */
    public void saveClerkAndNodeInfo() {

        Map<String, UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();

        if (uddiClerks.size() > 0) {

            //obtaining a clerk that can write to the home registry
            UDDIClerk homeClerk = null;
            for (UDDIClerk clerk : uddiClerks.values()) {
                if (clerk.getUDDINode().isHomeJUDDI()) {
                    homeClerk = clerk;
                }
            }
            //registering nodes and clerks
            if (homeClerk != null) {
                int numberOfHomeJUDDIs = 0;
                for (UDDINode uddiNode : clientConfig.getUDDINodes().values()) {
                    if (uddiNode.isHomeJUDDI()) {
                        numberOfHomeJUDDIs++;
                    }
                    homeClerk.saveNode(uddiNode.getApiNode());
                }
                if (numberOfHomeJUDDIs == 1) {
                    for (UDDIClerk clerk : clientConfig.getUDDIClerks().values()) {
                        homeClerk.saveClerk(clerk);
                    }
                } else {
                    log.error("The client config needs to have one homeJUDDI node and found " + numberOfHomeJUDDIs);
                }
            } else {
                log.debug("No home clerk found.");
            }
        }
    }

    /**
     * X-Register services listed in the uddi.xml
     */
    public void xRegister() {
        log.debug("Starting cross registration...");
        //XRegistration of listed businesses
        Set<XRegistration> xBusinessRegistrations = clientConfig.getXBusinessRegistrations();
        for (XRegistration xRegistration : xBusinessRegistrations) {
            xRegistration.xRegisterBusiness();
        }
        //XRegistration of listed serviceBindings
        Set<XRegistration> xServiceBindingRegistrations = clientConfig.getXServiceBindingRegistrations();
        for (XRegistration xRegistration : xServiceBindingRegistrations) {
            xRegistration.xRegisterServiceBinding();
        }
        log.debug("Cross registration completed");
    }

    /**
     * Registers services to UDDI using a clerk, and the uddi.xml configuration.
     */
    public void registerAnnotatedServices() {
        Map<String, UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
        if (uddiClerks.size() > 0) {
            AnnotationProcessor ap = new AnnotationProcessor();
            for (UDDIClerk uddiClerk : uddiClerks.values()) {
                Collection<BusinessService> services = ap.readServiceAnnotations(
                        uddiClerk.getClassWithAnnotations(), uddiClerk.getUDDINode().getProperties());
                for (BusinessService businessService : services) {
                    log.info("Node=" + uddiClerk.getUDDINode().getApiNode().getName());
                    uddiClerk.register(businessService, uddiClerk.getUDDINode().getApiNode());
                }
            }
        }
    }

    /**
     * Removes the service and all of its bindingTemplates of the annotated
     * classes.
     *
     * @throws TransportException
     * @throws RemoteException
     */
    public void unRegisterAnnotatedServices() {
        Map<String, UDDIClerk> clerks = clientConfig.getUDDIClerks();
        if (clerks.size() > 0) {
            AnnotationProcessor ap = new AnnotationProcessor();
            for (UDDIClerk clerk : clerks.values()) {
                Collection<BusinessService> services = ap.readServiceAnnotations(
                        clerk.getClassWithAnnotations(), clerk.getUDDINode().getProperties());
                for (BusinessService businessService : services) {
                    clerk.unRegisterService(businessService.getServiceKey(), clerk.getUDDINode().getApiNode());
                }
            }
        }
    }

    /**
     * Removes the bindings of the services in the annotated classes. Multiple
     * nodes may register the same service using different BindingTempates. If
     * the last BindingTemplate is removed the service can be removed as well.
     *
     * @param removeServiceWithNoBindingTemplates - if set to true it will
     * remove the service if there are no other BindingTemplates.
     */
    public void unRegisterBindingsOfAnnotatedServices(boolean removeServiceWithNoBindingTemplates) {

        Map<String, UDDIClerk> clerks = clientConfig.getUDDIClerks();
        if (clerks.size() > 0) {
            AnnotationProcessor ap = new AnnotationProcessor();
            for (UDDIClerk clerk : clerks.values()) {
                Collection<BusinessService> services = ap.readServiceAnnotations(
                        clerk.getClassWithAnnotations(), clerk.getUDDINode().getProperties());
                for (BusinessService businessService : services) {
                    if (businessService.getBindingTemplates() != null) {
                        List<BindingTemplate> bindingTemplates = businessService.getBindingTemplates().getBindingTemplate();
                        for (BindingTemplate bindingTemplate : bindingTemplates) {
                            clerk.unRegisterBinding(bindingTemplate.getBindingKey(), clerk.getUDDINode().getApiNode());
                        }
                    }
                    if (removeServiceWithNoBindingTemplates) {
                        try {
                            BusinessService existingService = clerk.findService(businessService.getServiceKey(), clerk.getUDDINode().getApiNode());
                            if (existingService.getBindingTemplates() == null || existingService.getBindingTemplates().getBindingTemplate().size() == 0) {
                                clerk.unRegisterService(businessService.getServiceKey(), clerk.getUDDINode().getApiNode());
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }

    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }
    
    public String getName() {
        return clientConfig.getClientName();
    }
    
    public String getClientCallbackUrl() {
        return clientConfig.getClientCallbackUrl();
    }

    /**
     * Returns the transport defined for the node with the given name "default".
     * deprecated, use the getTransport(String nodeName) instead. Returns the
     * "default" jUDDI nodes Transport.
     * @deprecated, use the getTransport(String nodeName) instead. Returns the
     * "default" jUDDI nodes Transport.
     * Note: this will always return a new instance of Transport
     * @return
     * @throws ConfigurationException
     */
    public Transport getTransport() throws ConfigurationException {
        return getTransport("default");
    }

    /**
     * Returns the transport defined for the node with the given nodeName.
     * Note: this will always return a new instance of Transport
     * @param nodeName
     * @return
     * @throws ConfigurationException
     */
    public Transport getTransport(String nodeName) throws ConfigurationException {
        try {
            String clazz = clientConfig.getHomeNode().getProxyTransport();
            String managerName = clientConfig.getClientName();
            Class<?> transportClass = ClassUtil.forName(clazz, UDDIClient.class);
            if (transportClass != null) {
                Transport transport = (Transport) transportClass.getConstructor(String.class, String.class).newInstance(managerName, nodeName);
                return transport;
            } else {
                throw new ConfigurationException("ProxyTransport was not defined in the " + clientConfig.getConfigurationFile());
            }
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

    /**
     * Gets the UDDI Clerk, the entry point into many functions of the juddi
     * client
     *
     * @param clerkName - This references the uddi/client/clern@name of the
     * juddi client config file. it stores credentials if necessary and
     * associates it with a particular UDDI node (server/cluster) If not
     * specificed, the value of "default" will be used.
     * @return
     */
    public UDDIClerk getClerk(String clerkName) {
        if (clerkName == null || clerkName.length() == 0) {
            return getClientConfig().getUDDIClerks().get("default");
        }
        return getClientConfig().getUDDIClerks().get(clerkName);
    }

    /**
     * Registers services to UDDI using a clerk, and the uddi.xml configuration.
     *
     * @throws WSDLException
     * @throws TransportException
     * @throws ConfigurationException
     * @throws RemoteException
     */
    public void registerWSDLs() {
        Map<String, UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
        if (uddiClerks.size() > 0) {
            for (UDDIClerk uddiClerk : uddiClerks.values()) {
                uddiClerk.registerWsdls();
            }
        }
    }

    public void unRegisterWSDLs() {
        Map<String, UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
        if (uddiClerks.size() > 0) {
            for (UDDIClerk uddiClerk : uddiClerks.values()) {
                uddiClerk.unRegisterWsdls();
            }
        }
    }
    
    /**
     * adds the typical SOAP tmodel references, but only if they aren't already present
     * @param bt
     * @return 
     */
        public static BindingTemplate addSOAPtModels(BindingTemplate bt) {
                if (bt.getCategoryBag() == null) {
                        bt.setCategoryBag(new CategoryBag());
                }
                boolean found = false;
                for (int i = 0; i < bt.getCategoryBag().getKeyedReference().size(); i++) {
                        if (bt.getCategoryBag().getKeyedReference().get(i).getTModelKey() != null
                                && bt.getCategoryBag().getKeyedReference().get(i).getTModelKey().equalsIgnoreCase("uddi:uddi.org:categorization:types")) {
                                if (bt.getCategoryBag().getKeyedReference().get(i).getKeyName() != null
                                        && bt.getCategoryBag().getKeyedReference().get(i).getKeyName().equalsIgnoreCase("uddi-org:types:wsdl")) {
                                        found = true;
                                }
                        }
                }
                if (!found)
                         bt.getCategoryBag().getKeyedReference().add(new KeyedReference( "uddi:uddi.org:categorization:types","uddi-org:types:wsdl", "wsdlDeployment" ));
                if (bt.getCategoryBag().getKeyedReference().isEmpty() && bt.getCategoryBag().getKeyedReferenceGroup().isEmpty())
                        bt.setCategoryBag(null);
                if (bt.getTModelInstanceDetails() == null) {
                        bt.setTModelInstanceDetails(new TModelInstanceDetails());
                }
                TModelInstanceInfo tModelInstanceInfo;
                if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.PROTOCOL_SOAP)) {
                        tModelInstanceInfo = new TModelInstanceInfo();
                        tModelInstanceInfo.setTModelKey(UDDIConstants.PROTOCOL_SOAP);
                        bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                }

                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("http:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_HTTP)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_HTTP);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("jms:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_JMS)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_JMS);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("rmi:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_RMI)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_RMI);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("udp:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_UDP)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_UDP);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("amqp:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_AMQP)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_AMQP);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("mailto:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_EMAIL)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_EMAIL);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("ftp:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_FTP)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_FTP);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("https:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.PROTOCOL_SSLv3)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.PROTOCOL_SSLv3);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("ftps:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.PROTOCOL_SSLv3)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.PROTOCOL_SSLv3);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("jndi:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_JNDI_RMI)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_JNDI_RMI);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                return bt;
        }

        /**
         * adds the typical REST tmodel references, but only if they aren't already present
         * @param bt
         * @return 
         */
        public static BindingTemplate addRESTtModels(BindingTemplate bt) {
                if (bt.getTModelInstanceDetails() == null) {
                        bt.setTModelInstanceDetails(new TModelInstanceDetails());
                }
                TModelInstanceInfo tModelInstanceInfo;
                if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.PROTOCOL_REST)) {
                        tModelInstanceInfo = new TModelInstanceInfo();
                        tModelInstanceInfo.setTModelKey(UDDIConstants.PROTOCOL_REST);
                        bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                }

                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("http:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.TRANSPORT_HTTP)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.TRANSPORT_HTTP);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                if (bt.getAccessPoint() != null && bt.getAccessPoint().getValue().startsWith("https:")) {
                        if (!Exists(bt.getTModelInstanceDetails().getTModelInstanceInfo(), UDDIConstants.PROTOCOL_SSLv3)) {
                                tModelInstanceInfo = new TModelInstanceInfo();
                                tModelInstanceInfo.setTModelKey(UDDIConstants.PROTOCOL_SSLv3);
                                bt.getTModelInstanceDetails().getTModelInstanceInfo().add(tModelInstanceInfo);
                        }
                }
                return bt;
        }

        private static boolean Exists(List<TModelInstanceInfo> items, String key) {
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getTModelKey() != null
                                && items.get(i).getTModelKey().equalsIgnoreCase(key)) {
                                return true;
                        }
                }
                return false;
        }
}
