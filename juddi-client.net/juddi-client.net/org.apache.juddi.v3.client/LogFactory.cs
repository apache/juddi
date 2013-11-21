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

using org.apache.juddi.v3.client.config;
using System;
using System.Collections.Generic;
using System.Text;

namespace org.apache.juddi.v3.client.log
{
    /// <summary>
    /// The LogFactor reads from the current configuration (.NET, not the Juddi-Client config xml)
    /// and produces the appropriate logger
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class LogFactory
    {
        private static LogLevel level = LogLevel.WARN;
        private static String target = "CONSOLE";
        private static bool inited = false;
        private static object thelock = new object();
        private static string option = "";
        private static void init()
        {
            lock (thelock)
            {
                if (inited)
                    return;
                level = LogLevel.WARN;

                try
                {
                    String s = "WARN";
                    s = System.Configuration.ConfigurationManager.AppSettings["org.apache.juddi.v3.client.log.level"];
                    if (String.IsNullOrEmpty(s))
                        s = "WARN";
                    level = (LogLevel)Enum.Parse(typeof(LogLevel), s);
                }
                catch
                {
                    level = LogLevel.WARN;
                }

                String target = "";
                try
                {
                    target = System.Configuration.ConfigurationManager.AppSettings["org.apache.juddi.v3.client.log.target"];
                }
                catch
                {
                    target = "CONSOLE";
                }
                try
                {
                    option = System.Configuration.ConfigurationManager.AppSettings["org.apache.juddi.v3.client.log.logger.file"];
                }
                catch
                {

                }
                inited = true;
            }
        }
        public static Log getLog(String name)
        {
            init();
            return getLogger(name);
        }

        public static Log getLog(String name, String targets, LogLevel level1, String options1)
        {
            
            return getLogger(name, targets, level1, options1);
        }

        private static Log getLogger(String name1, String targets1, LogLevel level1, String options1)
        {
            string[] targets = targets1.Split(',');
            Log ret = null;
            Log last = null;
            for (int i = 0; i < targets.Length; i++)
            {
                last = ret;
                if (targets[i].Equals("CONSOLE", StringComparison.CurrentCultureIgnoreCase))
                {
                    ret = new ConsoleLogger();

                }
                else if (targets[i].Equals("EVENTLOG", StringComparison.CurrentCultureIgnoreCase))
                {
                    ret = new EventLogger();
                }
                else if (targets[i].Equals("FILE", StringComparison.CurrentCultureIgnoreCase))
                {
                    ret = new FileLogger(options1);

                }
                else
                    ret = new ConsoleLogger();
                ret.setName(name1);
                ret.setLevel(level1);
                if (last != null)
                    ret.setDownstream(last);
            }
            return ret;
        }
        private static Log getLogger(String name)
        {
            return getLogger(name, target, level, option);
        }

        public static Log getLog(Type type)
        {
            init();
            return getLogger(type.Name);
            //return new ConsoleLogger(type.Name, level);
        }


    }
}
