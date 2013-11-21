using org.apache.juddi.v3.client.log;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace juddi_installer
{
    class Program
    {
        static void Main(string[] args)
        {
            EventLogger.RegisterLogSources();
        }
    }
}
