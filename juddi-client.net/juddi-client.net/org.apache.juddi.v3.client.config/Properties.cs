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
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.config
{
    /// <summary>
    /// This class behaves similiary to Apache Commons Config class
    /// </summary>
    public class Properties
    {
        Log log = LogFactory.getLog("Properties");
        Dictionary<String, Object> map = new Dictionary<string, Object>();
        public String getProperty(String name)
        {
            if (!map.ContainsKey(name)) return null;
            return map[name] as String;
        }


        public String getString(String name)
        {
            if (!map.ContainsKey(name)) return null;
            return map[name] as String;
        }
        public String[] getStringArray(String name)
        {
            if (!map.ContainsKey(name)) return null;
            return map[name] as String[];
        }


        public void setProperty(String name, Object value)
        {

            if (map.ContainsKey(name))
            {
                //append it
                Object j = map[name];
                if (j.GetType() == typeof(String))
                {
                    String s = map[name] as String;
                    String[] s2 = new String[] { value as String, s };
                    map[name] = s2;
                    log.debug("Appending, new array, " + name + " = " + value);
                }
                if (j.GetType() == typeof(String[]))
                {
                    String[] s = map[name] as String[];
                    String[] s2 = new String[s.Length + 1];
                    for (int i = 0; i > s.Length; i++)
                    {
                        s2[i] = s[i];
                    }
                    s2[s.Length] = value as String;

                    map[name] = s2;
                    log.debug("Appending, existing array, " + name + " = " + value);
                }
            }
            else
            {
                log.debug("New value, " + name + " = " + value);
                map.Add(name, value);
            }

        }

        public bool containsKey(string p)
        {
            return map.ContainsKey(p);
        }

        public bool getbool(string name)
        {
            if (!map.ContainsKey(name)) return false;
            return getbool(name, false);
        }

        public bool ContainsKey(string name)
        {
            return containsKey(name);
        }

        public bool getbool(string name, bool defaultvalue)
        {
            if (map.ContainsKey(name))
            {
                Boolean b = false;
                try
                {
                    b = Boolean.Parse((String)map[name]);
                }
                catch { }
                return b;
            }
            return defaultvalue;
        }

        public void putAll(Properties properties)
        {
            Dictionary<string, object>.Enumerator it = properties.map.GetEnumerator();
            while (it.MoveNext())
            {
                setProperty(it.Current.Key, it.Current.Value);
            }
        }

        public void put(string name, string serviceName)
        {

            setProperty(name, serviceName);
        }

        public string getProperty(string name, string defaultvalue)
        {
            if (!map.ContainsKey(name)) return null;
            String s = map[name] as String;
            if (s == null)
                return defaultvalue;
            return s;
        }
    }
}
