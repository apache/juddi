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
    this.command =
        Config.getStringProperty(COMMAND_KEY,DEFAULT_COMMAND);
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
                          new InputStreamReader(
                            p.getInputStream()));

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


  public static void main(String argc[])
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