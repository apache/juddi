using org.apache.juddi.v3.client.log;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.config
{
    public class XRegistration
    {

        private Log log = LogFactory.getLog(typeof(XRegistration));
        private UDDIClerk toClerk;
        private UDDIClerk fromClerk;
        private String entityKey;

        public XRegistration() { }

        public XRegistration(String entityKey, UDDIClerk fromClerk,
                UDDIClerk toClerk)
        {
            this.fromClerk = fromClerk;
            this.toClerk = toClerk;
            this.entityKey = entityKey;
        }

        public UDDIClerk getToClerk()
        {
            return toClerk;
        }
        public void setToClerk(UDDIClerk toClerk)
        {
            this.toClerk = toClerk;
        }
        public UDDIClerk getFromClerk()
        {
            return fromClerk;
        }
        public void setFromClerk(UDDIClerk fromClerk)
        {
            this.fromClerk = fromClerk;
        }
        public String getEntityKey()
        {
            return entityKey;
        }
        public void setEntityKey(String entityKey)
        {
            this.entityKey = entityKey;
        }

        /**
         * Copies the BusinessInformation from one UDDI to another UDDI. Note that no services are being
         * copied over by this service. Use xRegisterService to copy over services.
         */
        public void xRegisterBusiness()
        {
            businessEntity businessEntity;
            try
            {
                businessEntity = fromClerk.findBusiness(entityKey, fromClerk.getUDDINode().getApiNode());
                log.info("xregister business " + businessEntity.name[0].Value + " + from "
                        + fromClerk.getName() + " to " + toClerk.getName() + ".");
                //not bringing over the services. They need to be explicitly copied using xRegisterService.
                businessEntity.businessServices=(null);
                toClerk.register(businessEntity, toClerk.getUDDINode().getApiNode());
            }
            catch (Exception e)
            {
                log.error("Could not " + toString() + ". " + e.Message , e);
            }
        }

        /**
         * Copies the BusinessInformation from one UDDI to another UDDI.
         */
        public void xRegisterBusinessAndServices()
        {
            businessEntity businessEntity;
            try
            {
                businessEntity = fromClerk.findBusiness(entityKey, fromClerk.getUDDINode().getApiNode());
                log.info("xregister business " + businessEntity.name[0].Value + " + from "
                        + fromClerk.getName() + " to " + toClerk.getName() + " including all services owned by this business.");
                toClerk.register(businessEntity, toClerk.getUDDINode().getApiNode());
            }
            catch (Exception e)
            {
                log.error("Could not " + toString() + ". " + e.Message, e);
            }
        }
        /**
         * Copies the Service from one UDDI to another UDDI.
         */
        public void xRegisterService()
        {
            businessService businessService;
            try
            {
                businessService = fromClerk.findService(entityKey, fromClerk.getUDDINode().getApiNode());
                log.info("xregister service " + businessService.name[0].Value + " + from "
                        + fromClerk.getName() + " to " + toClerk.getName());
                businessService.bindingTemplates=(null);
                toClerk.register(businessService, toClerk.getUDDINode().getApiNode());
            }
            catch (Exception e)
            {
                log.error("Could not " + toString() + ". " + e.Message , e);
            }
        }
        /**
         * Copies the Service from one UDDI to another UDDI along with all the bindingTemplates.
         */
        public void xRegisterServiceAndBindings()
        {
            businessService businessService;
            try
            {
                businessService = fromClerk.findService(entityKey, fromClerk.getUDDINode().getApiNode());
                log.info("xregister service " + businessService.name[0].Value+ " + from "
                        + fromClerk.getName() + " to " + toClerk.getName());
                toClerk.register(businessService, toClerk.getUDDINode().getApiNode());
            }
            catch (Exception e)
            {
                log.error("Could not " + toString() + ". " + e.Message , e);
            }
        }
        /**
         * Copies the TemplateBinding from one UDDI to another UDDI.
         */
        public void xRegisterServiceBinding()
        {
            try
            {
                bindingTemplate bindingTemplate = fromClerk.findServiceBinding(entityKey, fromClerk.getUDDINode().getApiNode());
                log.info("xregister binding " + bindingTemplate.bindingKey + " + from "
                        + fromClerk.getName() + " to " + toClerk.getName());
                toClerk.register(bindingTemplate, toClerk.getUDDINode().getApiNode());
            }
            catch (Exception e)
            {
                log.error("Could not " + toString() + ". " + e.Message , e);
            }
        }

        public String toString()
        {
            return " xregister entityKey: " + entityKey + " + from " + fromClerk.getName() + " to " + toClerk.getName();
        }
	

    }
}
