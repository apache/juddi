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
package org.apache.juddi.v3.client.cryptor;

/**
 * A static entry point for encrypting text via CLI
 *
 * @since 3.1.5
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * @see CryptorFactory
 */
public class CryptorUtil {

        public static void main(String[] args) throws Exception {
                if (args.length == 0) {
                        PrintUsage();
                        return;
                }
                Cryptor cryptor = CryptorFactory.getCryptor(args[0]);
                if (System.getProperty("generate", "false").equalsIgnoreCase("true")) {
                        System.out.println("Generating new key...");
                        System.out.println(cryptor.newKey());
                } else {
                        System.out.print("Password: ");
                        char[] readPassword = System.console().readPassword();
                        System.out.println("Cipher: " + cryptor.encrypt(new String(readPassword)));
                }
        }

        private static void PrintUsage() {
                System.out.println("Encrypts a password using the specified crypto provider");
                System.out.println("Usage: java (options) -cp (classpath) " + CryptorUtil.class.getCanonicalName() + " (CryptoProvider)");

                System.out.println("Provided crypto providers:");
                System.out.println("\t" + DefaultCryptor.class.getCanonicalName() + " - uses PBEWithMD5AndDES");
                System.out.println("\t" + TripleDESCrytor.class.getCanonicalName() + " - uses TripleDES");
                System.out.println("\t" + AES128Cryptor.class.getCanonicalName() + " - uses AES128");
                System.out.println("\t" + AES256Cryptor.class.getCanonicalName() + " - uses AES256*");
                System.out.println();
                System.out.println("* Requires Unlimited Strength JCE *");
                System.out.println();
                System.out.println("Available options:");
                System.out.println("\t-Dgenerate=true - generates a new encryption key and writes to std out");
                System.out.println("\t-Djuddi.encryptionKeyFile.(providerClassName)=path - encrypts a password using the specified key");
                System.out.println("(e.g. -Djuddi.encryptionKeyFile.TripleDESCrytor,-Djuddi.encryptionKeyFile.AES128Cryptor, etc ");
        }
}
