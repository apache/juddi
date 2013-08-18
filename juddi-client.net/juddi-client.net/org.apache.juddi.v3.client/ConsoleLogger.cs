using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.log
{
    public class ConsoleLogger : Log
    {
        private string name;

        public ConsoleLogger(string name)
        {
            // TODO: Complete member initialization
            this.name = name;
        }
        public void info(string msg, Exception ex)
        {
            Console.WriteLine("INFO " + name + " " + msg + " " + LogHelper.HandleException(ex));
        }

        public void info(string msg)
        {
            Console.WriteLine("INFO " + name + " " + msg );
        }

        public void warn(string msg, Exception ex)
        {
         //   Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine("WARN " + name + " " + msg + " " + LogHelper.HandleException(ex));
        }

        public void warn(string msg)
        {
            Console.WriteLine("WARN " + name + " " + msg );
        }

        public void error(string msg, Exception ex)
        {
            Console.WriteLine("ERROR " + name + " " + msg + " " + LogHelper.HandleException(ex));
        }

        public void error(string msg)
        {
            Console.WriteLine("ERROR " + name + " " + msg );
        }

        public void debug(string msg, Exception ex)
        {
            Console.WriteLine("DEBUG " + name + " " + msg + " " + LogHelper.HandleException(ex));
        }

        public void debug(string msg)
        {
            Console.WriteLine("DEBUG " + name + " " + msg);
        }

        public bool isDebugEnabled()
        {
            return true;
        }


        public void debug(object msg)
        {
            Console.WriteLine("DEBUG " + name + " " + msg.ToString());
        }
    }
}
