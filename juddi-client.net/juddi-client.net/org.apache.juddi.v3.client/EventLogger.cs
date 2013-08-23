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
using System.Diagnostics;

namespace org.apache.juddi.v3.client.log
{
    /// <summary>
    /// Logs to the Windows system Event Log
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class EventLogger : Log
    {
        EventLog log;
        LogLevel level;
        string name = "";
        public EventLogger(String name, LogLevel level)
        {
            this.level = level;
            this.name = name;
            log = new EventLog("Application", "localhost", name);
        }
        public void info(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Information);
        }

        public void info(string msg)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg, EventLogEntryType.Information);
        }

        public void warn(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Warning);
        }

        public void warn(string msg)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg, EventLogEntryType.Warning);
        }

        public void error(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Error);
        }

        public void error(string msg)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg, EventLogEntryType.Error);
        }


        public void debug(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Information);
        }

        public void debug(string msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg, EventLogEntryType.Information);
        }

        public bool isDebugEnabled()
        {
            return true;
        }


        public void debug(object msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                log.WriteEntry(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg.ToString(), EventLogEntryType.Information);
        }
    }
}
