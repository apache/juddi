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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.error.RegistryException;

/**
 * This is a simple implementation of jUDDI's Authenticator interface.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DefaultAuthenticator implements Authenticator
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(DefaultAuthenticator.class);

  /**
   *
   */
  public DefaultAuthenticator()
  {
  }

  /**
   *
   */
  public String authenticate(String userID,String credential)
    throws RegistryException
  {
    return userID;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    Authenticator auth = new DefaultAuthenticator();

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