using System;
using System.Collections.Generic;
using System.Text;

namespace org.apache.juddi.v3.client.log
{
    public class LogFactory
    {
        public static Log getLog(String name)
        {
            return new EventLogger(name);
        }

        public static Log getLog(Type type)
        {
            return new EventLogger(type.Name);
        }
    }
}
