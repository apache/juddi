using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.config
{
    public partial class uddi
    {
        Properties p = new Properties();
        public  Properties getProperties()
        {
            return p;
        }

        public void setProperties(Properties props)
        {
            p = props;
        }
    }
}
