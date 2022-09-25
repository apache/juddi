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
using System.IO;

using System.Security.Cryptography;
using System.Text;

namespace org.apache.juddi.v3.client.cryptor
{
    /// <summary>
    /// 3DES Ciphers
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    [Obsolete("This class should not be used anymore!", false)]
    internal sealed class TripleDESCryptor : Cryptor
    {

        /**
         * TripleDESCryptoServiceProvider
        Legal min key size = 128
        Legal max key size = 192
        Legal min block size = 64
        Legal max block size = 64
         * */
        public string encrypt(string toEncrypt)
        {
            byte[] toEncryptArray = UTF8Encoding.UTF8.GetBytes(toEncrypt);

            // Get the key from config file

            //192 64 s6RDEWiv+mQ= iFGC3Nx1XvUCfDbHTKg8BEPIlJ+oLM7l
            TripleDESCryptoServiceProvider tdes = new TripleDESCryptoServiceProvider();
            tdes.IV = Convert.FromBase64String("s6RDEWiv+mQ=");
            tdes.Key = Convert.FromBase64String("iFGC3Nx1XvUCfDbHTKg8BEPIlJ+oLM7l");

            ICryptoTransform cTransform = tdes.CreateEncryptor();
            //transform the specified region of bytes array to resultArray
            byte[] resultArray =
              cTransform.TransformFinalBlock(toEncryptArray, 0,
              toEncryptArray.Length);
            //Release resources held by TripleDes Encryptor
            tdes.Clear();
            //Return the encrypted data into unreadable string format
            return Convert.ToBase64String(resultArray, 0, resultArray.Length);
        }

        public string decrypt(string str)
        {
            //get the byte code of the string
            byte[] toEncryptArray =  Convert.FromBase64String(str);
            TripleDESCryptoServiceProvider tdes = new TripleDESCryptoServiceProvider();
            tdes.IV = Convert.FromBase64String("s6RDEWiv+mQ=");
            tdes.Key = Convert.FromBase64String("iFGC3Nx1XvUCfDbHTKg8BEPIlJ+oLM7l");


            ICryptoTransform cTransform = tdes.CreateDecryptor();
            byte[] resultArray = cTransform.TransformFinalBlock(
                                 toEncryptArray, 0, toEncryptArray.Length);
            //Release resources held by TripleDes Encryptor                
            tdes.Clear();
            //return the Clear decrypted TEXT
            return UTF8Encoding.UTF8.GetString(resultArray);
        }
    }
}
