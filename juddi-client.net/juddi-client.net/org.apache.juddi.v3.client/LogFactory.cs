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
    /// 
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
                        s = "INFO";
                    level = (LogLevel)Enum.Parse(typeof(LogLevel), s);
                }
                catch (Exception ex)
                {
                    level = LogLevel.WARN;
                }

                String target = "";
                try
                {
                    target = System.Configuration.ConfigurationManager.AppSettings["org.apache.juddi.v3.client.log.targets"];
                }
                catch (Exception ex)
                {
                    level = LogLevel.WARN;
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

        private static Log getLogger(String name)
        {
            if (target.Equals("CONSOLE", StringComparison.CurrentCultureIgnoreCase))
            {
                return new ConsoleLogger(name, level);
            }
            else if (target.Equals("EVENTLOG", StringComparison.CurrentCultureIgnoreCase))
            {
                return new EventLogger(name, level);
            }
            else if (target.Equals("FILE", StringComparison.CurrentCultureIgnoreCase))
            {
                return new FileLogger(name, level, option);
            }
            else
                return new ConsoleLogger(name, level);
        }

        public static Log getLog(Type type)
        {
            init();
            return getLogger(type.Name);
            //return new ConsoleLogger(type.Name, level);
        }
    }
}
