using System;
using System.Collections.Generic;
using System.Text;

namespace org.apache.juddi.v3.client.log
{
    public class LogFactory
    {
        public static Log getLog(String name)
        {
            return new ConsoleLogger(name);
        }

        public static Log getLog(Type type)
        {
            return new ConsoleLogger(type.Name);
        }
    }
}
