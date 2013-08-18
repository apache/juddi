using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.config
{
    public class Property
    {
        public readonly static String UDDI_RELOAD_DELAY = "reloadDelay";

        public readonly static String UDDI_PROXY_FACTORY_INITIAL = "java.naming.factory.initial";
        public readonly static String UDDI_PROXY_PROVIDER_URL = "java.naming.provider.url";
        public readonly static String UDDI_PROXY_FACTORY_URL_PKS = "java.naming.factory.url.pkgs";

        public readonly static String DEFAULT_UDDI_PROXY_TRANSPORT = "org.uddi.api_v3.client.transport.JAXWSTransport";

        //Properties that can be set in the uddi.xml
        public static readonly String LANG = "lang";
        public static readonly String BUSINESS_KEY = "businessKey";
        public static readonly String KEY_DOMAIN = "keyDomain";
        public static readonly String SERVICE_KEY_FORMAT = "bpelServiceKeyFormat";
        public static readonly String BUSINESS_KEY_FORMAT = "businessKeyFormat";
        public static readonly String BINDING_KEY_FORMAT = "bindingKeyFormat";
        public static readonly String SUBSCRIPTION_KEY_FORMAT = "subscriptionKeyFormat";
        public static readonly String SERVICE_DESCRIPTION = "serviceDescription";
        public static readonly String BINDING_DESCRIPTION = "bindingDescription";
        public static readonly String SERVICE_CATEGORY_BAG = "serviceCategoryBag";
        public static readonly String BINDING_CATEGORY_BAG = "bindingCategoryBag";
        public static readonly String BASIC_AUTH_USERNAME = "basicAuthUsername";
        public static readonly String BASIC_AUTH_PASSWORD = "basicAuthPassword";

        //Default Values
        public static readonly String DEFAULT_LANG = "en";
        public static readonly String DEFAULT_SERVICE_DESCRIPTION = "Default service description when no <wsdl:document> element is defined inside the <wsdl:service> element.";
        public static readonly String DEFAULT_BINDING_DESCRIPTION = "Default binding description when no <wsdl:document> element is defined inside the <wsdl:binding> element.";

        public static String getTempDir()
        {
            String tmpDir = Environment.GetEnvironmentVariable("TEMP");
          
            return tmpDir;
        }

    }

}
