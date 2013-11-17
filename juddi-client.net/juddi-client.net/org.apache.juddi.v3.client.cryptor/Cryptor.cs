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
    /// The Crpytor interface, used for encrypting and decrypted credentials
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public interface Cryptor
    {



        /// <summary>
        /// Encrypt the string, if unable to encrypt, return null
        /// </summary>
        /// <param name="str"></param>
        /// <returns>encrypted string</returns>
        String encrypt(String str);
        /// <summary>
        /// decrypts the string
        /// </summary>
        /// <param name="str"></param>
        /// <returns>if the password can be decrypted, the decrypted value is returned, otherwise the original value is returned.
        /// In the event that decryption fails, the error message must be logged.</returns>
        String decrypt(String str);

    }
}
