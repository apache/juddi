/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.juddi.cryptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.Loader;

/**
 * Used to create the org.apache.juddi.cryptor.Cryptor implementation
 * as specified by the 'juddi.cryptor' property. Defaults to
 * org.apache.juddi.cryptor.DefaultCryptor if an implementation is not
 * specified.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public abstract class CryptorFactory
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(CryptorFactory.class);

  // Cryptor property key & default implementation
  private static final String IMPL_KEY = "juddi.cryptor";
  private static final String DEFAULT_IMPL = "org.apache.juddi.cryptor.DefaultCryptor";

  // the shared Cryptor instance
  private static Cryptor cryptor = null;

  /**
   * Returns a new instance of a CryptorFactory.
   *
   * @return Cryptor
   */
  public static Cryptor getCryptor()
  {
    if (cryptor == null)
      cryptor = createCryptor();
    return cryptor;
  }

  /**
   * Returns a new instance of a Cryptor.
   *
   * @return Cryptor
   */
  private static synchronized Cryptor createCryptor()
  {
    if (cryptor != null)
      return cryptor;

    // grab class name of the Cryptor implementation to create
    String className = Config.getStringProperty(IMPL_KEY,DEFAULT_IMPL);

    // write the Cryptor implementation name to the log
    log.debug("Cryptor Implementation = " + className);

    Class cryptorClass = null;
    try
    {
      // Use Loader to locate & load the Cryptor implementation
      cryptorClass = Loader.getClassForName(className);
    }
    catch(ClassNotFoundException e)
    {
      log.error("The specified Cryptor class '" + className +
        "' was not found in classpath.");
      log.error(e);
    }

    try
    {
      // try to instantiate the Cryptor implementation
      cryptor = (Cryptor)cryptorClass.newInstance();
    }
    catch(Exception e)
    {
      log.error("Exception while attempting to instantiate the " +
        "implementation of Cryptor: " + cryptorClass.getName() +
        "\n" + e.getMessage());
      log.error(e);
    }

    return cryptor;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    Cryptor cryptor = CryptorFactory.getCryptor();
    
    String encryptedText = cryptor.encrypt("password");
    System.out.println("EnCrypted text [" + encryptedText + "]");

    String decryptedText = cryptor.decrypt(encryptedText);
    System.out.println("DeCrypted text " + decryptedText);
  }
}