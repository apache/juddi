/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
using System.Collections.Generic;
using System.Text;
using org.apache.juddi.v3.client.config;
using System.Threading;
using org.apache.juddi.v3.client.transport;
using System.Configuration;

namespace org.apache.juddi.v3.client
{
    /// <summary>
    /// This is the entry point for most functions provide by the juddi-client.
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class UDDIClient: IDisposable
    {

        private static Log log = LogFactory.getLog(typeof(UDDIClient));
        private ClientConfig clientConfig = null;
        private String CONFIG_FILE = "uddi.xml";
        private Properties properties = null;

        public UDDIClient()
        {

            clientConfig = new ClientConfig(CONFIG_FILE, properties);
            UDDIClientContainer.addClient(this);
        }
        /**
         * Manages the clerks. Initiates reading the client configuration from the uddi.xml.
         * @throws ConfigurationException 
         */
        public UDDIClient(String configurationFile)
        {

            clientConfig = new ClientConfig(configurationFile);
            UDDIClientContainer.addClient(this);
        }
        /**
         * Manages the clerks. Initiates reading the client configuration from the uddi.xml.
         * @throws ConfigurationException 
         */
        public UDDIClient(String configurationFile, Properties properties)
        {

            clientConfig = new ClientConfig(configurationFile, properties);
            UDDIClientContainer.addClient(this);
        }
        /**
         * Stops the clerks.
         * @throws ConfigurationException 
         */
        public void stop()
        {
            log.info("Stopping UDDI Client " + clientConfig.getClientName());
            releaseResources();
            UDDIClientContainer.removeClerkManager(this.getName());

            //If running in embedded mode
            /*if (InVMTransport.class.getCanonicalName().equals(getClientConfig().getHomeNode().getProxyTransport())) {
                log.info("Shutting down embedded Server");
                stopEmbeddedServer();
            }*/
            log.info("UDDI Clerks shutdown completed for manager " + clientConfig.getClientName());
        }

        private void releaseResources()
        {
            if (this.clientConfig.isRegisterOnStartup())
            {
                this.unRegisterWSDLs();
                this.unRegisterBindingsOfAnnotatedServices(true);
            }
        }

       
        /**
         * Initializes the UDDI Clerk.
         * @throws ConfigurationException  
         */
        public void start()
        {

            if (UDDIClientContainer.addClient(this))
            {
                //If running in embedded mode
                /*if (InVMTransport.class.getCanonicalName().equals(getClientConfig().getHomeNode().getProxyTransport())) {
                    log.info("Starting embedded Server");
                    startEmbeddedServer();
                }*/
                if (this.clientConfig.isRegisterOnStartup())
                {
                    //Runnable runnable = new BackGroundRegistration(this);
                    Thread thread = new Thread(new ThreadStart(new BackGroundRegistration(this).run));
                    thread.Start();
                }
            }
        }

        protected void startEmbeddedServer()
        {

            /*try {
                String embeddedServerClass = getClientConfig().getHomeNode().getProperties().getProperty("embeddedServer","org.apache.juddi.v3.client.embed.JUDDIRegistry");
                Class<?> clazz =  ClassUtil.forName(embeddedServerClass, this.getClass());
                EmbeddedRegistry embeddedRegistry = (EmbeddedRegistry) clazz.newInstance();
                embeddedRegistry.start();
            } catch (Exception e) {
                throw new ConfigurationErrorsException(e.Message,e);
            }*/
        }

        protected void stopEmbeddedServer()
        {
            /*
            try {
                String embeddedServerClass = getClientConfig().getHomeNode().getProperties().getProperty("embeddedServer","org.apache.juddi.v3.client.embed.JUDDIRegistry");
                Class<?> clazz =  ClassUtil.forName(embeddedServerClass, this.getClass());
                EmbeddedRegistry embeddedRegistry = (EmbeddedRegistry) clazz.newInstance();
                embeddedRegistry.stop();
            } catch (Exception e) {
                throw new ConfigurationErrorsException(e.Message,e);
            }*/
        }

        public void restart()
        {
            stop();
            start();
        }

        /**
         * Saves the clerk and node info from the uddi.xml to the home jUDDI registry.
         * This info is needed if you want to JUDDI Server to do XRegistration/"replication".
         */
        public void saveClerkAndNodeInfo() {
		
		Dictionary<String,UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
		
		if (uddiClerks.Count > 0) {
			
			//obtaining a clerk that can write to the home registry
			UDDIClerk homeClerk=null;
			foreach (UDDIClerk clerk in uddiClerks.Values) {
				if (clerk.getUDDINode().isHomeJUDDI()) {
					homeClerk = clerk;
				}	
			}
			//registering nodes and clerks
			if (homeClerk!=null) {
				int numberOfHomeJUDDIs=0;
				foreach (UDDINode uddiNode in clientConfig.getUDDINodes().Values) {
					if (uddiNode.isHomeJUDDI()) numberOfHomeJUDDIs++;
					homeClerk.saveNode(uddiNode.getApiNode());
				}
				if (numberOfHomeJUDDIs==1) {
					foreach (UDDIClerk clerk in clientConfig.getUDDIClerks().Values) {
						//homeClerk.saveClerk(clerk);
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
        public void xRegister()
        {
            log.debug("Starting cross registration...");
            //XRegistration of listed businesses
            HashSet<XRegistration> xBusinessRegistrations = clientConfig.getXBusinessRegistrations();
            foreach (XRegistration xRegistration in xBusinessRegistrations)
            {
                xRegistration.xRegisterBusiness();
            }
            //XRegistration of listed serviceBindings
            HashSet<XRegistration> xServiceBindingRegistrations = clientConfig.getXServiceBindingRegistrations();
            foreach (XRegistration xRegistration in xServiceBindingRegistrations)
            {
                xRegistration.xRegisterServiceBinding();
            }
            log.debug("Cross registration completed");
        }
        /**
         * Registers services to UDDI using a clerk, and the uddi.xml
         * configuration.
         */
        public void registerAnnotatedServices()
        {
            Dictionary<String, UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
            if (uddiClerks.Count > 0)
            {
                /*AnnotationProcessor ap = new AnnotationProcessor();
                for (UDDIClerk uddiClerk : uddiClerks.values()) {
                    Collection<BusinessService> services = ap.readServiceAnnotations(
                            uddiClerk.getClassWithAnnotations(),uddiClerk.getUDDINode().getProperties());
                    for (BusinessService businessService : services) {
                        log.info("Node=" + uddiClerk.getUDDINode().getApiNode().getName());
                        uddiClerk.register(businessService, uddiClerk.getUDDINode().getApiNode());
                    }
                }*/
            }
        }
        /**
         * Removes the service and all of its bindingTemplates of the annotated classes.
         * @throws TransportException 
         * @throws RemoteException 
         */
        public void unRegisterAnnotatedServices()
        {
            Dictionary<String, UDDIClerk> clerks = clientConfig.getUDDIClerks();
            if (clerks.Count > 0)
            {
                /*AnnotationProcessor ap = new AnnotationProcessor();
                for (UDDIClerk clerk : clerks.values()) {
                    Collection<BusinessService> services = ap.readServiceAnnotations(
                            clerk.getClassWithAnnotations(),clerk.getUDDINode().getProperties());
                    for (BusinessService businessService : services) {
                        clerk.unRegisterService(businessService.getServiceKey(),clerk.getUDDINode().getApiNode());
                    }
                }*/
            }
        }

        /**
         * Removes the bindings of the services in the annotated classes. Multiple nodes may register
         * the same service using different BindingTempates. If the last BindingTemplate is removed
         * the service can be removed as well.
         * 
         * @param removeServiceWithNoBindingTemplates - if set to true it will remove the service if there
         * are no other BindingTemplates.
         */
        public void unRegisterBindingsOfAnnotatedServices(bool removeServiceWithNoBindingTemplates)
        {

            Dictionary<String, UDDIClerk> clerks = clientConfig.getUDDIClerks();
            if (clerks.Count > 0)
            {
                /*AnnotationProcessor ap = new AnnotationProcessor();
                for (UDDIClerk clerk : clerks.values()) {
                    Collection<BusinessService> services = ap.readServiceAnnotations(
                            clerk.getClassWithAnnotations(),clerk.getUDDINode().getProperties());
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
                                if (existingService.getBindingTemplates()==null || existingService.getBindingTemplates().getBindingTemplate().size()==0) {
                                    clerk.unRegisterService(businessService.getServiceKey(),clerk.getUDDINode().getApiNode());
                                }
                            } catch (Exception e) {
                                log.error(e.Message,e);
                            }
                        }
                    }
                }*/
            }

        }

        public ClientConfig getClientConfig()
        {
            return clientConfig;
        }

        public String getName()
        {
            return clientConfig.getClientName();
        }
        /**
         * @deprecated, use the getTransport(String nodeName) instead.
         * Returns the "default" jUDDI nodes Transport.
         * 
         * @return
         * @throws ConfigurationException
         */
        public Transport getTransport()
        {
            return getTransport("default");
        }
        /**
         * Returns the transport defined for the node with the given nodeName.
         * @param nodeName
         * @return
         * @throws ConfigurationException
         */
        public Transport getTransport(String nodeName)
        {
            try {
                String clazz = clientConfig.getHomeNode().getProxyTransport();
                String managerName = clientConfig.getClientName();
                return new AspNetTransport(managerName, nodeName, this.getClientConfig());
            } catch (Exception e) {
                throw new ConfigurationErrorsException(e.Message,e);
            }
        }

        public UDDIClerk getClerk(String clerkName)
        {
            return getClientConfig().getUDDIClerks()[(clerkName)];
        }

        /**
         * Registers services to UDDI using a clerk, and the uddi.xml
         * configuration.
         * @throws WSDLException 
         * @throws TransportException 
         * @throws ConfigurationException 
         * @throws RemoteException 
         */
        public void registerWSDLs()
        {
            Dictionary<String, UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
            Dictionary<String, UDDIClerk>.Enumerator it = uddiClerks.GetEnumerator();
            while (it.MoveNext())
            {
                it.Current.Value.registerWsdls();
            }

        }

        public void unRegisterWSDLs()
        {
            Dictionary<String, UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
            Dictionary<String, UDDIClerk>.Enumerator it = uddiClerks.GetEnumerator();
            while (it.MoveNext())
            {
                it.Current.Value.unRegisterWsdls();
            }
        }


       
    }
}
