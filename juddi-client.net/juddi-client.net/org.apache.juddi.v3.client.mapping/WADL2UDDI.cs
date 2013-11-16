using net.java.dev.wadl;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.log;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Xml.Serialization;

namespace org.apache.juddi.v3.client.mapping
{
    public class WADL2UDDI
    {
        private static Log log = LogFactory.getLog(typeof(WADL2UDDI));
        private String keyDomainURI;
        private String businessKey;
        private String lang;
        private UDDIClerk clerk = null;
        private Properties properties = null;


        public WADL2UDDI(UDDIClerk clerk, Properties properties)
        {
            this.clerk = clerk;
            this.properties = properties;

            if (clerk != null)
            {
                if (!properties.containsKey("keyDomain"))
                {
                    throw new ConfigurationErrorsException("Property keyDomain is a required property when using WADL2UDDI.");
                }
                if (!properties.containsKey("businessKey") && !properties.containsKey("businessName"))
                {
                    throw new ConfigurationErrorsException("Either property businessKey, or businessName, is a required property when using WADL2UDDI.");
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
                        throw new ConfigurationErrorsException("Property nodeName is not defined and is a required property when using WADL2UDDI.");
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

        public HashSet<tModel> createWADLTModels(String wadlURL, application app)
        {
            HashSet<tModel> tModels = new HashSet<tModel>();

            return tModels;
        }

        public HashSet<tModel> createWADLPortTypeTModels(String wadlURL, application app)
        {
            HashSet<tModel> tModels = new HashSet<tModel>();
            // Create a tModel for each portType

            return tModels;
        }

        public String getKeyDomainURI()
        {
            return keyDomainURI;
        }

        public void setKeyDomain(String keyDomainURI)
        {
            this.keyDomainURI = keyDomainURI;
        }

        public String getLang()
        {
            return lang;
        }

        public void setLang(String lang)
        {
            this.lang = lang;
        }

        private static String ContentToString(List<Object> content)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < content.Count; i++)
            {
                sb.Append(content[i].ToString()).Append(" ");
            }
            return sb.ToString().Trim();
        }

        /**
         * Creates a UDDI Business Service.
         *
         * @param serviceQName This must be specified to identify the namespace of
         * the service, which is used to set the service uddi key
         * @param waldDefinition
         * @return
         */
        public businessService createBusinessService(QName serviceQName, application wadlDefinition)
        {

            log.debug("Constructing Service UDDI Information for " + serviceQName);
            businessService service = new businessService();
            // BusinessKey
            service.businessKey = (businessKey);
            // ServiceKey
            service.serviceKey = (UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart()));
            // Description
            String serviceDescription = properties.getProperty(Property.SERVICE_DESCRIPTION, Property.DEFAULT_SERVICE_DESCRIPTION);
            // Override with the service description from the WSDL if present
            bool lengthwarn = false;
            if (wadlDefinition.doc != null)
            {

                for (int i = 0; i < wadlDefinition.doc.Length; i++)
                {
                    description description = new description();
                    if (wadlDefinition.doc[i].lang != null)
                    {
                        description.lang = (wadlDefinition.doc[i].lang);
                    }
                    else
                    {
                        description.lang = (lang);
                    }
                    if (description.lang != null && description.lang.Length > UDDIConstants.MAX_xml_lang_length)
                    {
                        lengthwarn = true;
                        description.lang = (description.lang.Substring(0, UDDIConstants.MAX_xml_lang_length - 1));
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.Append(wadlDefinition.doc[i].title).Append(" ");
                    sb.Append(ContentToString(wadlDefinition.doc[i].Any));

                    description.Value = (wadlDefinition.doc[i].title);
                    if (description.Value != null && description.Value.Length > UDDIConstants.MAX_description_length)
                    {
                        lengthwarn = true;
                        description.Value = (description.Value.Substring(0, UDDIConstants.MAX_description_length - 1));
                    }

                }
            }
            else
            {

                description description = new description();
                description.lang = (lang);
                if (description.lang != null && description.lang.Length > UDDIConstants.MAX_xml_lang_length)
                {
                    lengthwarn = true;
                    description.lang = (description.lang.Substring(0, UDDIConstants.MAX_xml_lang_length - 1));
                }
                description.Value = (serviceDescription);
                if (service.description == null)
                    service.description = new uddi.apiv3.description[] { description };

                if (description.Value != null && description.Value.Length > UDDIConstants.MAX_description_length)
                {
                    lengthwarn = true;
                    description.Value = (description.Value.Substring(0, UDDIConstants.MAX_description_length - 1));
                }
            }



            // Service name
            name sName = new name();
            sName.lang = (lang);
            if (wadlDefinition.doc != null && wadlDefinition.doc.Length > 0)
            {
                sName.Value = (wadlDefinition.doc[0].title);
            }
            if (sName.Value == null)
            {
                sName.Value = (serviceQName.getLocalPart());
            }
            service.name = new name[] { sName };

            categoryBag cb = new categoryBag();
            List<keyedReference> krs = new List<keyedReference>();
            String ns = serviceQName.getNamespaceURI();
            if (ns != null && ns != "")
            {
                keyedReference namespaceReference = new keyedReference(
                        "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", ns);
                krs.Add(namespaceReference);
            }

            keyedReference serviceReference = new keyedReference(
                    "uddi:uddi.org:wadl:types", "uddi-org:wadl:types", "service");
            krs.Add(serviceReference);

            keyedReference localNameReference = new keyedReference(
                    "uddi:uddi.org:xml:localname", "uddi-org:xml:localName", serviceQName.getLocalPart());
            krs.Add(localNameReference);
            cb.Items = krs.ToArray();
            service.categoryBag = (cb);
            if (wadlDefinition.resources != null)
                for (int i = 0; i < wadlDefinition.resources.Length; i++)
                {
                    bindingTemplate bindingTemplate = createWADLBinding(serviceQName, getDocTitle(wadlDefinition.resources[i].doc),
                        new Uri(wadlDefinition.resources[i].@base), wadlDefinition.resources[i]);
                    service.bindingTemplates = new bindingTemplate[] { bindingTemplate };
                }


            if (lengthwarn)
            {
                log.warn("Some object descriptions are longer than the maximum allowed by UDDI and have been truncated.");
            }
            return service;
        }

        private string getDocTitle(doc[] doc)
        {
            if (doc == null || doc.Length == 0 || doc[0].title == null)
            {
                return "A resource base URL without a description";
            }
            return (doc[0].title);
        }

        private string ContentToString(System.Xml.XmlNode[] xmlNode)
        {
            if (xmlNode == null) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < xmlNode.Length; i++)
            {
                sb.Append(xmlNode[i].Value);
            }
            return sb.ToString();
        }

