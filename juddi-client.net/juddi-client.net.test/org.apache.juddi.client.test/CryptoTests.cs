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
using org.apache.juddi.v3.client.cryptor;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace juddi_client.net.test
{
    [TestFixture]
    public class CryptoTests
    {
        [Test]
        public void AES128()
        {
            Console.Out.WriteLine("Cryptor AES128");
            Cryptor c = CryptorFactory.getCryptor(CryptorFactory.AES128);
            TestCryptor(c);
        }

        private void TestCryptor(Cryptor c)
        {
            String cipher = c.encrypt("Password");
            Console.Out.WriteLine(cipher);
            Assert.False(String.IsNullOrEmpty(cipher));

            String clear = c.decrypt(cipher);
            Console.Out.WriteLine(clear);
            Assert.False(String.IsNullOrEmpty(clear));
            Assert.AreEqual(clear, "Password");
        }
        [Test]
        public void AES256()
        {
            Console.Out.WriteLine("Cryptor AES256");
            Cryptor c = CryptorFactory.getCryptor(CryptorFactory.AES256);
            TestCryptor(c);
        }

        [Test]
        public void TripleDES()
        {
            Console.Out.WriteLine("Cryptor TripleDES");
            Cryptor c = CryptorFactory.getCryptor(CryptorFactory.TripleDES);
            TestCryptor(c);
        }
    }
}
