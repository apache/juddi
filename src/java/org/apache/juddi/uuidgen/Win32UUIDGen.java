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


  public static void main(String argc[])
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
