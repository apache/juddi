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
package org.apache.juddi.auth;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.cryptor.Cryptor;
import org.apache.juddi.cryptor.CryptorFactory;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnknownUserException;

/**
 * @author Anou Manavalan
 */
public class CryptedXMLDocAuthenticator extends XMLDocAuthenticator
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(CryptedXMLDocAuthenticator.class);

  /**
   *
   */
  public CryptedXMLDocAuthenticator()
  {
    super();
  }

  /**
   *
   */
  public String authenticate(String userID,String credential)
    throws RegistryException
  {
    preProcess(userID,credential);
    String encryptedCredential = encrypt(credential);
    return postProcess(userID, encryptedCredential);
  }

  /**
   *
   */
  private String encrypt(String str) 
    throws RegistryException
  {
    try
    {
      Cryptor cryptor = (Cryptor)CryptorFactory.getCryptor();
  
      return cryptor.encrypt(str);
    }
    catch (InvalidKeyException e)
    {
      log.error("Invalid Key Exception in crypting the password",e);
      throw new RegistryException(e.getMessage());
    }
    catch (NoSuchPaddingException e)
    {
      log.error("Padding Exception in crypting the password",e);
      throw new RegistryException(e.getMessage());
    }
    catch (NoSuchAlgorithmException e)
    {
      log.error("Algorithm Exception in crypting the password",e);
      throw new RegistryException(e.getMessage());
    }
    catch (InvalidAlgorithmParameterException e)
    {
      log.error("Algorithm parameter Exception in crypting the password",e);
      throw new RegistryException(e.getMessage());
    }
    catch (IllegalBlockSizeException e)
    {
      log.error("Block size Exception in crypting the password",e);
      throw new RegistryException(e.getMessage());
    }
    catch (BadPaddingException e)
    {
      log.error("Bad Padding Exception in crypting the password",e);
      throw new RegistryException(e.getMessage());
    }
  }

  /**
   * @param userID
   * @param credential
   * @throws RegistryException
   */
  private void preProcess(String userID, String credential) 
    throws RegistryException
  {
    // a userID must be specified.
    if (userID == null)
      //throw new UnknownUserException("Invalid user ID: "+userID);
      throw new UnknownUserException("Invalid user ID = "+userID);

    // credential (password) must be specified.
    if (credential == null)
      //throw new UnknownUserException("Invalid credentials");
      throw new UnknownUserException("Invalid credentials");
  }

  /**
   * @param userID
   * @param encryptedCredential
   * @return
   * @throws RegistryException
   */
  private String postProcess(String userID, String encryptedCredential) 
    throws RegistryException
  {
    if (userTable.containsKey(userID))
    {
      UserInfo userInfo = (UserInfo)userTable.get(userID);
      if ((userInfo.password == null) || (!encryptedCredential.equals(userInfo.password)))
      throw new UnknownUserException("Invalid credentials");
    }
    else
      throw new UnknownUserException("Invalid user ID: "+userID);

    return userID;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    Authenticator auth = new CryptedXMLDocAuthenticator();

    try {
      System.out.print("anou_mana/password: ");
      auth.authenticate("anou_mana","password");
      System.out.println("successfully authenticated");
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    }

    try {
      System.out.print("anou_mana/badpass: ");
      auth.authenticate("anou_mana","badpass");
      System.out.println("successfully authenticated");
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    }

    try {
      System.out.print("bozo/clown: ");
      auth.authenticate("bozo","clown");
      System.out.println("successfully authenticated");
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    }

    try {
      System.out.print("sviens/password: ");
      auth.authenticate("sviens","password");
      System.out.println("successfully authenticated");
    }
    catch(Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
}
