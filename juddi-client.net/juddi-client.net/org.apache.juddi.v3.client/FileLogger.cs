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

using System.Text;

namespace org.apache.juddi.v3.client.log
{
    /// <summary>
    /// A simple file logger
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class FileLogger : Log
    {
        private string option = "juddi.log";

        private string name;
        private LogLevel level;
        private Log downstream = null;
        public FileLogger() { }
        public FileLogger(string option)
        {
            this.option = option;
        }
        public void setDownstream(Log downstream)
        {
            this.downstream = downstream;
        }

        public void setLevel(LogLevel level)
        {
            this.level = level;
        }

        public void setName(string name)
        {
            this.name = name;
        }

        public void setOption(string option)
        {
            this.option = option;
        }


        public void info(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
            if (downstream != null)
                downstream.info(msg, ex);
        }

        private void WriteEntry(string msg)
        {
            try
            {
                if (!File.Exists(option))
                {
                    StreamWriter sw = File.CreateText(option);
                    sw.Close();
                    sw.Dispose();
                }
                using (StreamWriter sw = File.AppendText(option))
                {
                    sw.WriteLine(msg);
                }
            }
            catch (Exception ex){
                Console.Out.WriteLine("Unable to log to " + option + "! " + ex.Message);
            }

        }

        public void info(string msg)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg);
            if (downstream != null)
                downstream.info(msg);
        }

        public void warn(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
            if (downstream != null)
                downstream.warn(msg, ex);
        }

        public void warn(string msg)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg);
            if (downstream != null)
                downstream.warn(msg);
        }

        public void error(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
            if (downstream != null)
                downstream.error(msg, ex);
        }

        public void error(string msg)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg);
            if (downstream != null)
                downstream.error(msg);
        }


        public void debug(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
            if (downstream != null)
                downstream.debug(msg, ex);
        }

        public void debug(string msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg);
            if (downstream != null)
                downstream.warn(msg);
        }

        public bool isDebugEnabled()
        {
            return true;
        }


        public void debug(object msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg.ToString());
            if (downstream != null)
                downstream.warn(msg.ToString());
        }


    }
}
