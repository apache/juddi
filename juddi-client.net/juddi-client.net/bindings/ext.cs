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
using System;

/// This extends the generated classes with helper functions
/// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
namespace org.uddi.apiv3
{

    public partial class keyedReference
    {
        public keyedReference(String key, String name, String value)
        {
            this.tModelKey = key;
            this.keyName = name;
            this.keyValue = value;
        }
    }
    public partial class name
    {
        public name()
        { }
        public name(String Value, String lang)
        {
            this.valueField = Value;
            this.lang = lang;
        }
    }


    /// <remarks/>
    public partial class UDDI_Inquiry_SoapBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public UDDI_Inquiry_SoapBinding(String url)
        {
            this.Url = url;
        }
    }


    /// <remarks/>
    public partial class UDDI_Publication_SoapBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public UDDI_Publication_SoapBinding(String url)
        {
            this.Url = url;
        }
    }

    /// <remarks/>
    public partial class UDDI_Security_SoapBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public UDDI_Security_SoapBinding(String url)
        {
            this.Url = url;
        }
    }


    /// <remarks/>
    public partial class UDDI_CustodyTransfer_SoapBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public UDDI_CustodyTransfer_SoapBinding(String url)
        {
            this.Url = url;
        }
    }


    /// <remarks/>
    public partial class UDDI_Replication_SoapBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public UDDI_Replication_SoapBinding(String url)
        {
            this.Url = url;
        }
    }


    /// <remarks/>
    public partial class UDDI_Subscription_SoapBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public UDDI_Subscription_SoapBinding(String url)
        {
            this.Url = url;
        }
    }


    /// <remarks/>
    public partial class UDDI_ValueSetCaching_SoapBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public UDDI_ValueSetCaching_SoapBinding(String url)
        {
            this.Url = url;
        }

    }




    /// <remarks/>
    public partial class UDDI_ValueSetValidation_SoapBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public UDDI_ValueSetValidation_SoapBinding(String url)
        {
            this.Url = url;
        }
    }



    /**
     * Provides a basic enumeration of the predefinied useType values for UDDI service/bindingTemplates/accessPoints/useType
     * @author unknown
     */
    public enum AccessPointType
    {
        /**
             *  endPoint: designates that the accessPoint points to the actual service endpoint, i.e. the network address at which the Web service can be invoked,
             */
        endPoint,
        /**
         *  bindingTemplate: designates that the accessPoint contains a bindingKey that points to a different bindingTemplate entry. The value in providing this facility is seen when a business or entity wants to expose a service description (e.g. advertise that they have a service available that suits a specific purpose) that is actually a service that is described in a separate bindingTemplate record. This might occur when many service descriptions could benefit from a single service description,
         */
        bindingTemplate,
        /**
         * hostingRedirector: designates that the accessPoint can only be determined by querying another UDDI registry.  This might occur when a service is remotely hosted.
         */
        hostingDirector,
        /**
         * wsdlDeployment: designates that the accessPoint points to a remotely hosted WSDL document that already contains the necessary binding information, including the actual service endpoint.
         *  The bindingTemplate of a Web service making use of indirection via a hostingRedirector Web service contains the bindingKey of the hosting redirector service’s bindingTemplate.  The hosting redirector’s bindingTemplate contains the accessPoint of the Hosting Redirector Web service
         */
        wsdlDeployment
    }

}

namespace org.apache.juddi.apiv3
{
    public partial class JUDDIApiService : System.Web.Services.Protocols.SoapHttpClientProtocol
    {
        /// <remarks/>
        public JUDDIApiService(String url)
        {
            this.Url = url;
        }

    }


}
