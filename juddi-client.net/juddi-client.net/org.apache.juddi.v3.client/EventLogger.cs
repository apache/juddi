﻿/*
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

        private string name;
        private LogLevel level;
        private Log downstream = null;
        public static readonly string EVENT_LOG_SOURCE = "org.apache.juddi.client.net";
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

        /// <summary>
        /// throws security exception if not ran as admin
        /// </summary>
        public static void RegisterLogSources()
        {
            Boolean isRegistered = false;
            try {
                isRegistered = System.Diagnostics.EventLog.SourceExists(EVENT_LOG_SOURCE);
            } catch { }
            if (!isRegistered)
            {
                try
                {
                    EventLog.CreateEventSource(EVENT_LOG_SOURCE, EVENT_LOG_SOURCE);
                }catch (Exception ex)
                {
                    throw ex;
                }
            }


        }

        private void write(string msg, EventLogEntryType level)
        {
            try
            {
                using (EventLog log = new EventLog(EVENT_LOG_SOURCE))
                {
                    log.Source = EVENT_LOG_SOURCE;
                    log.WriteEntry(msg, level);
                }
            }
            catch (Exception ex)
            {
                if (Console.Out != null)
                    Console.Out.WriteLine("WARNING, could not log to windows event log, make sure " + EVENT_LOG_SOURCE + " is registered as a log source");
                
            }
        }

        public void info(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                write(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Information);
            if (downstream != null)
                downstream.info(msg);
        }

        public void info(string msg)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                write(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg, EventLogEntryType.Information);
            if (downstream != null)
                downstream.info(msg);
        }

        public void warn(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                write(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Warning);
            if (downstream != null)
                downstream.warn(msg, ex);
        }

        public void warn(string msg)
        {

            if (level.CompareTo(LogLevel.WARN) <= 0)
                write(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg, EventLogEntryType.Warning);
            if (downstream != null)
                downstream.warn(msg);
        }

        public void error(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                write(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Error);
            if (downstream != null)
                downstream.error(msg, ex);
        }

        public void error(string msg)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                write(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg, EventLogEntryType.Error);
            if (downstream != null)
                downstream.error(msg);
        }


        public void debug(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                write(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Information);
            if (downstream != null)
                downstream.debug(msg, ex);
        }

        public void debug(string msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                write(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg, EventLogEntryType.Information);
            if (downstream != null)
                downstream.debug(msg);
        }

        public bool isDebugEnabled()
        {
            return true;
        }


        public void debug(object msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                write(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg.ToString(), EventLogEntryType.Information);
            if (downstream != null)
                downstream.debug(msg);
        }


    }
}
