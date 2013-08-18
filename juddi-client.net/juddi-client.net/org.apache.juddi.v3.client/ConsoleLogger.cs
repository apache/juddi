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
