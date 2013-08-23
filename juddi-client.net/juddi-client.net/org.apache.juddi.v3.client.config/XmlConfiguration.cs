/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
using System.IO;
using System.Xml.Serialization;

namespace org.apache.juddi.v3.client.config
{
    /// <summary>
    /// The XmlConfiguration class was originally going to be a port of the Apache Commons Configuration, however to make things easier
    /// the juddi-client.xsd file was used to generate c# code representing the expected settings from the Java juddi-client.
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
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