        public static List<Uri> GetBaseAddresses(application app)
        {
            List<Uri> urls = new List<Uri>();
            if (app == null) return urls;
            if (app.resources != null)
                for (int i = 0; i < app.resources.Length; i++)
                {
                    try
                    {
                        urls.Add(new Uri(app.resources[i].@base));
                    }
                    catch (Exception ex)
                    {
                        log.warn("The base URL " + app.resources[i].@base + " is invalid or could not be parsed", ex);
                    }
                }
            return urls;
        }


        protected bindingTemplate createWADLBinding(QName serviceQName, String portName, Uri serviceUrl, resources res)
        {

            bindingTemplate bindingTemplate = new bindingTemplate();
            // Set BusinessService Key
            bindingTemplate.serviceKey = (UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart()));

            if (serviceUrl != null)
            {
                // Set AccessPoint
                accessPoint accessPoint = new accessPoint();
                accessPoint.useType = (AccessPointType.endPoint.ToString());
                accessPoint.Value = ((serviceUrl.ToString()));
                bindingTemplate.Item = (accessPoint);
                // Set Binding Key
                String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceQName, portName, serviceUrl);
                bindingTemplate.bindingKey = (bindingKey);

                description description = new description();
                description.lang = (lang);
                description.Value = (getDescription(res.doc));
                bindingTemplate.description = new description[] { description };

                // reference wsdl:binding tModel
                tModelInstanceInfo tModelInstanceInfoBinding = new tModelInstanceInfo();
                tModelInstanceInfoBinding.tModelKey = (keyDomainURI + "binding");
                instanceDetails id = new instanceDetails();
                id.instanceParms=  portName ;
                tModelInstanceInfoBinding.instanceDetails = (id);
                description descriptionB = new description();
                descriptionB.lang = (lang);
                descriptionB.Value = ("The binding that this endpoint implements. " + bindingTemplate.description[0].Value
                        + " The instanceParms specifies the port local name.");
                tModelInstanceInfoBinding.description = new description[] { descriptionB };
                tModelInstanceInfo tModelInstanceDetails = new tModelInstanceInfo();
                //  tModelInstanceDetails.instanceDetails = new instanceDetails[]{tModelInstanceInfoBinding};


                tModelInstanceInfo tModelInstanceInfoPortType = new tModelInstanceInfo();
                tModelInstanceInfoPortType.tModelKey = (keyDomainURI + "rest");

                description descriptionPT = new description();
                descriptionPT.lang = (lang);
                descriptionPT.Value = ("The wadl:Resource:base implements.");
                tModelInstanceInfoPortType.description = new description[] { descriptionPT };
                //  tModelInstanceDetails.getTModelInstanceInfo().add(tModelInstanceInfoPortType);

