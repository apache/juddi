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

import org.apache.juddi.client.*;
import org.apache.juddi.datatype.*;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.publisher.PublisherID;
import org.apache.juddi.datatype.business.*;
import org.apache.juddi.datatype.request.*;
import org.apache.juddi.datatype.response.*;
import org.apache.juddi.error.*;
import org.apache.juddi.registry.*;

import java.util.Vector;
import java.io.File;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DeletePublisherSample
{
  public static void main(String[] args)
  {
    RegistryProxy proxy = new RegistryProxy();

    try
    {

      // execute a GetAuthToken request
      AuthToken token = proxy.get_authToken("juddi", "password");
      AuthInfo authInfo = token.getAuthInfo();

      // create a publisher with administrative privileges
      Publisher publisher = new Publisher("BlueNoteIdentifier", "Blue Note", true);
      publisher.setEnabled(true);
      // create a publisher to remove
      Publisher perisher = new Publisher("removeMe", "Remove Me", false);
      // put the Publisher objects into a Vector
      Vector vector = new Vector(2);
      vector.add(publisher);
      vector.add(perisher);

      // make the request
      PublisherDetail detail = proxy.save_publisher(authInfo, vector);

      System.out.println("publishers saved = " + detail.getPublisherVector().size());

      // get an authToken using the publisher with administrative privileges
      token = proxy.get_authToken("BlueNoteIdentifier", "password");
      authInfo = token.getAuthInfo();

      // create a vector of strings containing the
      String publisherID = "removeMe";
      vector = new Vector(1);
      vector.add(publisherID);
      proxy.delete_publisher(authInfo, vector);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}