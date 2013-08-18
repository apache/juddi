using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;

namespace org.apache.juddi.v3.client.log
{
    public class EventLogger : Log
    {
        EventLog log;
        public EventLogger(String name)
        {
            log = new EventLog("Application","localhost",name);
        }
        public void info(string msg, Exception ex)
        {
            log.WriteEntry(msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Information);
        }

        public void info(string msg)
        {
            log.WriteEntry(msg, EventLogEntryType.Information);
        }

        public void warn(string msg, Exception ex)
        {
            log.WriteEntry(msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Warning);
        }

        public void warn(string msg)
        {
            log.WriteEntry(msg, EventLogEntryType.Warning);
        }

        public void error(string msg, Exception ex)
        {
            log.WriteEntry(msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Error);
        }

        public void error(string msg)
        {
            log.WriteEntry(msg , EventLogEntryType.Error);
        }


        public void debug(string msg, Exception ex)
        {
            log.WriteEntry(msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Information);
        }

        public void debug(string msg)
        {
            log.WriteEntry(msg , EventLogEntryType.Information);
        }

        public bool isDebugEnabled()
        {
            return true;
        }


        public void debug(object msg)
        {
            log.WriteEntry(msg.ToString(), EventLogEntryType.Information);
        }
    }
}
