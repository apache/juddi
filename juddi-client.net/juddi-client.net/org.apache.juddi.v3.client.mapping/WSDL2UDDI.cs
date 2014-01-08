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

using org.apache.juddi.jaxb;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.log;
using org.uddi.apiv3;
using org.xmlsoap.schemas;
using org.xmlsoap.schemas.easyWsdl;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Configuration;
using System.IO;
using System.Net;
using System.Web.Services.Description;
using System.Xml;
using System.Xml.Serialization;

namespace org.apache.juddi.v3.client.mapping
{
    /// <summary>
    /// 
    /// This class implements the OASIS <a
    /// href="http://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-wsdl-v202-20040631.htm">
    /// 'Using WSDL in a UDDI Registry, Version 2.0.2'</a> technote. This class
    /// creates a detailed mapping of WSDL 1.1 artifacts to the UDDI V3 data model.
    /// <ul> <th>Section 2.4 in the technote</th> <li>2.4.1 wsdl:portType ->
    /// uddi:tModel - {@link #createWSDLPortTypeTModels(String, Map)}</li> <li>2.4.2
    /// wsdl:binding -> uddi:tModel -
    /// {@link #createWSDLBindingTModels(String, Map)}</li> <li>TODO: 2.4.3
    /// wsdl:service -> uddi:businessService</li> <li>TODO: 2.4.4 wsdl:port ->
    /// uddi:bindingTemplate</li> <li>TODO: 2.4.5 wsdl:port Address Extensions ->
    /// uddi:bindingTemplate</li> </ul>
    /// 
    /// </summary>
    /// @author Kurt T Stam
    /// @Since 3.1.5
    public class WSDL2UDDI
    {

        /// <summary>
        /// 
        /// Required Properties are: businessName, for example: &#39;Apache&#39; nodeName,
        /// for example: &#39;uddi.example.org_80&#39; keyDomain, for example:
        /// juddi.apache.org
        /// 
        /// Optional Properties are: lang: for example: &#39;nl&#39;
        /// 
        /// </summary>
        /// <param name="clerk">can be null if register/unregister methods are not used.</param>
        /// <param name="urlLocalizer">A reference to an custom</param>
        /// <param name="properties">required values keyDomain, businessKey, nodeName</param>
        /// <exception cref="ConfigurationException"></exception>
        public WSDL2UDDI(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties)
        {
            if (properties == null)
                throw new ArgumentNullException("properties");
            this.clerk = clerk;
            this.urlLocalizer = urlLocalizer;
            this.properties = properties;

            if (clerk != null)
            {
                if (!properties.containsKey("keyDomain"))
                {
                    throw new ConfigurationErrorsException("Property keyDomain is a required property when using WSDL2UDDI.");
                }
                if (!properties.containsKey("businessKey") && !properties.containsKey("businessName"))
                {
                    throw new ConfigurationErrorsException("Either property businessKey, or businessName, is a required property when using WSDL2UDDI.");
                }
                if (!properties.containsKey("nodeName"))
                {
                    if (properties.containsKey("serverName") && properties.containsKey("serverPort"))
                    {
                        String nodeName = properties.getProperty("serverName") + "_" + properties.getProperty("serverPort");
                        properties.setProperty("nodeName", nodeName);
                    }
                    else
                    {
                        throw new ConfigurationErrorsException("Property nodeName is not defined and is a required property when using WSDL2UDDI.");
                    }
                }
            }

            //Obtaining values from the properties
            this.keyDomainURI = "uddi:" + properties.getProperty("keyDomain") + ":";
            if (properties.containsKey(Property.BUSINESS_KEY))
            {
                this.businessKey = properties.getProperty(Property.BUSINESS_KEY);
            }
            else
            {
                //using the BusinessKey Template, and the businessName to construct the key 
                this.businessKey = UDDIKeyConvention.getBusinessKey(properties);
            }
            this.lang = properties.getProperty(Property.LANG, Property.DEFAULT_LANG);

        }


        public businessService[] registerBusinessServices(org.xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition)
        {
            List<businessService> businessServices = new List<businessService>();
            Dictionary<apache.juddi.v3.client.mapping.QName, org.xmlsoap.schemas.easyWsdl.tService>.Enumerator it = wsdlDefinition.getAllServices().GetEnumerator();
            while (it.MoveNext())
            {
                QName serviceQName = (QName)it.Current.Key;
                org.xmlsoap.schemas.easyWsdl.tService service = wsdlDefinition.getService(serviceQName);
                businessService businessService = null;
                //add service
                Uri serviceUrl = null;
                if (service.port != null && service.port.Count > 0)
                {
                    HashSet<org.xmlsoap.schemas.easyWsdl.tPort>.Enumerator it2 = service.port.GetEnumerator();
                    while (it2.MoveNext())
                    {
                        //for (Object portName : service.getPorts().keySet()) {
                        businessService = registerBusinessService(serviceQName, (String)it2.Current.name, serviceUrl, wsdlDefinition).getBusinessService();
                    }
                }
                if (businessService != null)
                {
                    businessServices.Add(businessService);
                }
            }

            return businessServices.ToArray();

        }
        public ServiceRegistrationResponse registerBusinessService(QName serviceQName, String portName, Uri serviceUrl,
            org.xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition)
        {


            String genericWSDLURL = serviceUrl.ToString();     //TODO maybe point to repository version
            ServiceRegistrationResponse response = new ServiceRegistrationResponse();
            String serviceKey = UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart());
            businessService businessService = lookupService(serviceKey);
            if (businessService == null)
            {
                List<tModel> tModels = new List<tModel>();
                // Create the PortType tModels
                Dictionary
                    <QName, org.xmlsoap.schemas.easyWsdl.tPortType> portTypes = (Dictionary<QName, org.xmlsoap.schemas.easyWsdl.tPortType>)wsdlDefinition.getAllPortTypes();

                tModels.AddRange(createWSDLPortTypeTModels(genericWSDLURL, portTypes));
                // Create the Binding tModels
                Dictionary<QName, org.xmlsoap.schemas.easyWsdl.tBinding> bindings = (Dictionary<QName, org.xmlsoap.schemas.easyWsdl.tBinding>)wsdlDefinition.getAllBindings();
                tModels.AddRange(createWSDLBindingTModels(genericWSDLURL, bindings));
                // Register these tModels
                foreach (tModel tModel in tModels)
                {
                    clerk.register(tModel);
                }
                // Service
                businessService = createBusinessService(serviceQName, wsdlDefinition);
                // Register this Service
                clerk.register(businessService);
            }
            //Add the BindingTemplate to this Service
            bindingTemplate binding = createWSDLBinding(serviceQName, portName, serviceUrl, wsdlDefinition);
            // Register BindingTemplate
            if (binding.Item != null)
            {
                clerk.register(binding);
                if (businessService.bindingTemplates == null)
                {
                    businessService.bindingTemplates = new bindingTemplate[] { binding };
                }
                else
                {
                    List<bindingTemplate> bts = new List<bindingTemplate>();

                    for (int i = 0; i < businessService.bindingTemplates.Length; i++)
                        bts.Add(businessService.bindingTemplates[i]);
                    bts.Add(binding);
                    businessService.bindingTemplates = bts.ToArray();
                }

                response.setBindingKey(binding.bindingKey);
            }
            response.setBusinessService(businessService);
            return response;


        }