                //bindingTemplate.tModelInstanceDetails = new tModelInstanceInfo

            }
            return bindingTemplate;
        }

        private string getDescription(doc[] doc)
        {
            if (doc == null) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < doc.Length; i++)
            {
                sb.Append(doc[i].title).Append(" ");
                sb.Append(ContentToString(doc[i].Any));
            }
            return sb.ToString().Trim();
        }


        public const String PACKAGE = "org.apache.juddi.v3.client.mappings.wadl";


        public bool AcceptAllCertifications(object sender, System.Security.Cryptography.X509Certificates.X509Certificate certification, System.Security.Cryptography.X509Certificates.X509Chain chain, System.Net.Security.SslPolicyErrors sslPolicyErrors)
        {
            return true;
        }

        /**
         * Parses a web accessible WADL file
         * @param weburl
         * @param username
         * @param password
         * @param ignoreSSLErrors if true, SSL errors are ignored
         * @return a non-null "application" object, represeting a WADL's application root XML 
         * Sample code:<br>
         * <pre>
         * application app = WADL2UDDI.ParseWadl(new URL("http://server/wsdl.wsdl"), "username", "password", 
         *      clerkManager.getClientConfig().isX_To_Wsdl_Ignore_SSL_Errors() );
         * </pre>
         */
        public application ParseWadl(Uri weburl, String username, String password, bool ignoreSSLErrors)
        {
            WebClient httpclient = null;
            application unmarshal = null;
            try
            {
                String url = weburl.ToString();
                if (!url.ToLower().StartsWith("http"))
                {
                    return ParseWadl(weburl);
                }

                bool usessl = false;
                int port = 80;
                if (url.ToString().ToLower().StartsWith("https://"))
                {
                    port = 443;
                    usessl = true;
                }

                if (weburl.Port > 0)
                {
                    port = weburl.Port;
                }

                if (ignoreSSLErrors && usessl)
                {

                    ServicePointManager.ServerCertificateValidationCallback = new System.Net.Security.RemoteCertificateValidationCallback(AcceptAllCertifications);
                    httpclient = new WebClient();
                }
                else
                {
                    httpclient = new WebClient();
                }

                if (username != null && username.Length > 0
                        && password != null && password.Length > 0)
                {
                    httpclient.Credentials = new NetworkCredential(username, password);
                }

                try
                {

                    String wadl = httpclient.DownloadString(url);
                    XmlSerializer xs = new XmlSerializer(typeof(application));
                    StringReader sr = new StringReader(wadl);
                    unmarshal = (application)xs.Deserialize(sr);

                }
                finally
                {
                    httpclient.Dispose();

                }

            }
            catch (Exception e)
            {
                log.error("error downloading " + weburl, e);
            }
            finally
            {
                if (httpclient != null)
                {
                    httpclient.Dispose();
                }
            }
            return unmarshal;
        }

        /// <summary>
        /// will parse a file from the disk and parse it, returns null if an error occurs and logs it
        /// </summary>
        /// <param name="file"></param>
        /// <returns></returns>
        public static application ParseWadl(String file)
        {
            try
            {
                XmlSerializer xs = new XmlSerializer(typeof(application));
                String s = File.ReadAllText(file);
                StringReader sr = new StringReader(s);
                return (application)xs.Deserialize(sr);
            }
            catch (Exception ex)
            {
                log.error("error parsing file", ex);
            }
            return null;
        }

        /// <summary>
        /// will download a file from the network/internet and parse it, returns null if an error occurs and logs it
        /// </summary>
        /// <param name="file"></param>
        /// <returns></returns>
        public static application ParseWadl(Uri file)
        {
            if (file.OriginalString.StartsWith("http"))
            {
                try
                {
                    WebClient c = new WebClient();
                    String content = c.DownloadString(file);
                    StringReader sr = new StringReader(content);
                    XmlSerializer xs = new XmlSerializer(typeof(application));
                    c.Dispose();
                    return (application)xs.Deserialize(sr);
                }
                catch (Exception ex)
                {
                    log.error("error parsing file", ex);
                }
                return null;
            }
            else
                return ParseWadl(file.ToString());
        }




        private string getDescription(List<doc> doc)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < doc.Count; i++)
            {
                sb.Append(doc[i].title).Append(" ");
                sb.Append(ContentToString(doc[i].Any));
            }
            return sb.ToString().Trim();
        }


    }


    public class QName
    {
        string local;
        string ns;
        public QName() { }
        public QName(string ns, string local)
        {
            this.local = local;
            this.ns = ns;
        }
        public string getLocalPart()
        {
            return local;
        }

        public string getNamespaceURI()
        {
            return ns;
        }
    }
}