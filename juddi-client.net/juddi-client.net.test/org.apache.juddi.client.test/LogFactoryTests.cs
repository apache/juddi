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
using NUnit.Framework;
using org.apache.juddi.v3.client;
using System;
using System.Diagnostics;
using System.IO;
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
                Assert.True(elog.Entries.Count > 1);
            }
            catch (Exception ex) {
                Assert.Ignore("test ignored, event log access was probably denied " + ex.Message);
            }
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
                Assert.True(elog.Entries.Count > 1);
            }
            catch (Exception ex) {
                Assert.Ignore("test ignored, event log access was probably denied " + ex.Message);
            }
            
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
