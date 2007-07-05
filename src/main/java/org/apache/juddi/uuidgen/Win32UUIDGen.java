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

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Used to create new universally unique identifiers or UUID's (sometimes called
 * GUID's).  UDDI UUID's are allways formmated according to DCE UUID conventions.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public final class Win32UUIDGen implements UUIDGen
{
  /**
   *
   */
  public String uuidgen()
  {
    String[] uuids = this.uuidgen(1);
    return uuids[0];
  }

  /**
   *
   */
  public String[] uuidgen(int nmbr)
  {
    String[] uuids = new String[nmbr];

    try
    {
      Runtime r = Runtime.getRuntime();
      Process p = r.exec("uuidgen -n" + nmbr);
      BufferedReader x = new BufferedReader(new InputStreamReader(p.getInputStream()));

      for (int i = 0; i < nmbr; ++i)
        uuids[i] = x.readLine();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      throw new RuntimeException(ex.getMessage());
    }

    return uuids;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
  {
    UUIDGen uuidgen = new Win32UUIDGen();

    long start = System.currentTimeMillis();

    // fast (3705 milliseconds)
    for (int i = 1; i <= 250; ++i)
      System.out.println( i + ":  " + uuidgen.uuidgen());

    long end = System.currentTimeMillis();

    System.out.println("Generation (and display) of 250 UUID's took "+(end-start)+" milliseconds.");


    start = System.currentTimeMillis();

    // much faster (90 milliseconds)
    String[] ids = uuidgen.uuidgen(250);
    for (int i = 1; i < 250; ++i)
      System.out.println( i + ":  " + ids[i]);

    end = System.currentTimeMillis();

    System.out.println("Generation (and display) of 250 UUID's took "+(end-start)+" milliseconds.");
  }
}
