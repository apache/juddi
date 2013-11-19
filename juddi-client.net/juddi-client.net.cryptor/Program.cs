using org.apache.juddi.v3.client.cryptor;
using System;
using System.Collections.Generic;
using System.Linq;

namespace juddi_client.net.cryptor
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.Out.WriteLine("Select cryptographic method (CRTL-C to quit)");
            Console.Out.WriteLine("1) org.apache.juddi.v3.client.cryptor.AES256Cryptor");
            Console.Out.WriteLine("2) org.apache.juddi.v3.client.cryptor.AES128Cryptor");
            Console.Out.WriteLine("3) org.apache.juddi.v3.client.cryptor.TripleDESCryptor");
            Console.Out.Write("Selection > ");
            string cryptor = Console.In.ReadLine();

            int i = -1;
            try
            {
                i = Int32.Parse(cryptor);
            }
            catch { }
            while (i < 1 && i > 3)
            {
                Console.Out.Write("Selection > ");
                i = -1;
                try
                {
                    i = Int32.Parse(cryptor);
                }
                catch { }
            }

            Console.Out.Write("Password to encrypt > ");
            string pwd = ReadPassword('*');
            switch (i)
            {
                case 1:
                    Console.Out.WriteLine(org.apache.juddi.v3.client.cryptor.CryptorFactory.getCryptor(CryptorFactory.AES256).encrypt(pwd));
                    break;
                case 2:
                    Console.Out.WriteLine(org.apache.juddi.v3.client.cryptor.CryptorFactory.getCryptor(CryptorFactory.AES128).encrypt(pwd));
                    break;
                case 3:
                    Console.Out.WriteLine(org.apache.juddi.v3.client.cryptor.CryptorFactory.getCryptor(CryptorFactory.TripleDES).encrypt(pwd));
                    break;
            }

        }

        /// <summary>
        /// Like System.Console.ReadLine(), only with a mask. Source: http://stackoverflow.com/questions/3404421/password-masking-console-application
        /// </summary>
        /// <param name="mask">a <c>char</c> representing your choice of console mask</param>
        /// <returns>the string the user typed in </returns>
        public static string ReadPassword(char mask)
        {
            const int ENTER = 13, BACKSP = 8, CTRLBACKSP = 127;
            int[] FILTERED = { 0, 27, 9, 10 /*, 32 space, if you care */ }; // const

            var pass = new Stack<char>();
            char chr = (char)0;

            while ((chr = System.Console.ReadKey(true).KeyChar) != ENTER)
            {
                if (chr == BACKSP)
                {
                    if (pass.Count > 0)
                    {
                        System.Console.Write("\b \b");
                        pass.Pop();
                    }
                }
                else if (chr == CTRLBACKSP)
                {
                    while (pass.Count > 0)
                    {
                        System.Console.Write("\b \b");
                        pass.Pop();
                    }
                }
                else if (FILTERED.Count(x => chr == x) > 0) { }
                else
                {
                    pass.Push((char)chr);
                    System.Console.Write(mask);
                }
            }

            System.Console.WriteLine();

            return new string(pass.Reverse().ToArray());
        }
    }
}