        /// <summary>
        /// 
        /// Perform a lookup by serviceKey, and will return null if not found.
        /// 
        /// </summary>
        /// <param name="serviceKey"></param>
        /// <returns></returns>
        /// <exception cref="Exception"></exception>
        /// <exception cref="ConfigurationErrorsException"></exception>
        private businessService lookupService(string serviceKey)
        {
            //Checking if this serviceKey already exist
            businessService service = clerk.getServiceDetail(serviceKey);
            return service;
        }

        public String[] unRegisterBusinessServices(org.xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition)
        {
            String[] businessServices = new String[wsdlDefinition.getAllServices().Count];
            int i = 0;
            Dictionary<apache.juddi.v3.client.mapping.QName, org.xmlsoap.schemas.easyWsdl.tService>.Enumerator it = wsdlDefinition.getAllServices().GetEnumerator();
            while (it.MoveNext())
            {

                QName serviceQName = it.Current.Key;
                org.xmlsoap.schemas.easyWsdl.tService service = it.Current.Value;
                //unregister service
                Uri serviceUrl = null;
                if (service.port != null && service.port.Count > 0)
                {
                    HashSet<org.xmlsoap.schemas.easyWsdl.tPort>.Enumerator it2 = service.port.GetEnumerator();
                    while (it2.MoveNext())
                    {
                        //construct the accessURL
                        serviceUrl = new Uri(getBindingURL((org.xmlsoap.schemas.easyWsdl.tPort)service.getPort(it2.Current.name)));
                        businessServices[i++] = unRegisterBusinessService(serviceQName, (String)it2.Current.name, serviceUrl);
                    }
                }
            }
            return businessServices;
        }

