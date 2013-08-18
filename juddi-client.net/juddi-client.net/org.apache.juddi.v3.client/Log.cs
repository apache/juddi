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
using System.Text;

namespace org.apache.juddi.v3.client.log
{
    public interface Log
    {
        void info(string msg, Exception ex);
        void info(string msg);
        void warn(string msg, Exception ex);
        void warn(string msg);
        void error(string msg, Exception ex);
        void error(string msg);
        void debug(string msg, Exception ex);
        void debug(string msg);
        bool isDebugEnabled();
        void debug(Object msg);
       
    }
    public static class LogHelper
    {
       public static string HandleException(Exception ex)
        {
            string msg = "";
            while (ex != null)
            {
                msg = msg + ex.Message + Environment.NewLine + ex.StackTrace + Environment.NewLine;
                ex = ex.InnerException;
            }
            return msg;
        }
    }
}
