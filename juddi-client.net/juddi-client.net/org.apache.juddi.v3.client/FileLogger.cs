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
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.log
{
    /// <summary>
    /// A simple file logger
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class FileLogger : Log
    {
        private string name;
        private LogLevel level;
        private string option;

        public FileLogger(string name, LogLevel level, string option)
        {
            this.name = name;
            this.level = level;
            this.option = option;
            if (String.IsNullOrEmpty(option))
                option = "juddi.log";
        }

        public void info(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
        }

        private void WriteEntry(string p)
        {
            try
            {
                using (StreamWriter sw = File.AppendText(option))
                {
                    sw.WriteLine(p);
                }
            }
            catch { }
        }

        public void info(string msg)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg);
        }

        public void warn(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
        }

        public void warn(string msg)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg);
        }

        public void error(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
        }

        public void error(string msg)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg);
        }


        public void debug(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
        }

        public void debug(string msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg);
        }

        public bool isDebugEnabled()
        {
            return true;
        }


        public void debug(object msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg.ToString());
        }
    }
}
