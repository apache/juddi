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