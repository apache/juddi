using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.mapping
{
    public class ServiceRegistrationResponse
    {

        private String bindingKey;
        private businessService businessService;

        public String getBindingKey()
        {
            return bindingKey;
        }
        public void setBindingKey(String bindingKey)
        {
            this.bindingKey = bindingKey;
        }
        public businessService getBusinessService()
        {
            return businessService;
        }
        public void setBusinessService(businessService businessService)
        {
            this.businessService = businessService;
        }

    }

}
