using org.apache.juddi.v3.client.log;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Xml.XPath;

namespace org.apache.juddi.v3.client.config
{
    public static class XmlConfiguration 
    {
        public static uddi LoadXmlConfiguration(string filename)
        {
               String content = File.ReadAllText(filename);
               using (StringReader sr = new StringReader(content))
               {
                   try
                   {
                       XmlSerializer xs = new XmlSerializer(typeof(uddi));
                       return xs.Deserialize(sr) as uddi;
                   }
                   catch (Exception ex)
                   {
                       LogFactory.getLog(typeof(XmlConfiguration)).error("Error loading config file from " + filename + " ", ex);
                       throw ex;
                   }
               }
        }

    }
}
