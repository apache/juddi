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
using org.uddi.apiv3;

namespace org.apache.juddi.v3.client
{
    /// <summary>
    /// This is the entry point for most functions provide by the juddi-client.
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class UDDIClient
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
     
        /// <summary>
        /// Registers services to UDDI using a clerk, and the uddi.xml configuration.
        /// For .NET users, the class names must be AssemblyQualifiedNames
        /// </summary>
        /// <pre>
        /// Type objType = typeof(System.Array);
        /// Console.WriteLine ("Qualified assembly name:\n   {0}.", objType.AssemblyQualifiedName.ToString()); 
        /// </pre>
        public void registerAnnotatedServices()
        {
            Dictionary<String, UDDIClerk> uddiClerks = clientConfig.getUDDIClerks();
            if (uddiClerks.Count > 0)
            {
                AnnotationProcessor ap = new AnnotationProcessor();
                Dictionary<string, UDDIClerk>.Enumerator it=uddiClerks.GetEnumerator();
                while (it.MoveNext()){
                    UDDIClerk c=it.Current.Value;
                    List<businessService> services = ap.readServiceAnnotations(
                            c.getClassWithAnnotations(),c.getUDDINode().getProperties());
                    foreach (businessService businessService in services) {
                        log.info("Node=" + c.getUDDINode().getApiNode().name);
                        c.register(businessService, c.getUDDINode().getApiNode());
                    }
                }
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
                AnnotationProcessor ap = new AnnotationProcessor();
                Dictionary<string, UDDIClerk>.Enumerator it = clerks.GetEnumerator();
                while (it.MoveNext())
                {
                    UDDIClerk c = it.Current.Value;
                    List<businessService> services = ap.readServiceAnnotations(
                            c.getClassWithAnnotations(), c.getUDDINode().getProperties());
                    foreach (businessService businessService in services)
                    {
                        c.unRegisterService(businessService.serviceKey, c.getUDDINode().getApiNode());
                    }
                }
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
                AnnotationProcessor ap = new AnnotationProcessor();
                Dictionary<string, UDDIClerk>.Enumerator it = clerks.GetEnumerator();
                while (it.MoveNext())
                {
                       UDDIClerk c = it.Current.Value;
                    List<businessService> services = ap.readServiceAnnotations(
                           c.getClassWithAnnotations(),c.getUDDINode().getProperties());
                    foreach (businessService businessService in services) {
                        if (businessService.bindingTemplates != null) {
                            foreach (bindingTemplate bindingTemplate in businessService.bindingTemplates) {
                                c.unRegisterBinding(bindingTemplate.bindingKey, c.getUDDINode().getApiNode());
                            }
                        }
                        if (removeServiceWithNoBindingTemplates) {
                            try {
                                businessService existingService = c.findService(businessService.serviceKey, c.getUDDINode().getApiNode());
                                if (existingService.bindingTemplates==null || existingService.bindingTemplates.Length==0) {
                                    c.unRegisterService(businessService.serviceKey,c.getUDDINode().getApiNode());
                                }
                            } catch (Exception e) {
                                log.error(e.Message,e);
                            }
                        }
                    }
                }
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


        /**
      * adds the typical SOAP tmodel references, but only if they aren't already present
      * @param bt
      * @return 
      */
        public static bindingTemplate addSOAPtModels(bindingTemplate bt)
        {
            bool found = false;
            List<object> cbags = new List<object>();
            if (bt.categoryBag != null)
                cbags.AddRange(bt.categoryBag.Items);

            for (int i = 0; i < cbags.Count; i++)
            {
                if (cbags[i] is keyedReference)
                {
                    keyedReference kr = (keyedReference)cbags[i];
                    if (kr.tModelKey!=null
                            && kr.tModelKey.Equals("uddi:uddi.org:categorization:types", StringComparison.CurrentCultureIgnoreCase))
                    {
                        if (kr.keyName != null
                                && kr.keyName.Equals("uddi-org:types:wsdl", StringComparison.CurrentCultureIgnoreCase))
                        {
                            found = true;
                        }
                    }
                }
            }
            if (!found)
                cbags.Add(new keyedReference("uddi:uddi.org:categorization:types", "uddi-org:types:wsdl", "wsdlDeployment"));
            if (cbags.Count > 0)
            {
                if (bt.categoryBag == null)
                    bt.categoryBag = new categoryBag();
                bt.categoryBag.Items = cbags.ToArray();
            }

            List<tModelInstanceInfo> data = new List<tModelInstanceInfo>();
            if (bt.tModelInstanceDetails != null)
            {
                data.AddRange(bt.tModelInstanceDetails);
            }
            accessPoint ap=null;
            if (bt.Item is accessPoint)
            {
                ap = (accessPoint)bt.Item;
            }
            tModelInstanceInfo tModelInstanceInfo;
            if (!Exists(data, UDDIConstants.PROTOCOL_SOAP))
            {
                tModelInstanceInfo = new tModelInstanceInfo();
                tModelInstanceInfo.tModelKey=(UDDIConstants.PROTOCOL_SOAP);
                data.Add(tModelInstanceInfo);
            }

            if (ap != null && ap.Value!=null && ap.Value.StartsWith("http:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_HTTP))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.TRANSPORT_HTTP);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("jms:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_JMS))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.TRANSPORT_JMS);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("rmi:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_RMI))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.TRANSPORT_RMI);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("udp:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_UDP))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.TRANSPORT_UDP);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("amqp:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_AMQP))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.TRANSPORT_AMQP);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("mailto:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_EMAIL))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.TRANSPORT_EMAIL);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("ftp:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_FTP))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.TRANSPORT_FTP);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("https:"))
            {
                if (!Exists(data, UDDIConstants.PROTOCOL_SSLv3))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.PROTOCOL_SSLv3);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("ftps:"))
            {
                if (!Exists(data, UDDIConstants.PROTOCOL_SSLv3))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.PROTOCOL_SSLv3);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("jndi:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_JNDI_RMI))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey=(UDDIConstants.TRANSPORT_JNDI_RMI);
                    data.Add(tModelInstanceInfo);
                }
            }
            bt.tModelInstanceDetails = data.ToArray();
            return bt;
        }

        /**
         * adds the typical REST tmodel references, but only if they aren't already present
         * @param bt
         * @return 
         */
        public static bindingTemplate addRESTtModels(bindingTemplate bt)
        {
            List<tModelInstanceInfo> data = new List<tModelInstanceInfo>();
            if (bt.tModelInstanceDetails != null)
            {
                data.AddRange(bt.tModelInstanceDetails);
            }
            accessPoint ap = null;
            if (bt.Item is accessPoint)
            {
                ap = (accessPoint)bt.Item;
            }
            tModelInstanceInfo tModelInstanceInfo;
            if (!Exists(data, UDDIConstants.PROTOCOL_REST))
            {
                tModelInstanceInfo = new tModelInstanceInfo();
                tModelInstanceInfo.tModelKey = (UDDIConstants.PROTOCOL_REST);
                data.Add(tModelInstanceInfo);
            }

            if (ap != null && ap.Value!=null && ap.Value.StartsWith("http:"))
            {
                if (!Exists(data, UDDIConstants.TRANSPORT_HTTP))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey = (UDDIConstants.TRANSPORT_HTTP);
                    data.Add(tModelInstanceInfo);
                }
            }
            if (ap != null && ap.Value!=null && ap.Value.StartsWith("https:"))
            {
                if (!Exists(data, UDDIConstants.PROTOCOL_SSLv3))
                {
                    tModelInstanceInfo = new tModelInstanceInfo();
                    tModelInstanceInfo.tModelKey = (UDDIConstants.PROTOCOL_SSLv3);
                    data.Add(tModelInstanceInfo);
                }
            }
            bt.tModelInstanceDetails = data.ToArray();
            return bt;
        }

        private static bool Exists(List<tModelInstanceInfo> items, String key)
        {
            for (int i = 0; i < items.Count; i++)
            {
                if (items[i].tModelKey != null
                        && items[i].tModelKey.Equals(key, StringComparison.CurrentCultureIgnoreCase))
                {
                    return true;
                }
            }
            return false;
        }
    }
}
