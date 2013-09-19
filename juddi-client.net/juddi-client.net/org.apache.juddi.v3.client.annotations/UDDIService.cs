using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.annotations
{
    [System.AttributeUsage(System.AttributeTargets.Interface | System.AttributeTargets.Class, AllowMultiple = false)]
    public class UDDIService : System.Attribute
    {
        public UDDIService()
        {
            this.lang = "en";
            businessKey = "";
            serviceName = "";
            categoryBag = "";
        }
        /** Name of the Service, this can be omitted if one is specified in a WebService annotation */
        public String serviceName { get; set; }
        /** Description of the Service */
        public String description { get; set; }
        /** Unique key of this service */
        public String serviceKey { get; set; }
        /** Unique key of the business to which this Service belongs. */
        public String businessKey { get; set; }
        /** Language code i.e.: en, fr, nl. */
        public String lang { get; set; }
        /** List of KeyedReferences */
        public String categoryBag { get; set; }
    }
}
