using NUnit.Framework;
using org.apache.juddi.v3.client;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using org.apache.juddi.v3.client.log;

namespace juddi_client.net.test
{
    [TestFixture]
    public class LogFactoryTests
    {
        [Test]
        public void testLogFactory()
        {
            Log log = LogFactory.getLog("testLogFactory", "CONSOLE", LogLevel.INFO, null);
            Assert.NotNull(log);
            log.info("test");
            log.info("test", new Exception("hi"));
        }

        [Test]
        public void testLogFactory2()
        {
            Assume.That(!IsLinux);
            Log log = LogFactory.getLog("testLogFactory", "CONSOLE,EVENTLOG", LogLevel.INFO, null);
            Assert.NotNull(log);
            log.info("test");
            log.info("test", new Exception("hi"));
        }

        [Test]
        public void testLogFactory3()
        {
            Assume.That(!IsLinux);
            if (File.Exists("./testlog.log"))
                File.Delete("./testlog.log");
            Log log = LogFactory.getLog("testLogFactory", "CONSOLE,EVENTLOG,FILE", LogLevel.INFO, "./testlog.log");
            Assert.NotNull(log);
            log.info("test");
            log.info("test", new Exception("hi"));
            Assert.True(File.Exists("./testlog.log"));
            File.Delete("./testlog.log");
            EventLog elog = null;
            try
            {
                elog = new EventLog(EventLogger.EVENT_LOG_SOURCE);
            }
            catch (Exception ex) { }
            Assume.That(elog != null);
            Assert.True(elog.Entries.Count > 1);
        }

        [Test]
        public void testLogFactory4()
        {
            if (File.Exists("./testlog.log"))
                File.Delete("./testlog.log");
            Log log = LogFactory.getLog("testLogFactory", "FILE", LogLevel.INFO, "./testlog.log");
            Assert.NotNull(log);
            log.info("test");
            log.info("test", new Exception("hi"));
            Assert.True(File.Exists("./testlog.log"));
            File.Delete("./testlog.log");
        }

        [Test]
        public void testLogFactory5()
        {
            Assume.That(!IsLinux);
            Log log = LogFactory.getLog("testLogFactory", "EVENTLOG", LogLevel.INFO, null);
            Assert.NotNull(log);
            log.info("test");
            log.info("test", new Exception("hi"));
            EventLog elog = null;
            try
            {
                elog = new EventLog(EventLogger.EVENT_LOG_SOURCE);
            }
            catch (Exception ex) { }
            Assume.That(elog != null);
            Assert.True(elog.Entries.Count > 1);
        }


        public static bool IsLinux
        {
            get
            {
                int p = (int)Environment.OSVersion.Platform;
                return (p == 4) || (p == 6) || (p == 128);
            }
        }
    }
}
