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
package org.apache.juddi.uuidgen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.juddi.util.Config;

/**
 * Used to create new universally unique identifiers or UUID's
 * (sometimes called GUID's).  UDDI UUID's are allways formmated
 * according to DCE UUID conventions.
 * 
 * @author Steve Viens (sviens@apache.org)
 */
public final class NativeUUIDGen implements UUIDGen
{
  private static final String COMMAND_KEY = "juddi.uuidgenCommand";
  private static final String DEFAULT_COMMAND = "uuidgen";
  private String command = null;
  
  /**
   *
   */
  public NativeUUIDGen()
  {     
        this.command = Config.getStringProperty(COMMAND_KEY,DEFAULT_COMMAND);
  }
  
  /**
   *
   */
  public String uuidgen()
  {
    try
    {
      Runtime r = Runtime.getRuntime();
      Process p = r.exec(command);
      BufferedReader x = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
      
      return x.readLine();
    }    
    catch (IOException e)
    {      
        e.printStackTrace();
    }    
    
    return null;
  }  
  
  /**
   *
   */
  public String[] uuidgen(int nmbr) 
  {    
        String[] uuids = new String[nmbr];
    for (int i=0; i<uuids.length; i++)
      uuids[i] = uuidgen();    
  
    return uuids; 
  }  
        
                
  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/
  
        
  public static void main(String args[])
  {
    UUIDGen generator = new NativeUUIDGen();
    long start = System.currentTimeMillis();

    for (int i = 1; i <= 100; ++i)
        generator.uuidgen();

    long end = System.currentTimeMillis();

    System.out.println("\nNativeUUIDGen: Generation of 100 UUID's took " +
                (end-start)+" milliseconds.");  
  }
}