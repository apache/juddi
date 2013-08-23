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
using org.apache.juddi.apiv3;
using org.apache.juddi.v3.client.transport;
using System;


namespace org.apache.juddi.v3.client.config
{
    /// <summary>
    /// Represents a specific UDDI server, which is the set of services defined by the specification
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class UDDINode
    {
        private Properties properties;
        private Transport transport;

        private bool HomeJUDDI;
        private String name;
        private String clientName;
        private String description;
        private String custodyTransferUrl;
        private String inquiryUrl;
        private String publishUrl;
        private String securityUrl;
        private String subscriptionUrl;
        private String subscriptionListenerUrl;
        private String juddiApiUrl;
        private String proxyTransport;
        private String factoryInitial;
        private String factoryURLPkgs;
        private String factoryNamingProvider;


        public UDDINode()
        {

        }

        public UDDINode(node node)
        {

            name = node.name;
            clientName = node.clientName;
            description = node.description;
            custodyTransferUrl = node.custodyTransferUrl;
            inquiryUrl = node.inquiryUrl;
            publishUrl = node.publishUrl;
            securityUrl = node.securityUrl;
            subscriptionUrl = node.subscriptionUrl;
            subscriptionListenerUrl = node.subscriptionListenerUrl;
            juddiApiUrl = node.juddiApiUrl;
            proxyTransport = node.proxyTransport;
            factoryInitial = node.factoryInitial;
            factoryURLPkgs = node.factoryURLPkgs;
            factoryNamingProvider = node.factoryNamingProvider;
        }

        public Properties getProperties()
        {
            return properties;
        }

        public void setProperties(Properties properties)
        {
            this.properties = properties;
        }

        public node getApiNode()
        {
            node apiNode = new node();
            apiNode.custodyTransferUrl = (custodyTransferUrl);
            apiNode.description = (description);
            apiNode.factoryInitial = (factoryInitial);
            apiNode.factoryNamingProvider = (factoryNamingProvider);
            apiNode.factoryURLPkgs = (factoryURLPkgs);
            apiNode.inquiryUrl = (inquiryUrl);
            apiNode.juddiApiUrl = (juddiApiUrl);
            apiNode.clientName = (clientName);
            apiNode.name = (name);
            apiNode.proxyTransport = (proxyTransport);
            apiNode.publishUrl = (publishUrl);
            apiNode.securityUrl = (securityUrl);
            apiNode.subscriptionUrl = (subscriptionUrl);
            return apiNode;
        }


        public Transport getTransport()// throws TransportException 
        {
            if (transport == null)
            {
                transport = new AspNetTransport();

            }
            return transport;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getClientName()
        {
            return clientName;
        }

        public void setClientName(String clientName)
        {
            this.clientName = clientName;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getCustodyTransferUrl()
        {
            return custodyTransferUrl;
        }

        public void setCustodyTransferUrl(String custodyTransferUrl)
        {
            this.custodyTransferUrl = custodyTransferUrl;
        }

        public String getInquiryUrl()
        {
            return inquiryUrl;
        }

        public void setInquiryUrl(String inquiryUrl)
        {
            this.inquiryUrl = inquiryUrl;
        }

        public String getPublishUrl()
        {
            return publishUrl;
        }

        public void setPublishUrl(String publishUrl)
        {
            this.publishUrl = publishUrl;
        }

        public String getSecurityUrl()
        {
            return securityUrl;
        }

        public void setSecurityUrl(String securityUrl)
        {
            this.securityUrl = securityUrl;
        }

        public String getSubscriptionUrl()
        {
            return subscriptionUrl;
        }

        public void setSubscriptionUrl(String subscriptionUrl)
        {
            this.subscriptionUrl = subscriptionUrl;
        }

        public String getSubscriptionListenerUrl()
        {
            return subscriptionListenerUrl;
        }

        public void setSubscriptionListenerUrl(String subscriptionListenerUrl)
        {
            this.subscriptionListenerUrl = subscriptionListenerUrl;
        }

        public String getJuddiApiUrl()
        {
            return juddiApiUrl;
        }

        public void setJuddiApiUrl(String juddiApiUrl)
        {
            this.juddiApiUrl = juddiApiUrl;
        }

        public String getProxyTransport()
        {
            return proxyTransport;
        }

        public void setProxyTransport(String proxyTransport)
        {
            this.proxyTransport = proxyTransport;
        }

        public String getFactoryInitial()
        {
            return factoryInitial;
        }

        public void setFactoryInitial(String factoryInitial)
        {
            this.factoryInitial = factoryInitial;
        }

        public String getFactoryURLPkgs()
        {
            return factoryURLPkgs;
        }

        public void setFactoryURLPkgs(String factoryURLPkgs)
        {
            this.factoryURLPkgs = factoryURLPkgs;
        }

        public String getFactoryNamingProvider()
        {
            return factoryNamingProvider;
        }

        public void setFactoryNamingProvider(String factoryNamingProvider)
        {
            this.factoryNamingProvider = factoryNamingProvider;
        }

        public bool isHomeJUDDI()
        {
            return HomeJUDDI;
        }

        public void setHomeJUDDI(bool isHomeJUDDI)
        {
            this.HomeJUDDI = isHomeJUDDI;
        }


        public void setProperties(uddiClientNodeProperty[] uddiClientNodeProperty)
        {
            if (uddiClientNodeProperty == null)
                return;
            Properties p = new Properties();
            for (int i = 0; i < uddiClientNodeProperty.Length; i++)
            {
                p.setProperty(uddiClientNodeProperty[i].name, uddiClientNodeProperty[i].value);
            }
            this.properties = p;
        }
    }
}
