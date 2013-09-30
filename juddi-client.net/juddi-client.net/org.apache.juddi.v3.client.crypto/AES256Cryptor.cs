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
using System.Security.Cryptography;
using System.Text;

namespace org.apache.juddi.v3.client.crypto
{
    /// <summary>
    /// AES256 Cipher
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public sealed class AES256Cryptor : AESCryptor
    {

        protected internal override int GetKeySize()
        {
            return 256;
        }

        protected internal override int GetBlockSize()
        {
            return 256;
        }

        public String generateKey()
        {

           /* AesManaged
        Legal min key size = 128
        Legal max key size = 256
        Legal min block size = 128
        Legal max block size = 128*/
            using (RijndaelManaged rijAlg = new RijndaelManaged())
            {
                rijAlg.KeySize = 256;
                rijAlg.BlockSize = 256;
                rijAlg.GenerateKey();
                rijAlg.GenerateIV();
                return rijAlg.KeySize + " " + rijAlg.BlockSize + " " + Convert.ToBase64String(rijAlg.IV, Base64FormattingOptions.None) + " " + 
                    Convert.ToBase64String(rijAlg.Key, Base64FormattingOptions.None);
            }

        }
        protected internal override byte[] GetKey()
        {
            //256 256 
            
            //
            //OI3xpA3ju175rBFDbgNek9fvQOXMhLNpktgm4+mDvvQ=
            //tK47Y1FE1JragvCmanbzsA== 
            //yEt6Jn1rEnFFmWduUEu7fxki31k3/TPOzhzHXrKhd4U=

            //256 
            //gAyHDYd4hwYru2ofV41KEw== 
            //2LsXyePZKqYRyxks/9mXiiMewNo5Ai8KDz8FNSi/OvU=


            //zR5gURV+ZeJ9pzYIymEwkg==
            //xgFJ6zCSBB7OAWo3v2y5H1JO4VYlRyxA5Z4gIOOBUzY=

            //9jjQB84Xx1fA5D0vS8EWqA==
            //RLzwr0D+wnoOPcl4lHPPILPN1TLaH89u0la2+GFWIFY=

            //auWb4YCYAuZQ/joSieu8bg==
            //VlfjcIggVX5QlvwAKzKmaI92q1ADZgop5RRsQUW8sXQ=
            return Convert.FromBase64String("OI3xpA3ju175rBFDbgNek9fvQOXMhLNpktgm4+mDvvQ=");
        }

        protected internal override byte[] GetIV()
        {
            return Convert.FromBase64String("19BWwWbtICJkI04WpBkMBkURJTTRB0gIyUyiVgXcaCw= ");
        }
    }
}
