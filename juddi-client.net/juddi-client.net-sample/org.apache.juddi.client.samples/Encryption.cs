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
using System.Security.Cryptography;

namespace org.apache.juddi.client.sample
{
    public class Encryption
    {
        public static void main(string[] args)
        {
            AesManaged aes = new AesManaged();
            Console.WriteLine("AesManaged ");
            KeySizes[] ks = aes.LegalKeySizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min key size = " + k.MinSize);
                Console.WriteLine("\tLegal max key size = " + k.MaxSize);
            }
            ks = aes.LegalBlockSizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min block size = " + k.MinSize);
                Console.WriteLine("\tLegal max block size = " + k.MaxSize);
            }


            RijndaelManaged rij = new RijndaelManaged();
            Console.WriteLine("RijndaelManaged ");
            ks = rij.LegalKeySizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min key size = " + k.MinSize);
                Console.WriteLine("\tLegal max key size = " + k.MaxSize);
            }
            ks = rij.LegalBlockSizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min block size = " + k.MinSize);
                Console.WriteLine("\tLegal max block size = " + k.MaxSize);
            }
            TripleDESCryptoServiceProvider tsp = new TripleDESCryptoServiceProvider();
            Console.WriteLine("TripleDESCryptoServiceProvider ");
            ks = tsp.LegalKeySizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min key size = " + k.MinSize);
                Console.WriteLine("\tLegal max key size = " + k.MaxSize);
            }
            ks = tsp.LegalBlockSizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min block size = " + k.MinSize);
                Console.WriteLine("\tLegal max block size = " + k.MaxSize);
            }


            using (RijndaelManaged rijAlg = new RijndaelManaged())
            {
                rijAlg.KeySize = 256;
                rijAlg.BlockSize = 256;
                rijAlg.GenerateKey();
                rijAlg.GenerateIV();
                Console.Out.WriteLine(rijAlg.KeySize + " " + rijAlg.BlockSize + " " + Convert.ToBase64String(rijAlg.IV, Base64FormattingOptions.None) + " " +
                    Convert.ToBase64String(rijAlg.Key, Base64FormattingOptions.None));
            }


            TripleDESCryptoServiceProvider des = new TripleDESCryptoServiceProvider();
            des.KeySize = 192;
            des.BlockSize = 64;
            des.GenerateKey();
            des.GenerateIV();
            Console.Out.WriteLine(des.KeySize + " " + des.BlockSize + " " + Convert.ToBase64String(des.IV, Base64FormattingOptions.None) + " " +
                Convert.ToBase64String(des.Key, Base64FormattingOptions.None));


        }
    }
}
