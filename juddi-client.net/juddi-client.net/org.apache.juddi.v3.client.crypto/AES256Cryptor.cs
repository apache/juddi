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

namespace org.apache.juddi.v3.client.crypto
{
    /// <summary>
    /// AES256 Cipher
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    internal sealed class AES256Cryptor : AESCryptor
    {

        protected internal override int GetKeySize()
        {
            return 256;
        }

        protected internal override byte[] GetKey()
        {
            return Convert.FromBase64String("K48QmIsRr0xQD+WOwyg+fJWGS8K1M82V8XKn+/IzPo0=");
        }

        protected internal override byte[] GetIV()
        {
            return Convert.FromBase64String("Ro80zsaX0a4PLtyXuFKq6Q==");
        }
    }
}
