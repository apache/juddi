using NUnit.Framework;
using org.apache.juddi.v3.client.log;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;

namespace juddi_client.net.test
{
    [TestFixture]
    public class LogFactoryTests
    {
        [Test]
        public void testLogFactory()
        {
            Log log = LogFactory.getLog("testLogFactory", "CONSOLE", org.apache.juddi.v3.client.LogLevel.INFO, null);
            Assert.NotNull(log);
            log.info("test");
            log.info("test", new Exception("hi"));
        }

        [Test]
        public void testLogFactory2()
        {
            Log log = LogFactory.getLog("testLogFactory", "CONSOLE,EVENTLOG", org.apache.juddi.v3.client.LogLevel.INFO, null);
            Assert.NotNull(log);
            log.info("test");
            log.info("test", new Exception("hi"));
        }

        [Test]
        public void testLogFactory3()
        {
            if (File.Exists("./testlog.log"))
                File.Delete("./testlog.log");
            Log log = LogFactory.getLog("testLogFactory", "CONSOLE,EVENTLOG,FILE", org.apache.juddi.v3.client.LogLevel.INFO, "./testlog.log");
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
            Log log = LogFactory.getLog("testLogFactory", "FILE", org.apache.juddi.v3.client.LogLevel.INFO, "./testlog.log");
            Assert.NotNull(log);
            log.info("test");
            log.info("test", new Exception("hi"));
            Assert.True(File.Exists("./testlog.log"));
            File.Delete("./testlog.log");
        }

        [Test]
        public void testLogFactory5()
        {

            Log log = LogFactory.getLog("testLogFactory", "EVENTLOG", org.apache.juddi.v3.client.LogLevel.INFO, null);
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
    }
}
