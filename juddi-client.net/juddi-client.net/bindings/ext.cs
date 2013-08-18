using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.uddi.apiv3
{
    /// <remarks/>

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
