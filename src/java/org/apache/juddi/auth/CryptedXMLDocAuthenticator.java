/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
