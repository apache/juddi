using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.log
{
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
                WriteEntry(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Warning);
        }

        public void warn(string msg)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg, EventLogEntryType.Warning);
        }

        public void error(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg + " " + LogHelper.HandleException(ex), EventLogEntryType.Error);
        }

        public void error(string msg)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                WriteEntry(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg, EventLogEntryType.Error);
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
