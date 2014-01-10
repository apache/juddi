using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.log;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.mapping
{
    public static class Common2UDDI
    {
        public static List<description> mapdescription(String content, String lang)
        {

            List<description> ret = new List<description>();
            if (String.IsNullOrEmpty(content))
            {
                return ret;
            }
            if (content.Length > UDDIConstants.MAX_description_length)
            {
                int offset = 0;
                while (offset < content.Length)
                {
                    description description = new description();
                    description.lang = (lang);
                    int len =  UDDIConstants.MAX_description_length;
                    if (len > content.Length-offset)
                    {
                        len = content.Length -offset;
                    }

                    description.Value = (content.Substring(offset, len));
                    offset = offset + UDDIConstants.MAX_description_length;
                    ret.Add(description);

                }
            }
            else
            {
                ret.Add(new description(content, lang));
            }
            return ret;

        }
    }
}