        private string getBindingURL(xmlsoap.schemas.easyWsdl.tPort tPort)
        {
            String bindingUrl = null;
            if (tPort.Any != null)
            {
                HashSet<XmlElement>.Enumerator it = tPort.Any.GetEnumerator();
                while (it.MoveNext())
                {
                    String location = null;
                    if (it.Current.LocalName.Equals("address", StringComparison.CurrentCultureIgnoreCase) &&
                                   it.Current.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/soap/", StringComparison.CurrentCultureIgnoreCase))
                    {

                        location = it.Current.GetAttribute("location");
                    }
                    else
                        if (it.Current.LocalName.Equals("address", StringComparison.CurrentCultureIgnoreCase) &&
                        it.Current.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/http/", StringComparison.CurrentCultureIgnoreCase))
                        {
                            location = it.Current.GetAttribute("location");
                        }
                        else

                            if (it.Current.LocalName.Equals("address", StringComparison.CurrentCultureIgnoreCase) &&
                        it.Current.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/soap12/", StringComparison.CurrentCultureIgnoreCase))
                            {
                                location = it.Current.GetAttribute("location");
                            }

                    Uri locationURI = new Uri(location);
                    if (locationURI != null)
                    {
                        bindingUrl = urlLocalizer.rewrite(locationURI);
                        break;
                    }
                }
            }
            return bindingUrl;
        }
        public String unRegisterBusinessService(QName serviceName, String portName, Uri serviceUrl)
        {
            String serviceKey = UDDIKeyConvention.getServiceKey(properties, serviceName.getLocalPart());
            businessService service = lookupService(serviceKey);
            bool isRemoveServiceIfNoTemplates = true;
            String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceName, portName, serviceUrl);
            //check if this bindingKey is in the service's binding templates
            foreach (bindingTemplate bindingTemplate in service.bindingTemplates)
            {
                if (bindingKey.Equals(bindingTemplate.bindingKey))
                {
                    clerk.unRegisterBinding(bindingKey);
                    //if this is the last binding for this service, and 
                    if (service.bindingTemplates.Length == 1 && isRemoveServiceIfNoTemplates)
                    {
                        clerk.unRegisterService(serviceKey);
                        if (bindingTemplate.tModelInstanceDetails != null)
                        {
                            for (int i = 0; i < bindingTemplate.tModelInstanceDetails.Length; i++)
                            {
                                //foreach (tModelInstanceInfo tmi in bindingTemplate.tModelInstanceDetails)) {
                                String tModelKey = bindingTemplate.tModelInstanceDetails[i].tModelKey;
                                tModelDetail tModelDetail = clerk.getTModelDetail(tModelKey);
                                //delete all tModels assuming they are the portType and Binding tModels.
                                if (tModelDetail.tModel != null && tModelDetail.tModel.Length > 0)
                                {
                                    for (int k = 0; k < tModelDetail.tModel.Length; k++)
                                    {
                                        clerk.unRegisterTModel(tModelDetail.tModel[k].tModelKey);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return serviceKey;
        }
        public String getKeyDomainURI() { return keyDomainURI; }
        public void setKeyDomain(String keyDomainURI) { }
        public String getLang() { return lang; }
        String lang = "en";
        private UDDIClerk clerk;
        private URLLocalizer urlLocalizer;
        private Properties properties;
        private string keyDomainURI;
        private string businessKey;
        public void setLang(String lang)
        {
            this.lang = lang;
        }
        public String Lang
        {
            get
            {
                return lang;
            }
            set
            {
                lang = value;
            }
        }

        /// <summary>
        /// a simple convenience wrapper
        /// </summary>
        /// <param name="tModelKey"></param>
        /// <param name="keyName"></param>
        /// <param name="value"></param>
        /// <returns></returns>
        protected static keyedReference newKeyedReference(String tModelKey, String keyName, String value)
        {
            keyedReference typesReference = new keyedReference();
            typesReference.tModelKey = (tModelKey);
            typesReference.keyName = (keyName);
            typesReference.keyValue = (value);
            return typesReference;
        }
        /// <summary>
        /// 
        /// Builds a finder to find the binding tModels for a portType.
        /// 
        /// </summary>
        /// <param name="portType"></param>
        ///  <param name="ns">Namespace</param>
        /// <returns></returns>
        public static find_tModel createFindBindingTModelForPortType(String portType, String ns)
        {

            find_tModel findTModel = new find_tModel();
            categoryBag categoryBag = new categoryBag();
            List<keyedReference> items = new List<keyedReference>();
            if (ns != null && ns.Length != 0)
            {
                keyedReference namespaceReference = newKeyedReference(
                        "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", ns);
                items.Add(namespaceReference);
            }
            keyedReference bindingReference = newKeyedReference(
                    "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "binding");
            items.Add(bindingReference);

            keyedReference portTypeReference = newKeyedReference(
                    "uddi:uddi.org:wsdl:porttypereference", "uddi-org:wsdl:portTypeReference", portType);
            items.Add(portTypeReference);

            categoryBag.Items = (object[])items.ToArray();
            findTModel.categoryBag = (categoryBag);

            if (log.isDebugEnabled())
            {
                log.debug(new PrintUDDI<find_tModel>().print(findTModel));
            }
            return findTModel;
        }
        /// <summary>
        /// 
        /// Builds a finder to find the portType tModels for a portType.
        /// 
        /// </summary>
        /// <param name="portTypeName"></param>
        /// <param name="ns">Namespace</param>
        /// <returns></returns>
        public static find_tModel createFindPortTypeTModelForPortType(String portTypeName, String ns)
        {
            find_tModel findTModel = new find_tModel();
            categoryBag categoryBag = new categoryBag();
            List<keyedReference> cbitems = new List<keyedReference>();

            if (ns != null && ns.Length != 0)
            {
                keyedReference namespaceReference = newKeyedReference(
                        "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", ns);
                cbitems.Add(namespaceReference);
            }
            keyedReference bindingReference = newKeyedReference(
                    "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "binding");
            cbitems.Add(bindingReference);

            keyedReference portTypeReference = newKeyedReference(
                    "uddi:uddi.org:wsdl:porttypereference", "uddi-org:wsdl:portTypeReference", portTypeName);
            cbitems.Add(portTypeReference);
            categoryBag.Items = cbitems.ToArray();
            findTModel.categoryBag = categoryBag;

            if (log.isDebugEnabled())
            {
                log.debug(new PrintUDDI<find_tModel>().print(findTModel));
            }
            return findTModel;
        }



        /// <summary>
        /// 
        /// Creates a business service based off of a WSDL definition&lt;Br&gt;No changes are made to the UDDI
        /// endpoints using this method
        /// &lt;br&gt;
        /// Example Code:
        /// &lt;pre&gt;
        /// URL url = new URL(&quot;http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl&quot;);
        /// String domain = url.getHost();
        /// ReadWSDL rw = new ReadWSDL();
        /// Definition wsdlDefinition = rw.readWSDL(url);
        /// properties.put(&quot;keyDomain&quot;, domain);
        /// properties.put(&quot;businessName&quot;, domain);
        /// properties.put(&quot;serverName&quot;, url.getHost());
        /// properties.put(&quot;serverPort&quot;, url.getPort());
        /// wsdlURL = wsdlDefinition.getDocumentBaseURI();
        /// WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
        /// BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
        /// &lt;/pre&gt;
        /// </summary>
        /// <param name="wsdlDefinition">must not be null</param>
        /// <returns>a business service</returns>
        /// <exception cref="ArgumentNullException"> if the wsdlDefinition is null</exception>
        public businessService[] createBusinessServices(org.xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition)
        {
            if (wsdlDefinition == null)
            {
                throw new ArgumentNullException();
            }
            List<businessService> businessServices = new List<businessService>();

            Dictionary<apache.juddi.v3.client.mapping.QName, org.xmlsoap.schemas.easyWsdl.tService>.Enumerator it = wsdlDefinition.getAllServices().GetEnumerator();
            while (it.MoveNext())
            {
                QName serviceQName = it.Current.Key;
                org.xmlsoap.schemas.easyWsdl.tService service = it.Current.Value;
                businessService businessService = createBusinessService(serviceQName, wsdlDefinition);
                //service.getExtensibilityElements().
                //add the bindingTemplates
                Uri serviceUrl = null;
                if (service.port != null && service.port.Count > 0)
                {
                    List<bindingTemplate> bts = new List<bindingTemplate>();
                    HashSet<org.xmlsoap.schemas.easyWsdl.tPort>.Enumerator it2 = service.port.GetEnumerator();
                    while (it2.MoveNext())
                    {
                        bindingTemplate bindingTemplate = createWSDLBinding(serviceQName, (String)it2.Current.name, serviceUrl, wsdlDefinition);
                        bts.Add(bindingTemplate);
                    }
                    businessService.bindingTemplates = bts.ToArray();
                }
                businessServices.Add(businessService);
            }

            return businessServices.ToArray();
        }

        /// <summary>
        /// 
        /// Creates a UDDI Business Service.
        /// 
        /// </summary>
        /// <param name="serviceQName"></param>
        /// <param name="wsdlDefinition"></param>
        /// <returns></returns>
        private businessService createBusinessService(QName serviceQName, xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition)
        {

            log.debug("Constructing Service UDDI Information for " + serviceQName);
            businessService service = new businessService();
            // BusinessKey
            service.businessKey = (businessKey);
            // ServiceKey
            service.serviceKey = (UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart()));
            // Description
            String serviceDescription = "";
            // Override with the service description from the WSDL if present
            org.xmlsoap.schemas.easyWsdl.tService svc = wsdlDefinition.getService(serviceQName);
            if (svc != null && svc.documentation != null)
            {
                HashSet<XmlNode>.Enumerator it = svc.documentation.Any.GetEnumerator();
                while (it.MoveNext())
                {

                    if (it.Current.Value != null)
                    {
                        serviceDescription += it.Current.Value;
                    }
                }

            }
            if (String.IsNullOrEmpty(serviceDescription))
            {
                serviceDescription = properties.getProperty(Property.SERVICE_DESCRIPTION, Property.DEFAULT_SERVICE_DESCRIPTION);
            }
            description description = new description();
            description.lang = (lang);
            description.Value = (serviceDescription);
            service.description = new description[] { (description) };
            // Service name
            name sName = new name();
            sName.lang = (lang);
            sName.Value = (serviceQName.getLocalPart());
            service.name = new name[] { sName };

            categoryBag categoryBag = new categoryBag();

            String ns = serviceQName.getNamespaceURI();
            List<keyedReference> cbitems = new List<keyedReference>();
            if (ns != null && ns != "")
            {
                keyedReference namespaceReference = newKeyedReference(
                        "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", ns);
                cbitems.Add(namespaceReference);
            }

            keyedReference serviceReference = newKeyedReference(
                    "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "service");
            cbitems.Add(serviceReference);

            keyedReference localNameReference = newKeyedReference(
                    "uddi:uddi.org:xml:localname", "uddi-org:xml:localName", serviceQName.getLocalPart());
            cbitems.Add(localNameReference);
            categoryBag.Items = (object[])cbitems.ToArray();
            service.categoryBag = categoryBag;

            return service;
        }

        private bindingTemplate createWSDLBinding(QName serviceQName, string portName, Uri serviceUrl, xmlsoap.schemas.easyWsdl.tDefinitions wsdlDefinition)
        {
            bindingTemplate bindingTemplate = new bindingTemplate();
            // Set BusinessService Key
            bindingTemplate.serviceKey = (UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart()));

            if (serviceUrl != null)
            {
                // Set AccessPoint
                accessPoint accessPoint = new accessPoint();
                accessPoint.useType = (AccessPointType.endPoint.ToString());
                accessPoint.Value = (urlLocalizer.rewrite(serviceUrl));
                bindingTemplate.Item = (accessPoint);
                // Set Binding Key
                String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceQName, portName, serviceUrl);
                bindingTemplate.bindingKey = (bindingKey);
            }

            org.xmlsoap.schemas.easyWsdl.tService service = wsdlDefinition.getService(serviceQName);
            if (service != null)
            {
                List<tModelInstanceInfo> tii = new List<tModelInstanceInfo>();


                org.xmlsoap.schemas.easyWsdl.tPort port = service.getPort(portName);
                if (port != null)
                {
                    if (serviceUrl == null)
                    {
                        if (port.Any != null)
                        {
                            HashSet<XmlElement>.Enumerator it = port.Any.GetEnumerator();
                            while (it.MoveNext())
                            {
                                String location = null;
                                if (it.Current.LocalName.Equals("address", StringComparison.CurrentCultureIgnoreCase) &&
                                    it.Current.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/soap/", StringComparison.CurrentCultureIgnoreCase))
                                {

                                    location = it.Current.GetAttribute("location");
                                }
                                else
                                    if (it.Current.LocalName.Equals("address", StringComparison.CurrentCultureIgnoreCase) &&
                                    it.Current.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/http/", StringComparison.CurrentCultureIgnoreCase))
                                    {
                                        location = it.Current.GetAttribute("location");
                                    }
                                    else

                                        if (it.Current.LocalName.Equals("address", StringComparison.CurrentCultureIgnoreCase) &&
                                    it.Current.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/soap12/", StringComparison.CurrentCultureIgnoreCase))
                                        {
                                            location = it.Current.GetAttribute("location");
                                        }
                                if (location != null)
                                {
                                    try
                                    {
                                        Uri locationURI = new Uri(location);
                                        accessPoint accessPoint = new accessPoint();
                                        accessPoint.useType = (AccessPointType.endPoint.ToString());
                                        accessPoint.Value = (urlLocalizer.rewrite(locationURI));
                                        bindingTemplate.Item = (accessPoint);
                                        // Set Binding Key
                                        String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceQName, portName, locationURI);
                                        bindingTemplate.bindingKey = (bindingKey);
                                        break;
                                    }
                                    catch (Exception e)
                                    {
                                        log.error("", e);
                                    }
                                }
                            }
                        }

                    }
                    XmlQualifiedName bx = port.binding;
                    org.xmlsoap.schemas.easyWsdl.tBinding bindingelement = wsdlDefinition.getBinding(bx);
                    // Set the Binding Description
                    String bindingDescription = "";
                    // Override with the service description from the WSDL if present

                    if (bindingelement != null && bindingelement.documentation != null
                        && bindingelement.documentation.Any != null)
                    {
                        HashSet<XmlNode>.Enumerator it = bindingelement.documentation.Any.GetEnumerator();
                        while (it.MoveNext())
                        {
                            bindingDescription += it.Current.Value;
                        }
                    }
                    if (String.IsNullOrEmpty(bindingDescription))
                        bindingDescription = properties.getProperty(Property.BINDING_DESCRIPTION, Property.DEFAULT_BINDING_DESCRIPTION); ;

                    description description = new description();
                    description.lang = (lang);
                    description.Value = (bindingDescription);
                    bindingTemplate.description = new description[] { (description) };


                    // reference wsdl:binding tModel
                    tModelInstanceInfo tModelInstanceInfoBinding = new tModelInstanceInfo();
                    tModelInstanceInfoBinding.tModelKey = (keyDomainURI + bindingelement.name);
                    instanceDetails instanceDetails = new instanceDetails();
                    instanceDetails.instanceParms = (portName);
                    tModelInstanceInfoBinding.instanceDetails = (instanceDetails);
                    description descriptionB = new description();
                    descriptionB.lang = (lang);
                    descriptionB.Value = ("The wsdl:binding that this wsdl:port implements. " + bindingDescription
                            + " The instanceParms specifies the port local name.");
                    tModelInstanceInfoBinding.description = new uddi.apiv3.description[] { description };
                    tii.Add(tModelInstanceInfoBinding);


                    // reference wsdl:portType tModel
                    org.xmlsoap.schemas.easyWsdl.tPortType portType = wsdlDefinition.getPortType(bindingelement.type);
                    tModelInstanceInfo tModelInstanceInfoPortType = new tModelInstanceInfo();
                    tModelInstanceInfoPortType.tModelKey = (keyDomainURI + portType.name);
                    String portTypeDescription = "";
                    if (portType.documentation != null && portType.documentation.Any != null)
                    {
                        HashSet<XmlNode>.Enumerator it = portType.documentation.Any.GetEnumerator();
                        while (it.MoveNext())
                        {
                            portTypeDescription += it.Current.Value;
                        }
                    }
                    description descriptionPT = new description();
                    descriptionPT.lang = (lang);
                    descriptionPT.Value = ("The wsdl:portType that this wsdl:port implements." + portTypeDescription);
                    tModelInstanceInfoPortType.description = new description[] { (descriptionPT) };
                    tii.Add(tModelInstanceInfoPortType);

                    bindingTemplate.tModelInstanceDetails = tii.ToArray();
                }
                else
                {
                    log.error("Could not find Port with portName: " + portName);
                }
            }
            else
            {
                log.error("Could not find Service with serviceName: " + serviceQName.getLocalPart());
            }

            return bindingTemplate;
        }

        /// <summary>
        /// 
        /// &lt;h3&gt;2.4.1 wsdl:portType -&gt; uddi:tModel&lt;/h3&gt;
        /// 
        /// &lt;p&gt;A wsdl:portType MUST be modeled as a uddi:tModel.&lt;/p&gt;
        /// 
        /// &lt;p&gt;The minimum information that must be captured about a portType is its
        /// entity type, its local name, its namespace, and the location of the WSDL
        /// document that defines the portType. Capturing the entity type enables
        /// users to search for tModels that represent portType artifacts. Capturing
        /// the local name, namespace, and WSDL location enables users to locate the
        /// definition of the specified portType artifact.&lt;/p&gt;
        /// 
        /// &lt;p&gt;The wsdl:portType information is captured as follows:&lt;/p&gt;
        /// 
        /// &lt;p&gt;The uddi:name element of the tModel MUST be the value of the name
        /// attribute of the wsdl:portType.&lt;/p&gt;
        /// 
        /// &lt;p&gt;The tModel MUST contain a categoryBag, and the categoryBag MUST
        /// contain a keyedReference with a tModelKey of the WSDL Entity Type
        /// category system and a keyValue of &quot;portType&quot;.&lt;/p&gt;
        /// 
        /// &lt;p&gt;If the wsdl:portType has a targetNamespace then the categoryBag MUST
        /// also contain an additional keyedReference with a tModelKey of the XML
        /// Namespace category system and a keyValue of the target namespace of the
        /// wsdl:definitions element that contains the wsdl:portType. If the
        /// targetNamespace is absent from the portType, a categoryBag MUST NOT
        /// contain a keyedReference to the XML Namespace category system.&lt;/p&gt;
        /// 
        /// &lt;p&gt;The tModel MUST contain an overviewDoc with an overviewURL containing
        /// the location of the WSDL document that describes the wsdl:portType.&lt;/p&gt;
        /// Example Code
        /// &lt;pre&gt;
        /// URL url = new URL(&quot;http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl&quot;);
        /// String domain = url.getHost();
        /// ReadWSDL rw = new ReadWSDL();
        /// Definition wsdlDefinition = rw.readWSDL(url);
        /// properties.put(&quot;keyDomain&quot;, domain);
        /// properties.put(&quot;businessName&quot;, domain);
        /// properties.put(&quot;serverName&quot;, url.getHost());
        /// properties.put(&quot;serverPort&quot;, url.getPort());
        /// wsdlURL = wsdlDefinition.getDocumentBaseURI();
        /// WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
        /// Map&lt;QName, PortType&gt; portTypes = (Map&lt;QName, PortType&gt;) wsdlDefinition.getAllPortTypes();
        /// Set&lt;TModel&gt; portTypeTModels = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
        /// &lt;/pre&gt;
        /// 
        /// </summary>
        /// <param name="wsdlURL">This is used to set the Overview URL</param>
        /// <param name="portTypes">Map</param>
        /// <returns>set of WSDL PortType tModels</returns>
        /// <exception cref="Exception"></exception>

        public List<tModel> createWSDLPortTypeTModels(string wsdlURL, Dictionary<QName, xmlsoap.schemas.easyWsdl.tPortType> portTypes)
        {
            List<tModel> tModels = new List<tModel>();
            Dictionary<QName, xmlsoap.schemas.easyWsdl.tPortType>.Enumerator it = portTypes.GetEnumerator();
            while (it.MoveNext())
            {
                QName qName = it.Current.Key;
                tModel tModel = new tModel();
                String localpart = qName.getLocalPart();
                String ns = qName.getNamespaceURI();
                // Set the Key
                tModel.tModelKey = (keyDomainURI + localpart);

                // Set the Name. The uddi:name element of the tModel MUST be the value of
                // the name attribute of the wsdl:portType.
                name name = new name();
                name.lang = (lang);
                name.Value = (localpart);
                tModel.name = (name);
                // Set the OverviewURL. The tModel MUST contain an overviewDoc with an 
                // overviewURL containing the location of the WSDL document that 
                // describes the wsdl:portType.
                overviewURL overviewURL = new overviewURL();
                overviewURL.useType = (AccessPointType.wsdlDeployment.ToString());
                overviewURL.Value = (wsdlURL);
                overviewDoc overviewDoc = new overviewDoc();
                overviewDoc.overviewURLs = new overviewURL[] { (overviewURL) };
                tModel.overviewDoc = new overviewDoc[] { (overviewDoc) };
                // Create the categoryBag, The tModel MUST contain a categoryBag
                categoryBag categoryBag = new categoryBag();

                List<keyedReference> cbitems = new List<keyedReference>();
                // the categoryBag MUST contain a keyedReference with a tModelKey of the WSDL 
                // Entity Type category system and a keyValue of "portType".
                keyedReference typesReference = newKeyedReference(
                        "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "portType");
                cbitems.Add(typesReference);

                // If the wsdl:portType has a targetNamespace then the categoryBag MUST also contain an 
                // additional keyedReference with a tModelKey of the XML Namespace category system and a 
                // keyValue of the target namespace of the wsdl:definitions element that contains the 
                // wsdl:portType. If the targetNamespace is absent from the portType, a categoryBag 
                // MUST NOT contain a keyedReference to the XML Namespace category system.
                if (ns != null && !"".Equals(ns))
                {
                    keyedReference namespaceReference = newKeyedReference(
                            "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", ns);
                    cbitems.Add(namespaceReference);
                }

                categoryBag.Items = (object[])cbitems.ToArray();
                tModel.categoryBag = categoryBag;

                tModels.Add(tModel);


            }
            return tModels;

        }

        static Log log = LogFactory.getLog(typeof(WSDL2UDDI));

        /// <summary>
        /// 
        /// &lt;h3&gt;2.4.1 wsdl:portType -&gt; uddi:tModel&lt;/h3&gt;
        /// 
        /// &lt;p&gt;A wsdl:portType MUST be modeled as a uddi:tModel.&lt;/p&gt;
        /// 
        /// &lt;p&gt;The minimum information that must be captured about a portType is its
        /// entity type, its local name, its namespace, and the location of the WSDL
        /// document that defines the portType. Capturing the entity type enables
        /// users to search for tModels that represent portType artifacts. Capturing
        /// the local name, namespace, and WSDL location enables users to locate the
        /// definition of the specified portType artifact.&lt;/p&gt;
        /// 
        /// &lt;p&gt;The wsdl:portType information is captured as follows:&lt;/p&gt;
        /// 
        /// &lt;p&gt;The uddi:name element of the tModel MUST be the value of the name
        /// attribute of the wsdl:portType.&lt;/p&gt;
        /// 
        /// &lt;p&gt;The tModel MUST contain a categoryBag, and the categoryBag MUST
        /// contain a keyedReference with a tModelKey of the WSDL Entity Type
        /// category system and a keyValue of &quot;portType&quot;.&lt;/p&gt;
        /// 
        /// &lt;p&gt;If the wsdl:portType has a targetNamespace then the categoryBag MUST
        /// also contain an additional keyedReference with a tModelKey of the XML
        /// Namespace category system and a keyValue of the target namespace of the
        /// wsdl:definitions element that contains the wsdl:portType. If the
        /// targetNamespace is absent from the portType, a categoryBag MUST NOT
        /// contain a keyedReference to the XML Namespace category system.&lt;/p&gt;
        /// 
        /// &lt;p&gt;The tModel MUST contain an overviewDoc with an overviewURL containing
        /// the location of the WSDL document that describes the wsdl:portType.&lt;/p&gt;
        /// Example Code
        /// &lt;pre&gt;
        /// URL url = new URL(&quot;http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl&quot;);
        /// String domain = url.getHost();
        /// ReadWSDL rw = new ReadWSDL();
        /// Definition wsdlDefinition = rw.readWSDL(url);
        /// properties.put(&quot;keyDomain&quot;, domain);
        /// properties.put(&quot;businessName&quot;, domain);
        /// properties.put(&quot;serverName&quot;, url.getHost());
        /// properties.put(&quot;serverPort&quot;, url.getPort());
        /// wsdlURL = wsdlDefinition.getDocumentBaseURI();
        /// WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
        /// Map&lt;QName, PortType&gt; portTypes = (Map&lt;QName, PortType&gt;) wsdlDefinition.getAllPortTypes();
        /// Set&lt;TModel&gt; portTypeTModels = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
        /// &lt;/pre&gt;
        /// 
        /// </summary>
        /// <param name="wsdlURL">This is used to set the Overview URL</param>
        /// <param name="portType">Map</param>
        /// @return set of WSDL PortType tModels
        /// <exception cref="Exception"></exception>

        public List<tModel> createWSDLBindingTModels(string wsdlURL, Dictionary<QName, xmlsoap.schemas.easyWsdl.tBinding> bindings)
        {
            List<tModel> tModels = new List<tModel>();
            Dictionary<QName, xmlsoap.schemas.easyWsdl.tBinding>.Enumerator it = bindings.GetEnumerator();
            while (it.MoveNext())
            {
                QName qName = it.Current.Key;
                String localpart = qName.getLocalPart();
                String ns = qName.getNamespaceURI();
                // Build the tModel
                tModel tModel = new tModel();
                // Set the Key
                tModel.tModelKey = (keyDomainURI + localpart);
                // Set the Name
                name name = new name();
                name.lang = (lang);
                name.Value = (localpart);
                tModel.name = (name);
                // Set the OverviewURL
                overviewURL overviewURL = new overviewURL();
                overviewURL.useType = (AccessPointType.wsdlDeployment.ToString());
                overviewURL.Value = (wsdlURL);
                overviewDoc overviewDoc = new overviewDoc();
                overviewDoc.overviewURLs = new overviewURL[] { (overviewURL) };
                tModel.overviewDoc = new overviewDoc[] { (overviewDoc) };
                // Set the categoryBag
                categoryBag categoryBag = new categoryBag();

                List<keyedReference> cbitems = new List<keyedReference>();
                if (ns != null && !"".Equals(ns))
                {
                    // A keyedReference with a tModelKey of the WSDL Entity Type category system and a keyValue of "binding".
                    keyedReference namespaceReference = newKeyedReference(
                            "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", ns);
                    cbitems.Add(namespaceReference);
                }

                // A keyedReference with a tModelKey of the WSDL Entity Type category system and a keyValue of "binding".
                keyedReference typesReference = newKeyedReference(
                        "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "binding");
                cbitems.Add(typesReference);

                // A keyedReference with a tModelKey of the WSDL portType Reference category system and a keyValue 
                // of the tModelKey that models the wsdl:portType to which the wsdl:binding relates.

                org.xmlsoap.schemas.easyWsdl.tBinding binding = bindings[(qName)];
                String portTypeKey = keyDomainURI + binding.type.Name;
                keyedReference namespaceReference2 = newKeyedReference(
                        "uddi:uddi.org:wsdl:porttypereference", "uddi-org:wsdl:portTypeReference",
                        portTypeKey);
                cbitems.Add(namespaceReference2);

                //  A keyedReference with a tModelKey of the UDDI Types category system and a keyValue of 
                // "wsdlSpec" for backward compatibility.
                keyedReference typesReferenceBackwardsCompatible = newKeyedReference(
                        "uddi:uddi.org:categorization:types", "uddi-org:types", "wsdlSpec");
                cbitems.Add(typesReferenceBackwardsCompatible);


                // One or two keyedReferences as required to capture the protocol
                foreach (XmlElement xe in binding.Any)
                {

                    if (xe.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/soap/", StringComparison.CurrentCultureIgnoreCase)
                        && xe.LocalName.Equals("binding", StringComparison.CurrentCultureIgnoreCase))
                    {

                        // If the wsdl:binding contains a soap:binding extensibility element from the 
                        // 'http://schemas.xmlsoap.org/wsdl/soap/' namespace then the categoryBag MUST 
                        //include a keyedReference with a tModelKey of the Protocol Categorization 
                        // category system and a keyValue of the tModelKey of the SOAP Protocol tModel.

                        keyedReference soapProtocol = newKeyedReference(
                                "uddi:uddi.org:wsdl:categorization:protocol", "uddi-org:protocol:soap", "uddi:uddi.org:protocol:soap");
                        cbitems.Add(soapProtocol);
                        // If the value of the transport attribute of the soap:binding element 
                        // is 'http://schemas.xmlsoap.org/soap/http' then the categoryBag MUST 
                        // include a keyedReference with a tModelKey of the Transport Categorization 
                        // category system and a keyValue of the tModelKey of the HTTP Transport tModel.


                        if (String.IsNullOrEmpty(xe.GetAttribute("transport")))
                        {
                            // TODO If the value of the transport attribute is anything else, 
                            // then the bindingTemplate MUST include an additional keyedReference with a tModelKey 
                            // of the Transport Categorization category system and a keyValue of the tModelKey of 
                            // an appropriate transport tModel.
                            log.warn("empty soap transport for binding " + it.Current.Key.getLocalPart() + " " + it.Current.Key.getNamespaceURI());
                        }
                        else
                        {
                            String attr = xe.GetAttribute("transport");

                            if (attr != null && attr.Equals("http://schemas.xmlsoap.org/soap/http"))
                            {
                                keyedReference httpTransport = newKeyedReference(
                                       "uddi:uddi.org:wsdl:categorization:transport", "uddi-org:http", "uddi:uddi.org:transport:http");
                                cbitems.Add(httpTransport);
                            }
                            else
                            {
                                log.warn("i don't know how to process the soap transport value of " + xe.GetAttribute("transport", "http://schemas.xmlsoap.org/wsdl/soap/"));
                            }
                        }
                    }
                    else if (xe.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/http/", StringComparison.CurrentCultureIgnoreCase)
                      && xe.LocalName.Equals("binding", StringComparison.CurrentCultureIgnoreCase))
                    {

                        // If the wsdl:binding contains an http:binding extensibility element from the 
                        // http://schemas.xmlsoap.org/wsdl/http/ namespace then the categoryBag MUST 
                        // include a keyedReference with a tModelKey of the Protocol Categorization 
                        // category system and a keyValue of the tModelKey of the HTTP Protocol tModel.
                        keyedReference soapProtocol = newKeyedReference(
                                "uddi:uddi.org:wsdl:categorization:protocol", "uddi-org:protocol:http", "uddi:uddi.org:protocol:http");
                        cbitems.Add(soapProtocol);
                    }
                    else if (xe.NamespaceURI.Equals("http://schemas.xmlsoap.org/wsdl/soap12/", StringComparison.CurrentCultureIgnoreCase)
                        && xe.LocalName.Equals("binding", StringComparison.CurrentCultureIgnoreCase))
                    {
                        // If the wsdl:binding contains a soap:binding extensibility element from the 
                        // 'http://schemas.xmlsoap.org/wsdl/soap/' namespace then the categoryBag MUST 
                        //include a keyedReference with a tModelKey of the Protocol Categorization 
                        // category system and a keyValue of the tModelKey of the SOAP Protocol tModel.

                        keyedReference soapProtocol = newKeyedReference(
                                "uddi:uddi.org:wsdl:categorization:protocol", "uddi-org:protocol:soap", "uddi:uddi.org:protocol:soap");
                        cbitems.Add(soapProtocol);
                        // If the value of the transport attribute of the soap:binding element 
                        // is 'http://schemas.xmlsoap.org/soap/http' then the categoryBag MUST 
                        // include a keyedReference with a tModelKey of the Transport Categorization 
                        // category system and a keyValue of the tModelKey of the HTTP Transport tModel.


                        if (String.IsNullOrEmpty(xe.GetAttribute("transport")))
                        {
                            // TODO If the value of the transport attribute is anything else, 
                            // then the bindingTemplate MUST include an additional keyedReference with a tModelKey 
                            // of the Transport Categorization category system and a keyValue of the tModelKey of 
                            // an appropriate transport tModel.
                            log.warn("empty soap transport for binding " + it.Current.Key.getLocalPart() + " " + it.Current.Key.getNamespaceURI());
                        }
                        else
                        {
                            String attr = xe.GetAttribute("transport");

                            if (attr != null && attr.Equals("http://schemas.xmlsoap.org/soap/http"))
                            {
                                keyedReference httpTransport = newKeyedReference(
                                       "uddi:uddi.org:wsdl:categorization:transport", "uddi-org:http", "uddi:uddi.org:transport:http");
                                cbitems.Add(httpTransport);
                            }
                            else
                            {
                                log.warn("i don't know how to process the soap transport value of " + xe.GetAttribute("transport", "http://schemas.xmlsoap.org/wsdl/soap/"));
                            }
                        }
                    }
                    else
                    {
                        log.warn("Unrecongnized binding type: " + xe.NamespaceURI + " " + xe.LocalName + ". Generated"
                            + "binding tModel may be missing the required (according to WSDL2UDDI spec) "
                            + "uddi:uddi.org:wsdl:categorization:protocol keyedReference.");
                    }
                }



                categoryBag = new uddi.apiv3.categoryBag();
                categoryBag.Items = cbitems.ToArray();
                tModel.categoryBag = categoryBag;
                tModels.Add(tModel);
            }
            return tModels;
        }
    }

    /// <summary>
    /// Information about a web service, a WSDL Parser
    /// Supports imported WSDL's
    /// </summary>
    public class ReadWSDL
    {
        bool ignoressl = false;
        public bool ignoreSSLErrors
        {
            get { return ignoressl; }
            set { ignoressl = value; }
        }

        /// <summary>
        /// Parses a WSDL. Note all exceptions are returned to the caller, including transport
        /// IO and parsing errors
        /// </summary>
        /// <param name="file">the file path or URL to the WSDL</param>
        /// <returns></returns>
        /// <throws>ArgumentNullException if file is null</throws>
        public org.xmlsoap.schemas.easyWsdl.tDefinitions readWSDL(String file)
        {
            if (file == null)
                throw new ArgumentNullException();
            org.xmlsoap.schemas.easyWsdl.tDefinitions wsdl = getWsdl(file);
            return wsdl;
        }


        org.xmlsoap.schemas.easyWsdl.tDefinitions getWsdl(String url)
        {
            org.xmlsoap.schemas.tDefinitions imported = null;
            //read it
            if (url.StartsWith("http", StringComparison.CurrentCultureIgnoreCase))
            {
                if (url.StartsWith("https", StringComparison.CurrentCultureIgnoreCase) && ignoressl)
                {
                    ServicePointManager.ServerCertificateValidationCallback = delegate { return true; };
                }
                WebClient c = new WebClient();
                if (url.StartsWith("https", StringComparison.CurrentCultureIgnoreCase) && ignoressl)
                    ServicePointManager.ServerCertificateValidationCallback = null;
                String s = c.DownloadString(url);
                StringReader sr = new StringReader(s);
                XmlSerializer xs = new XmlSerializer(typeof(org.xmlsoap.schemas.tDefinitions));
                imported = (org.xmlsoap.schemas.tDefinitions)xs.Deserialize(sr);
                c.Dispose();
            }
            else
            {
                //serialize it
                StringReader sr = new StringReader(File.ReadAllText(url));
                XmlSerializer xs = new XmlSerializer(typeof(org.xmlsoap.schemas.tDefinitions));
                imported = (org.xmlsoap.schemas.tDefinitions)xs.Deserialize(sr);
            }


            //convert it

            org.xmlsoap.schemas.easyWsdl.tDefinitions defs = ConvertTo.toEasyWsdl(imported);
            HashSet<org.xmlsoap.schemas.easyWsdl.tImport>.Enumerator it = defs.import.GetEnumerator();
            while (it.MoveNext())
            {
                if (it.Current.location.EndsWith(".xsd", StringComparison.CurrentCultureIgnoreCase))
                    continue;
                org.xmlsoap.schemas.easyWsdl.tDefinitions xi = null;
                if (!it.Current.location.StartsWith("http"))
                {
                    String temp = url.Substring(0, url.LastIndexOf(Path.DirectorySeparatorChar)) + Path.DirectorySeparatorChar + (it.Current.location);
                    xi = getWsdl(temp);
                }
                else
                    xi = getWsdl(it.Current.location);


                //merge import stuff into our main
                HashSet<XmlElement>.Enumerator it2 = xi.Any.GetEnumerator();
                while (it2.MoveNext())
                {
                    defs.Any.Add(it2.Current);
                }
                HashSet<org.xmlsoap.schemas.easyWsdl.tBinding>.Enumerator it3 = xi.binding.GetEnumerator();
                while (it3.MoveNext())
                {
                    defs.binding.Add(it3.Current);
                }


                if (defs.documentation == null)
                    defs.documentation = new xmlsoap.schemas.easyWsdl.tDocumentation();
                if (defs.documentation.Any == null)
                    defs.documentation.Any = new HashSet<System.Xml.XmlNode>();

                HashSet<XmlNode>.Enumerator it4 = xi.documentation.Any.GetEnumerator();
                while (it4.MoveNext())
                {
                    defs.documentation.Any.Add(it4.Current);
                }

                HashSet<org.xmlsoap.schemas.easyWsdl.tMessage>.Enumerator it5 = xi.message.GetEnumerator();
                while (it5.MoveNext())
                {

                    defs.message.Add(it5.Current);
                }


                HashSet<org.xmlsoap.schemas.easyWsdl.tPortType>.Enumerator it6 = xi.portType.GetEnumerator();
                while (it6.MoveNext())
                {
                    defs.portType.Add(it6.Current);
                }

                HashSet<org.xmlsoap.schemas.easyWsdl.tService>.Enumerator it7 = xi.service.GetEnumerator();
                while (it7.MoveNext())
                {
                    defs.service.Add(it7.Current);
                }

                HashSet<org.xmlsoap.schemas.easyWsdl.tTypes>.Enumerator it8 = xi.types.GetEnumerator();
                while (it8.MoveNext())
                {
                    defs.types.Add(it8.Current);
                }


            }

            return defs;

        }


        public void setIgnoreSSLErrors(bool p)
        {
            ignoressl = p;
        }
    }


}
