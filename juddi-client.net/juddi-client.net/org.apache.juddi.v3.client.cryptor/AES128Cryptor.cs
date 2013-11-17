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

namespace org.apache.juddi.v3.client.cryptor
{
    /// <summary>
    /// AES128 Cipher
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    internal sealed class AES128Cryptor : AESCryptor
    {
        protected internal override int GetKeySize()
        {
            return 128;
        }

        protected internal override int GetBlockSize()
        {
            return 128;
        }

        protected internal override byte[] GetKey()
        {
            return Convert.FromBase64String("oT5ljhVnMVQMnV2E7BOj7Q==");

        }

        protected internal override byte[] GetIV()
        {
            return Convert.FromBase64String("xtMa34fpJ9bCdpODQn8GmQ==");
        }
    }
}
