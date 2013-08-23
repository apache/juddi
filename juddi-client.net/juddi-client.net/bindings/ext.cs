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
