/*
 * Copyright 2013 The Apache Software Foundation.
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
 */
package org.apache.juddi.v3.auth;

import org.apache.juddi.cryptor.Cryptor;
import org.apache.juddi.cryptor.CryptorFactory;
 
/**
 *
 * @author Alex O'Ree
 * @see org.apache.juddi.cryptor.DefaultCryptor
 */
public class CrytorUtil {

    public static void main(String[] args) throws Exception{
        if (args.length == 0) {
            PrintUsage();
            return;
        }
        Cryptor cryptor = CryptorFactory.getCryptor(args[0]);
        System.out.print("Password: ");
        char[] readPassword = System.console().readPassword();
        System.out.println("Cipher: " +  cryptor.encrypt(new String(readPassword)));
    }

    private static void PrintUsage() {
        System.out.println("Encrypts a password using the specified crypto provider");
        System.out.println("Usage: java -cp (classpath) org.apache.juddi.v3.auth.CrytorUtil (CryptoProvider)");
        
        System.out.println("Provided crypto providers:");
        System.out.println("\torg.apache.juddi.cryptor.DefaultCryptor - uses PBEWithMD5AndDES");
    }
}
