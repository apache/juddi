using org.apache.juddi.v3.client.mapping;
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
using System;

namespace org.apache.juddi.v3.client.config
{

    public class UDDIKeyConvention
    {

        //Default Values
        public static readonly String DEFAULT_BUSINESS_KEY_FORMAT = "uddi:${keyDomain}:business_${businessName}";
        public static readonly String DEFAULT_SERVICE_KEY_FORMAT = "uddi:${keyDomain}:service_${serviceName}";
        public static readonly String DEFAULT_SUBSCRIPTION_KEY_FORMAT = "uddi:${keyDomain}:service_cache_${serverName}";
        public static readonly String DEFAULT_BINDING_KEY_FORMAT = "uddi:${keyDomain}:binding_${serverName}_${serviceName}_${portName}_${serverPort}";

        /// <summary>
        /// Constructs the serviceKey based on the bindingKeyFormat specified in the properties. When no
        /// businessKeyFormat is specific the default format of uddi:${keyDomain}:${businessName} is used. The businessName
        /// property needs to be set properties.
        /// </summary>
        /// <param name="properties"></param>
        /// <returns></returns>
        public static String getBusinessKey(Properties properties)
        {
            String businessKey = properties.getProperty(Property.BUSINESS_KEY);
            if (businessKey == null)
            {
                String keyFormat = properties.getProperty(Property.BUSINESS_KEY_FORMAT, DEFAULT_BUSINESS_KEY_FORMAT);
                businessKey = TokenResolver.replaceTokens(keyFormat, properties).ToLower();
            }
            return businessKey;
        }

        public static String getSubscriptionKey(Properties properties)
        {
            String keyFormat = properties.getProperty(Property.SUBSCRIPTION_KEY_FORMAT, DEFAULT_SUBSCRIPTION_KEY_FORMAT);
            String subscriptionKey = TokenResolver.replaceTokens(keyFormat, properties).ToLower();
            return subscriptionKey;
        }

        /// <summary>
        /// Constructs the serviceKey based on the serviceKeyFormat specified in the properties. When no
        ///serviceKeyFormat is specific the default format of uddi:${keyDomain}:${serviceName} is used.
        /// </summary>
        /// <param name="properties"></param>
        /// <param name="serviceName"></param>
        /// <returns></returns>
        public static String getServiceKey(Properties properties, String serviceName)
        {
            Properties tempProperties = new Properties();
            tempProperties.putAll(properties);
            tempProperties.put("serviceName", serviceName);
            //Constructing the serviceKey
            String keyFormat = tempProperties.getProperty(Property.SERVICE_KEY_FORMAT, DEFAULT_SERVICE_KEY_FORMAT);
            String serviceKey = TokenResolver.replaceTokens(keyFormat, tempProperties).ToLower();
            return serviceKey;
        }




        /**
         * Constructs the bindingKey based on the bindingKeyFormat specified in the properties. When no
         * bindingKeyFormat is specific the default format of uddi:${keyDomain}:${nodeName}-${serviceName}-{portName} is used.
         * 
         * @param properties
         * @param serviceName
         * @param portName
         * @return the bindingKey
    */

        public static String getBindingKey(Properties properties, QName serviceName, String portName, Uri bindingUrl)
        {
            Properties tempProperties = new Properties();
            tempProperties.putAll(properties);
            tempProperties.put("serviceName", serviceName.getLocalPart());
            tempProperties.put("portName", portName);
            int port = bindingUrl.Port;
            if (port < 0)
            {
                if ("http".Equals(bindingUrl.Scheme, StringComparison.CurrentCultureIgnoreCase))
                {
                    port = 80;
                }
                else if ("https".Equals(bindingUrl.Scheme))
                {
                    port = 443;
                }
            }
            if (!tempProperties.containsKey("serverPort"))
                tempProperties.put("serverPort", port.ToString());

            //Constructing the binding Key
            String keyFormat = properties.getProperty(Property.BINDING_KEY_FORMAT, DEFAULT_BINDING_KEY_FORMAT);
            String bindingKey = TokenResolver.replaceTokens(keyFormat, tempProperties).ToLower();
            return bindingKey;
        }
    }

}
