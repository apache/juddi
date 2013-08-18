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
