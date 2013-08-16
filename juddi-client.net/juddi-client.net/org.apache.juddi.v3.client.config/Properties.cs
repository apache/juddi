using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.config
{
    public class Properties
    {
        Dictionary<String, String> map = new Dictionary<string, string>();
        public String getProperty(String name)
        {
            return map[name];
        }

        public void setProperty(String name, String value)
        {
             map.Add(name, value);
        }
    }
}
