/*/*
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


namespace org.apache.juddi.v3.client.log
{
    /// <summary>
    /// Outputs log data to the console, if available.
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class ConsoleLogger : Log
    {
        private string name;
        private LogLevel level;
        private Log downstream = null;
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

        public void info(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
            if (downstream != null)
                downstream.info(msg, ex);
        }

        public void info(string msg)
        {
            if (level.CompareTo(LogLevel.INFO) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " INFO [" + name + "] " + msg);
            if (downstream != null)
                downstream.info(msg);
        }

        public void warn(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
            if (downstream != null)
                downstream.warn(msg, ex);
        }

        public void warn(string msg)
        {
            if (level.CompareTo(LogLevel.WARN) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " WARN [" + name + "] " + msg);
            if (downstream != null)
                downstream.warn(msg);
        }

        public void error(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
            if (downstream != null)
                downstream.error(msg, ex);
        }

        public void error(string msg)
        {
            if (level.CompareTo(LogLevel.ERROR) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " ERROR [" + name + "] " + msg);
            if (downstream != null)
                downstream.error(msg); ;
        }

        public void debug(string msg, Exception ex)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg + " " + LogHelper.HandleException(ex));
            if (downstream != null)
                downstream.debug(msg, ex);
        }

        public void debug(string msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg);
            if (downstream != null)
                downstream.debug(msg);
        }

        public bool isDebugEnabled()
        {
            return (level.CompareTo(LogLevel.DEBUG) == 0);
        }


        public void debug(object msg)
        {
            if (level.CompareTo(LogLevel.DEBUG) <= 0)
                if (Console.Out != null)
                    Console.WriteLine(DateTime.Now.ToString("o") + " DEBUG [" + name + "] " + msg.ToString());
        }




        
    }
}
