using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml.Serialization;

namespace org.apache.juddi.jaxb
{
    public  class PrintUDDI<T>
    {
        public  String print(Object j)
        {
            XmlSerializer xs = new XmlSerializer(typeof(T));
            StringWriter sw = new StringWriter();
            xs.Serialize(sw, j);
            return sw.ToString();
        }
    }
}
