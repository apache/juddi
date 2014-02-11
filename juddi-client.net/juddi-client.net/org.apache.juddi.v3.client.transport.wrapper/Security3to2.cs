using org.uddi.apiv2;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.transport.wrapper
{
    public class Security3to2 : UDDI_Security_SoapBinding, IDisposable
    {
        private string endpointURL;
        private PublishSoap publishv2 = new PublishSoap();

        public Security3to2(string endpointURL)
        {
            // TODO: Complete member initialization
            this.endpointURL = endpointURL;
        }

        public Security3to2()
        {
            // TODO: Complete member initialization
        }
        public  void Dispose()
        {
            base.Dispose();
            if (publishv2 != null)
            {
                publishv2.Dispose();
                publishv2 = null;
            }
        }
        private void Init()
        {
            publishv2.Url = this.Url;
        }

        public override void discard_authToken(uddi.apiv3.discard_authToken discard_authToken1)
        {
            Init();
            uddi.apiv2.discard_authToken r = new uddi.apiv2.discard_authToken();
            r.authInfo = discard_authToken1.authInfo;
            r.generic = VERSION;
            publishv2.discard_authToken(r);
        }
        public override uddi.apiv3.authToken get_authToken(uddi.apiv3.get_authToken get_authToken1)
        {
            Init();
            uddi.apiv2.get_authToken r = new uddi.apiv2.get_authToken();
            r.cred = get_authToken1.cred;
            r.generic = VERSION;
            r.userID = get_authToken1.userID;
            uddi.apiv2.authToken res = publishv2.get_authToken(r);

            uddi.apiv3.authToken x = new uddi.apiv3.authToken();
            x.authInfo = res.authInfo;
            return x;

        }
        public static readonly String VERSION = "2.0";
    }
}
