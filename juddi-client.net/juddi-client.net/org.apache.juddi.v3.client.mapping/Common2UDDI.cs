    /*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
 using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.log;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.mapping
{
    /// <summary>
    /// Common parsing functions for converting to UDDI's data structures
    /// </summary>
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
